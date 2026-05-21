#!/usr/bin/env python3
"""
Metriken-Auswertung fuer Der-Grosse-Preis.

Parst die XML-Reports von JaCoCo, PMD und Checkstyle und gibt
eine formatierte Zusammenfassung als GitHub-Actions-Job-Summary aus.

Gemessene Metriken:
  1. Kopplung            - PMD CouplingBetweenObjects + Checkstyle ClassFanOutComplexity / CDAC
  2. Kohaerenz           - PMD GodClass, TooManyMethods, TooManyFields, ExcessiveClassLength
  3. Klassenbeziehungen  - Checkstyle MethodCount + Quellcode-Analyse (extends / implements)
  4. Softwaretestmetriken- JaCoCo Coverage + Surefire Testergebnisse
"""

import os
import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

# Projekt-Wurzelverzeichnis (ein Level hoeher als dieses Skript)
BASE_DIR = Path(__file__).resolve().parent.parent

JACOCO_XML      = BASE_DIR / "target" / "site" / "jacoco" / "jacoco.xml"
PMD_XML         = BASE_DIR / "target" / "pmd.xml"
CHECKSTYLE_XML  = BASE_DIR / "target" / "checkstyle-result.xml"
SUREFIRE_DIR    = BASE_DIR / "target" / "surefire-reports"
SOURCE_DIR      = BASE_DIR / "src" / "main" / "java"


# ---------------------------------------------------------------------------
# Hilfsfunktionen
# ---------------------------------------------------------------------------

def calc_pct(covered: int, missed: int) -> float:
    total = covered + missed
    return round(covered / total * 100, 1) if total else 0.0


def load_xml(path: Path):
    """Laedt eine XML-Datei und gibt das Root-Element zurueck.
    Entfernt DOCTYPE-Deklarationen (z.B. JaCoCo DTD) vor dem Parsen."""
    if not path.exists():
        return None
    try:
        content = path.read_bytes()
        content = re.sub(rb"<!DOCTYPE[^>]*>", b"", content)
        return ET.fromstring(content)
    except ET.ParseError as exc:
        print(f"  [WARN] Konnte {path.name} nicht parsen: {exc}", flush=True)
        return None


# ---------------------------------------------------------------------------
# Report-Parser
# ---------------------------------------------------------------------------

def parse_jacoco() -> dict | None:
    """Softwaretestmetriken: JaCoCo-Coverage-Daten.

    Gemessene Werte:
      LINE   - Anteil der Quellcode-Zeilen, die durch Tests ausgefuehrt werden.
      BRANCH - Anteil der Verzweigungen (if/else, switch, ternary), die abgedeckt sind.
      METHOD - Anteil der Methoden, die mindestens einmal aufgerufen werden.
      CLASS  - Anteil der Klassen mit mindestens einer getesteten Methode.
    """
    root = load_xml(JACOCO_XML)
    if root is None:
        return None

    metrics = {}
    for counter in root.findall("counter"):
        ctype   = counter.get("type", "")
        covered = int(counter.get("covered", 0))
        missed  = int(counter.get("missed", 0))
        metrics[ctype] = {
            "covered": covered,
            "missed":  missed,
            "pct":     calc_pct(covered, missed),
        }
    return metrics or None


def parse_pmd() -> dict | None:
    """Kopplung & Kohaerenz: PMD-Verletzungen.

    Gemessene Werte:
      CouplingBetweenObjects - CBO: Anzahl der Typen, mit denen eine Klasse gekoppelt ist.
      ExcessiveImports       - Anzahl der Import-Anweisungen als Kopplungs-Indikator.
      GodClass               - Erkennt Klassen mit WMC>=47, ATFD>5, TCC<1/3.
      TooManyMethods         - Anzahl der Methoden pro Klasse (> 20 = Warnung).
      TooManyFields          - Anzahl der Felder pro Klasse (> 15 = Warnung).
      ExcessiveClassLength   - Zeilenanzahl der Klasse (> 500 = Warnung).
    """
    root = load_xml(PMD_XML)
    if root is None:
        return None

    by_rule: dict[str, list] = {}
    for file_elem in root.findall("file"):
        fname      = file_elem.get("name", "")
        class_name = Path(fname).stem
        for v in file_elem.findall("violation"):
            rule = v.get("rule", "unknown")
            by_rule.setdefault(rule, []).append({
                "class": class_name,
                "line":  v.get("beginline", "?"),
                "msg":   (v.text or "").strip(),
            })
    return by_rule or {}


def parse_checkstyle() -> dict | None:
    """Kopplung & Klassenbeziehungen: Checkstyle-Metriken.

    Gemessene Werte:
      ClassFanOutComplexity        - Anzahl unterschiedlicher Klassen, die referenziert werden.
      ClassDataAbstractionCoupling - Anzahl instantiierter Klassen innerhalb einer Klasse.
      MethodCount                  - Gesamtanzahl der Methoden pro Klasse.
    """
    root = load_xml(CHECKSTYLE_XML)
    if root is None:
        return None

    results: dict[str, dict] = {}
    for file_elem in root.findall("file"):
        fname      = file_elem.get("name", "")
        class_name = Path(fname).stem
        for error in file_elem.findall("error"):
            source     = error.get("source", "")
            msg        = error.get("message", "")
            check_name = source.rsplit(".", 1)[-1] if "." in source else source

            # Ersten Zahlenwert aus der Nachricht extrahieren
            numbers = re.findall(r"\d+", msg)
            value   = int(numbers[0]) if numbers else None

            results.setdefault(class_name, {})[check_name] = {
                "value": value,
                "msg":   msg,
            }
    return results or {}


def parse_surefire() -> dict | None:
    """Softwaretestmetriken: Testergebnisse aus Surefire-Reports.

    Gemessene Werte:
      total    - Gesamtanzahl der ausgefuehrten Tests.
      passed   - Anzahl bestandener Tests.
      failed   - Anzahl fehlgeschlagener Tests (failures + errors).
      skipped  - Anzahl uebersprungener Tests.
      rate_pct - Testerfolgsrate in Prozent.
    """
    if not SUREFIRE_DIR.exists():
        return None

    total = errors = failures = skipped = 0
    for xml_file in SUREFIRE_DIR.glob("TEST-*.xml"):
        root = load_xml(xml_file)
        if root is None:
            continue
        total    += int(root.get("tests",    0))
        errors   += int(root.get("errors",   0))
        failures += int(root.get("failures", 0))
        skipped  += int(root.get("skipped",  0))

    passed = total - errors - failures - skipped
    return {
        "total":    total,
        "passed":   passed,
        "failed":   failures + errors,
        "skipped":  skipped,
        "rate_pct": calc_pct(passed, failures + errors),
    }


def analyze_inheritance() -> list[dict]:
    """Beziehungen zwischen Klassen: Vererbungs- und Realisierungsbeziehungen.

    Gemessene Werte:
      extends    - Elternklasse (Vererbungsbeziehung, DIT-Beitrag).
      implements - Implementierte Interfaces (Realisierungsbeziehung).
    Analysiert alle .java-Dateien im src/main/java-Verzeichnis.
    """
    if not SOURCE_DIR.exists():
        return []

    results = []
    for java_file in sorted(SOURCE_DIR.rglob("*.java")):
        try:
            content = java_file.read_text(encoding="utf-8", errors="ignore")
        except OSError:
            continue

        # Nur nicht-anonyme Klassen und Interfaces beruecksichtigen
        class_decl = re.search(
            r"\b(?:class|interface|enum)\s+(\w+)"
            r"(?:\s+extends\s+([\w.<>, ]+?))?"
            r"(?:\s+implements\s+([\w.<>, ]+?))?"
            r"\s*[{<]",
            content,
        )
        if not class_decl:
            continue

        class_name = class_decl.group(1)
        raw_extends    = (class_decl.group(2) or "").strip()
        raw_implements = (class_decl.group(3) or "").strip()

        parent     = raw_extends.split("<")[0].strip() if raw_extends else None
        interfaces = (
            [i.split("<")[0].strip() for i in raw_implements.split(",") if i.strip()]
            if raw_implements else []
        )

        if parent or interfaces:
            results.append({
                "class":      class_name,
                "extends":    parent,
                "implements": interfaces,
                "file":       str(java_file.relative_to(SOURCE_DIR)),
            })

    return results


# ---------------------------------------------------------------------------
# Markdown-Report
# ---------------------------------------------------------------------------

STATUS_OK   = "✅"
STATUS_WARN = "⚠️"
STATUS_FAIL = "❌"

def status_icon(value, warn_threshold, fail_threshold=None, invert=False):
    """Gibt ein Status-Icon zurueck basierend auf Schwellenwerten."""
    if value is None:
        return "–"
    if invert:
        # Hoeher ist besser (z.B. Coverage)
        if fail_threshold is not None and value < fail_threshold:
            return STATUS_FAIL
        if value < warn_threshold:
            return STATUS_WARN
        return STATUS_OK
    else:
        # Niedriger ist besser (z.B. Kopplung)
        if fail_threshold is not None and value > fail_threshold:
            return STATUS_FAIL
        if value > warn_threshold:
            return STATUS_WARN
        return STATUS_OK


def generate_markdown(jacoco, pmd, checkstyle, surefire, inheritance) -> str:
    lines = []

    lines.append("# Metriken-Report: Der Grosse Preis\n")
    lines.append("Automatisch generiert durch die CI/CD-Pipeline.\n")

    # ================================================================
    # 1. KOPPLUNG
    # ================================================================
    lines.append("---")
    lines.append("## 1. Kopplung (Coupling)\n")
    lines.append(
        "> **Gemessene Werte:**\n"
        "> - **CBO** *(Coupling Between Objects, PMD)*: Anzahl der Klassen, mit denen eine\n"
        ">   Klasse direkt gekoppelt ist (Attribute, Rueckgabetypen, lokale Variablen).\n"
        ">   Schwellenwert: **> 20** = Warnung.\n"
        "> - **Fan-Out** *(ClassFanOutComplexity, Checkstyle)*: Anzahl unterschiedlicher\n"
        ">   Typen, die eine Klasse referenziert. Schwellenwert: **> 20** = Warnung.\n"
        "> - **CDAC** *(ClassDataAbstractionCoupling, Checkstyle)*: Anzahl der in einer\n"
        ">   Klasse instantiierten Typen. Schwellenwert: **> 7** = Warnung.\n"
        "> - **Imports** *(ExcessiveImports, PMD)*: Hohe Import-Anzahl als Kopplungs-Indikator.\n"
        ">   Schwellenwert: **> 20** Imports = Warnung.\n"
    )

    # CBO aus PMD
    cbo_data: dict[str, int] = {}
    if pmd and "CouplingBetweenObjects" in pmd:
        for v in pmd["CouplingBetweenObjects"]:
            m = re.search(r"\((\d+) depend", v["msg"])
            if m:
                cbo_data[v["class"]] = int(m.group(1))

    # Fan-Out und CDAC aus Checkstyle
    fanout_data: dict[str, int] = {}
    cdac_data:   dict[str, int] = {}
    if checkstyle:
        for cls, checks in checkstyle.items():
            if "ClassFanOutComplexity" in checks:
                val = checks["ClassFanOutComplexity"]["value"]
                if val is not None:
                    fanout_data[cls] = val
            if "ClassDataAbstractionCoupling" in checks:
                val = checks["ClassDataAbstractionCoupling"]["value"]
                if val is not None:
                    cdac_data[cls] = val

    all_coupling = sorted(
        set(list(cbo_data) + list(fanout_data) + list(cdac_data))
    )

    if all_coupling:
        lines.append("| Klasse | CBO (PMD) | Fan-Out (CS) | CDAC (CS) | Status |")
        lines.append("|--------|:---------:|:------------:|:---------:|:------:|")
        for cls in all_coupling:
            cbo    = cbo_data.get(cls)
            fanout = fanout_data.get(cls)
            cdac   = cdac_data.get(cls)
            st_cbo    = status_icon(cbo,    warn_threshold=20, fail_threshold=30)
            st_fanout = status_icon(fanout, warn_threshold=20, fail_threshold=30)
            st_cdac   = status_icon(cdac,   warn_threshold=7,  fail_threshold=15)
            worst = STATUS_FAIL if STATUS_FAIL in (st_cbo, st_fanout, st_cdac) \
                else STATUS_WARN if STATUS_WARN in (st_cbo, st_fanout, st_cdac) \
                else STATUS_OK
            lines.append(
                f"| `{cls}` | {cbo or '–'} {st_cbo} | "
                f"{fanout or '–'} {st_fanout} | "
                f"{cdac or '–'} {st_cdac} | {worst} |"
            )
    else:
        lines.append(
            f"{STATUS_OK} Keine Kopplungs-Verletzungen – alle Klassen unterhalb der Schwellenwerte.\n"
        )

    if pmd and "ExcessiveImports" in pmd:
        lines.append(f"\n**Zu viele Imports (> 20):** {len(pmd['ExcessiveImports'])} Klasse(n)\n")
        for v in pmd["ExcessiveImports"]:
            lines.append(f"- `{v['class']}` (Zeile {v['line']}): {v['msg']}")

    lines.append("")

    # ================================================================
    # 2. KOHAERENZ
    # ================================================================
    lines.append("---")
    lines.append("## 2. Kohärenz (Cohesion)\n")
    lines.append(
        "> **Gemessene Werte:**\n"
        "> - **GodClass** *(PMD)*: WMC (Weighted Methods per Class) ≥ 47 **UND** ATFD\n"
        ">   (Access to Foreign Data) > 5 **UND** TCC (Tight Class Cohesion) < 1/3.\n"
        ">   Zeigt Klassen mit zu vielen Verantwortlichkeiten (Single Responsibility verletzt).\n"
        "> - **TooManyMethods** *(PMD)*: Anzahl der Methoden > 20. Klassen mit vielen\n"
        ">   Methoden haben oft niedrige Kohaerenz.\n"
        "> - **TooManyFields** *(PMD)*: Anzahl der Felder > 15. Zu viele Felder deuten\n"
        ">   auf eine Klasse mit mehreren Verantwortlichkeiten hin.\n"
        "> - **ExcessiveClassLength** *(PMD)*: Zeilenanzahl > 500. Lange Klassen sind\n"
        ">   schwer wartbar und haben meist niedrige Kohaerenz.\n"
        "> - **MethodCount** *(Checkstyle)*: Gesamtanzahl der Methoden inkl. Konstruktoren > 20.\n"
    )

    cohesion_rules = {
        "GodClass":           "God-Class (WMC≥47, ATFD>5, TCC<1/3)",
        "TooManyMethods":     "Zu viele Methoden (> 20)",
        "TooManyFields":      "Zu viele Felder (> 15)",
        "ExcessiveClassLength": "Klasse zu lang (> 500 Zeilen)",
    }

    cohesion_found = False
    if pmd:
        for rule, label in cohesion_rules.items():
            if rule in pmd:
                cohesion_found = True
                violations = pmd[rule]
                lines.append(f"**{STATUS_WARN} {label}:** {len(violations)} Treffer\n")
                lines.append("| Klasse | Zeile | Beschreibung |")
                lines.append("|--------|:-----:|--------------|")
                for v in violations:
                    short_msg = v["msg"][:100] + "…" if len(v["msg"]) > 100 else v["msg"]
                    lines.append(f"| `{v['class']}` | {v['line']} | {short_msg} |")
                lines.append("")

    # MethodCount aus Checkstyle
    if checkstyle:
        mc_rows = [
            (cls, data["MethodCount"]["value"])
            for cls, data in checkstyle.items()
            if "MethodCount" in data and data["MethodCount"]["value"] is not None
        ]
        if mc_rows:
            cohesion_found = True
            mc_rows.sort(key=lambda x: -(x[1]))
            lines.append("**Methoden-Anzahl pro Klasse (Checkstyle MethodCount, Schwellenwert > 20):**\n")
            lines.append("| Klasse | Methoden | Status |")
            lines.append("|--------|:--------:|:------:|")
            for cls, count in mc_rows:
                st = status_icon(count, warn_threshold=20, fail_threshold=40)
                lines.append(f"| `{cls}` | {count} | {st} |")
            lines.append("")

    if not cohesion_found:
        lines.append(
            f"{STATUS_OK} Keine Kohaerenz-Verletzungen – alle Klassen unterhalb der Schwellenwerte.\n"
        )

    # ================================================================
    # 3. BEZIEHUNGEN ZWISCHEN KLASSEN
    # ================================================================
    lines.append("---")
    lines.append("## 3. Beziehungen zwischen Klassen (Class Relationships)\n")
    lines.append(
        "> **Gemessene Werte:**\n"
        "> - **Vererbung (extends)**: Direkte Elternklasse einer Klasse.\n"
        ">   Tiefe der Vererbungshierarchie (DIT) beeinflusst Wiederverwendbarkeit\n"
        ">   und Testbarkeit. Empfehlung: DIT ≤ 5.\n"
        "> - **Realisierung (implements)**: Implementierte Interfaces.\n"
        ">   Viele Interfaces = hoehere Kopplung an Vertrag.\n"
        "> - **Fan-Out** *(ClassFanOutComplexity)*: Beziehungen durch Verwendung\n"
        ">   anderer Klassen (Abhaengigkeitsbeziehungen).\n"
        "> - **Quelle**: Statische Analyse der Java-Quelldateien in src/main/java.\n"
    )

    if inheritance:
        # Tiefe der Vererbung berechnen (einfache Heuristik: Kette verfolgen)
        extends_map = {
            item["class"]: item["extends"]
            for item in inheritance if item["extends"]
        }

        def compute_dit(cls: str, visited: set | None = None) -> int:
            if visited is None:
                visited = set()
            if cls in visited or cls not in extends_map:
                return 0
            visited.add(cls)
            return 1 + compute_dit(extends_map[cls], visited)

        lines.append("**Vererbungs- und Realisierungsbeziehungen:**\n")
        lines.append(
            "| Klasse | Erbt von (`extends`) | Implementiert (`implements`) | DIT |"
        )
        lines.append(
            "|--------|----------------------|------------------------------|:---:|"
        )
        for item in inheritance:
            parent = f"`{item['extends']}`" if item["extends"] else "–"
            ifaces = (
                ", ".join(f"`{i}`" for i in item["implements"])
                if item["implements"] else "–"
            )
            dit = compute_dit(item["class"])
            st  = status_icon(dit, warn_threshold=3, fail_threshold=5)
            lines.append(f"| `{item['class']}` | {parent} | {ifaces} | {dit} {st} |")

        total_ext  = sum(1 for i in inheritance if i["extends"])
        total_impl = sum(1 for i in inheritance if i["implements"])
        lines.append(
            f"\n**Gesamt:** {len(inheritance)} Klassen mit Beziehungen, "
            f"{total_ext} Vererbungen, {total_impl} Interface-Implementierungen.\n"
        )
    else:
        lines.append("_Keine Vererbungs- oder Interface-Beziehungen in den Quellen gefunden._\n")

    # ================================================================
    # 4. SOFTWARETESTMETRIKEN
    # ================================================================
    lines.append("---")
    lines.append("## 4. Softwaretestmetriken (Test Metrics)\n")
    lines.append(
        "> **Gemessene Werte:**\n"
        "> - **Line Coverage**: Anteil der Quellcode-Zeilen, die durch mindestens\n"
        ">   einen Test ausgefuehrt werden. Ziel: **≥ 70 %**.\n"
        "> - **Branch Coverage**: Anteil der Verzweigungen (if/else, switch, ternary),\n"
        ">   die durch Tests abgedeckt sind. Ziel: **≥ 60 %**.\n"
        "> - **Method Coverage**: Anteil der Methoden, die mindestens einmal aufgerufen\n"
        ">   werden. Ziel: **≥ 80 %**.\n"
        "> - **Class Coverage**: Anteil der Klassen mit mindestens einer getesteten\n"
        ">   Methode. Ziel: **≥ 80 %**.\n"
        "> - **Testerfolgsrate**: Anteil der bestandenen Tests an der Gesamtzahl.\n"
        ">   Ziel: **100 %** (kein fehlgeschlagener Test).\n"
        "> - **Quelle**: JaCoCo-Instrumentierung waehrend Maven-Test-Lauf.\n"
    )

    if jacoco:
        lines.append("**JaCoCo Code-Coverage:**\n")
        lines.append(
            "| Metrik | Abgedeckt | Nicht abgedeckt | Coverage | Ziel |"
        )
        lines.append(
            "|--------|:---------:|:---------------:|:--------:|:----:|"
        )
        display = [
            ("LINE",   "Zeilen (Lines)",         70, 40),
            ("BRANCH", "Verzweigungen (Branches)", 60, 30),
            ("METHOD", "Methoden (Methods)",       80, 50),
            ("CLASS",  "Klassen (Classes)",        80, 50),
        ]
        for metric_key, label, good, warn in display:
            if metric_key in jacoco:
                d   = jacoco[metric_key]
                pct = d["pct"]
                st  = STATUS_OK if pct >= good else (STATUS_WARN if pct >= warn else STATUS_FAIL)
                lines.append(
                    f"| {label} | {d['covered']} | {d['missed']} "
                    f"| {st} **{pct} %** | ≥ {good} % |"
                )
        lines.append("")
    else:
        lines.append(f"{STATUS_WARN} JaCoCo-Report nicht gefunden (`target/site/jacoco/jacoco.xml`).\n")

    if surefire:
        total   = surefire["total"]
        passed  = surefire["passed"]
        failed  = surefire["failed"]
        skipped = surefire["skipped"]
        rate    = surefire["rate_pct"]
        st      = STATUS_OK if failed == 0 else STATUS_FAIL

        lines.append("**Testergebnisse (Surefire):**\n")
        lines.append(
            "| Gesamt | Bestanden | Fehlgeschlagen | Uebersprungen | Erfolgsrate |"
        )
        lines.append(
            "|:------:|:---------:|:--------------:|:-------------:|:-----------:|"
        )
        lines.append(
            f"| {total} | {passed} | {failed} | {skipped} | {st} **{rate} %** |"
        )
        lines.append("")
    else:
        lines.append(
            f"{STATUS_WARN} Surefire-Reports nicht gefunden (`target/surefire-reports/`).\n"
        )

    lines.append("---")
    lines.append(
        "_Report erzeugt von `scripts/metrics-summary.py` · "
        "Tools: JaCoCo, PMD, Checkstyle_\n"
    )

    return "\n".join(lines)


# ---------------------------------------------------------------------------
# Hauptprogramm
# ---------------------------------------------------------------------------

def main():
    print("=== Metriken-Auswertung ===", flush=True)

    jacoco      = parse_jacoco()
    pmd         = parse_pmd()
    checkstyle  = parse_checkstyle()
    surefire    = parse_surefire()
    inheritance = analyze_inheritance()

    print(f"  JaCoCo:            {'gefunden' if jacoco      else 'nicht gefunden'}", flush=True)
    print(f"  PMD:               {'gefunden' if pmd         else 'nicht gefunden'}", flush=True)
    print(f"  Checkstyle:        {'gefunden' if checkstyle  else 'nicht gefunden'}", flush=True)
    print(f"  Surefire:          {'gefunden' if surefire    else 'nicht gefunden'}", flush=True)
    print(f"  Vererbungsanalyse: {len(inheritance)} Klassen mit Beziehungen", flush=True)

    summary = generate_markdown(jacoco, pmd, checkstyle, surefire, inheritance)

    summary_file = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary_file:
        with open(summary_file, "a", encoding="utf-8") as fh:
            fh.write(summary)
        print("Summary in GitHub Step Summary geschrieben.", flush=True)
    else:
        print("\n" + summary, flush=True)


if __name__ == "__main__":
    main()
