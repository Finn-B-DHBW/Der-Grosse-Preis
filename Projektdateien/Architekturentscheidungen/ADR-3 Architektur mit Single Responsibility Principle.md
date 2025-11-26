# Architektur mit mehreren Klassen nach Single Responsibility Principle (SRP)

## Kontext und Problemstellung

Die Anwendung soll langfristig wartbar und erweiterbar sein, z.B. um neue Spielmodi wie Aufgaben oder Challenges neben klassischen Fragen zu unterstützen. Eine monolithische Klasse, die alle Aufgaben übernimmt (UI, Spiellogik, Persistenz), wäre schwer testbar, unübersichtlich und schlecht erweiterbar. Es musste entschieden werden, wie die Struktur des Codes gestaltet wird.


## Betrachtete Varianten

* Eine zentrale „God-Class“, die UI, Spiellogik und Datenzugriff bündelt
* Aufteilung in einige wenige große Schichten (z.B. UI-Schicht und Logik-Schicht) mit gemischten Verantwortlichkeiten
* Aufteilung in mehrere spezialisierte Klassen mit klaren Verantwortlichkeiten nach dem Single Responsibility Principle (SRP)

## Entscheidung

Gewählte Variante: „Aufteilung in mehrere spezialisierte Klassen nach SRP“, denn jede Klasse soll eine klar abgegrenzte Aufgabe haben (z.B. UI-Ansichten, Spiellogik, Persistenz), was die Wartbarkeit und Erweiterbarkeit verbessert.


## Status
Angenommen

## Konsequenzen

* Gut, weil Änderungen (z.B. an der Punktelogik oder an der Darstellung der Spielansicht) in klar abgegrenzten Klassen vorgenommen werden können, ohne die gesamte Anwendung zu gefährden.
* Gut, weil die Anwendung leichter testbar wird: Logikklassen können unabhängig von der UI getestet werden.
* Gut, weil neue Funktionen wie ein zusätzlicher Spielmodus (z.B. Aufgaben/Challenges) durch Hinzufügen neuer Klassen und Anpassung weniger Schnittstellen integriert werden können.
* Schlecht, weil die Anzahl der Klassen und die Komplexität der Struktur steigt und das Team sich an Namenskonventionen und klare Schnittstellen halten muss.
* Schlecht, weil für kleine Änderungen manchmal mehrere Klassen angepasst werden müssen, was mehr Abstimmung im Team erfordert.



