# Projektname
## Softwareanforderungen

### 1. Einleitung

#### 1.1 Übersicht
Die Software „Großer Preis Spielverwaltung“ dient der digitalen Durchführung des Gruppenspiels „Der große Preis“ in Jugendgruppen. Sie ersetzt die bisherige, fehleranfällige PowerPoint-Lösung durch eine stabile und nutzerfreundliche Java-Anwendung.

Mit einer klar strukturierten Oberfläche ermöglicht sie die einfache Erstellung, Verwaltung und Präsentation von Fragen und Spielrunden. Durch optionale Module wie eine integrierte Fragensammlung oder KI-basierte Fragengenerierung wird die Vorbereitung künftiger Spiele erheblich vereinfacht.

Die Hauptvorteile liegen in der Zeitersparnis, Reduktion von Bedienfehlern und der Wiederverwendbarkeit von Inhalten. Damit bietet die Software eine moderne, effiziente und flexible Alternative für Gruppenleiter, die Spiele intuitiv und ohne technischen Aufwand organisieren möchten.

#### 1.2 Geltungsbereich
Dieses Dokument beschreibt die funktionalen und nicht-funktionalen Anforderungen an die Software „Großer Preis Spielverwaltung“. Die Spezifikation bezieht sich auf das vollständige System, mit Fokus auf die Kernfunktionen: Erstellung, Konfiguration, Durchführung und Verwaltung von Quizspielen für Gruppen.

Erweiterungen wie KI-basierte Fragengenerierung, eine zentrale Frage- und Spieldatenbank, sowie die Option, Spiele zu teilen oder in eine Online-Spieledatenbank hochzuladen, werden im Dokument als zukünftige, nicht im Basisumfang enthaltene Funktionen aufgeführt.

Die Konfiguration der Software darf Online-Zugriff nutzen, das eigentliche Spiel soll jedoch ohne Internetverbindung funktionieren, um den Einsatz auch bei fehlender Netzabdeckung zu ermöglichen.

Das Dokument bezieht alle relevanten funktionalen und nicht-funktionalen Anforderungen sowie Schnittstellen ein, soweit sie für die Grundfunktionalität notwendig sind. Details zum Software-Design oder zur Implementierung sowie ausführliche technische Dokumentationen sind nicht Bestandteil dieses Dokuments.

Die Anforderungen gelten universell, um die Nutzung der Software über den Kreis der Jugendgruppe hinaus allen interessierten Nutzern zu ermöglichen. Spezifische Anforderungen an zukünftige Erweiterungen, mobile oder webbasierte Versionen werden in späteren Dokumenten behandelt.

#### 1.3 Definitionen, Akronyme und Abkürzungen
- Großer Preis Spielverwaltung: Die zu entwickelnde Java-Software zur Verwaltung und Präsentation des Spiels „Dein großer Preis“.

- KI: Künstliche Intelligenz, Technologien zur automatischen Generierung von Fragen und Aufgaben.

- SRS: Software Requirements Specification, das Anforderungsdokument für die Software.

- RUP: Rational Unified Process, ein Vorgehensmodell für die Softwareentwicklung.

- PowerPoint: Microsoft-Software zur Erstellung von Präsentationen, die bisher zur Durchführung des Spiels verwendet wird.

- Datenbank: Struktur zur Speicherung von fertigen Fragen und Spielkonfigurationen.

- Spielrunde: Ein kompletter Durchlauf des Spiels mit mehreren Fragen und Teams.

#### 1.4 Referenzen
- IEEE Std 830-1998: Standard für Software Requirements Specifications, Institute of Electrical and Electronics Engineers, 1998.
- Rational Unified Process (RUP) Dokumentation, IBM, 2024.
- UML-Spezifikationen der Object Management Group (OMG), 2023.
- Projektinterne Jira-Backlog- und User Stories des DHBW-Projekts „Der Große Preis“. ([Jira](https://dhbw-se.atlassian.net/jira/software/projects/DGP/boards/1))
- GitHub Repository „Der Große Preis“ zur Software- und Dokumentationsversionierung. ([GitHub](https://github.com/Finn-B-DHBW/Der-Grosse-Preis))


### 2. Funktionale Anforderungen

#### 2.1 Übersicht
Die Software „Großer Preis Spielverwaltung“ ermöglicht es, Quizspiele für Jugendgruppen digital zu erstellen, zu konfigurieren und durchzuführen. Benutzer können Spielrunden anlegen, Fragen auswählen oder hinzufügen, Teams bilden und die Spielabläufe steuern. Das System unterstützt dabei sowohl die Vorbereitung durch den Spielersteller als auch das aktive Spielen durch die Teilnehmer. Die Anwendung bietet eine intuitive Benutzeroberfläche und speichert Konfigurationen für eine einfache Wiederverwendung. Erweiterte Funktionen wie eine Fragedatenbank und KI-generierte Fragen sind als zukünftige Optionen vorgesehen.

Das Anwendungsfalldiagramm zeigt die drei Hauptakteure — Spielersteller, Spielleiter und Spieler — und deren Interaktionen mit dem System. Die Rollen Spielersteller und Spielleiter können von derselben Person übernommen werden.

Die Spielersteller konfigurieren das Spiel, legen Fragen und Parameter fest, während der Spielleiter Spielrunden startet und überwacht. Die Spieler nehmen aktiv am Spiel teil, indem sie Fragen beantworten und Punkte erzielen. Erweiterte Funktionen wie die Nutzung einer Datenbank oder KI-basierte Fragengenerierung sind als optionale Erweiterungen modelliert.

![UML-UseCase-Diagramm](https://github.com/Finn-B-DHBW/Der-Grosse-Preis/raw/c88e13f35f9df5fadff18ee8408522f71886e5ab/Projektdateien/DPG%20UML%20Use%20Case.png)


#### 2.2 Spielkonfiguration erstellen und verwalten
**Beschreibung:**  
Dieser Use Case umfasst alle Aktivitäten, die notwendig sind, um eine neue Spielkonfiguration anzulegen, Parameter festzulegen (Kategorien, Schwierigkeitsgrade, Fragetypen), Fragen hinzuzufügen, und die Konfiguration zu speichern oder zu exportieren. Erweiterte Funktionen wie Fragen aus einer Datenbank laden oder per KI generieren sind optionale Erweiterungen.

![UML-Aktivitätsdiagramm](https://github.com/Finn-B-DHBW/Der-Grosse-Preis/raw/6a39dbcd260a13a8efae93bbc7ec5bf37c2f173c/Projektdateien/UML%20Diagramme/Aktivit%C3%A4tsdiagramm%20Kategorie%20erstellen.jpg)

![UML-Aktivitäsdiagramm](https://github.com/Finn-B-DHBW/Der-Grosse-Preis/raw/12812cdfacce513972e8b54168a4c5ade3ca327a/Projektdateien/UML%20Diagramme/Aktivit%C3%A4tsdiagramm%20Spielkonfigurationen%20verwalten.png)

![Sequenzdiagramm](https://github.com/Finn-B-DHBW/Der-Grosse-Preis/raw/12812cdfacce513972e8b54168a4c5ade3ca327a/Projektdateien/UML%20Sequenzendiagramm/SequenzDiagramm_Denis.png)

**User Stories:**  

- Als Spielersteller möchte ich eine neue Spielkonfiguration anlegen, um das Spiel individuell anzupassen.  
- Als Nutzer (vorbereitender Spielleiter) möchte ich bei der Konfiguration die Anzahl der Kategorien und Schwierigkeitsstufen selbst bestimmen, damit ich steuern kann, wie lang das Spiel gehen wird. ([Jira DGP-19](https://dhbw-se.atlassian.net/browse/DGP-19))
- Als Spielersteller möchte ich fertige Fragen aus einer Datenbank laden können.
- Als Nutzer (vorbereitender Spielleiter) möchte ich mein vorbereitetes konfiguriertes Spiel speichern, damit ich Tage später, auf das fertig vorbereitete Spiel zugreifen kann. ([Jira DGP-15](https://dhbw-se.atlassian.net/browse/DGP-15))

**Voraussetzungen:**  
- Der Nutzer ist als Spielersteller angemeldet.  
- Keine aktive Spielrunde läuft.

**Nachbedingungen:**  
- Es besteht eine gespeicherte und wiederverwendbare Spielkonfiguration.

**Geschätzter Aufwand:** Mittel


#### 2.3 Spielvorbereitung und Spielstart
**Beschreibung:**  
Der Spielleiter legt die Spielparameter fest und lädt eine bestehende Konfiguration. Entweder erstellt er die Teams oder jedes Team nimmt am Spiel über ein Smartphone teil. Anschließend wird das Spiel gestartet.

![UML-Sequenzdiagramm](https://github.com/Finn-B-DHBW/Der-Grosse-Preis/blob/6a39dbcd260a13a8efae93bbc7ec5bf37c2f173c/Projektdateien/UML%20Sequenzendiagramm/Sequenzendiagramm_Use_Case_2.3.jpeg)

**User Stories:**  
- Als Spielleiter möchte ich Teams erstellen, damit Spieler zusammen spielen können.  
- Als Spieler möchte ich mein Team über das Smartphone im Spiel registrieren. 
- Als Spielleiter möchte ich bestehende Konfigurationen laden, um Zeit zu sparen.

**UI-MockUp:**

![UI_MockUP_GruppeErstellen_Mobil](https://github.com/Finn-B-DHBW/Der-Grosse-Preis/raw/c88e13f35f9df5fadff18ee8408522f71886e5ab/Projektdateien/Figma%20GUI%20Mockups/Gruppen%20erstellen%20ansicht%20Handy.png)

**Voraussetzungen:**  
- Spielkonfiguration ist vorhanden.  
- Spieler sind in Teams eingeteilt.

**Nachbedingungen:**  
- Das Spiel ist gestartet und das System wechselt in den Spielmodus.

**Geschätzter Aufwand:** Niedrig


#### 2.4 Spiel durchführen

**Beschreibung:**  
Das System zeigt Fragen an, Spieler wählen Antworten aus. Der Spielleiter oder das System vergibt Punkte und steuert den Spielverlauf.

![UML-Aktivitätsdiagramm](https://github.com/Finn-B-DHBW/Der-Grosse-Preis/raw/6a39dbcd260a13a8efae93bbc7ec5bf37c2f173c/Projektdateien/UML-Aktivitaetdiagramm/Aktivitaetdiagramm_Frage.jpg)

**User Stories:**  
- Als Nutzer (aktiver Spieler) möchte ich eine Übersicht mit allen Kategorien und Schwierigkeitsstufen sehen, damit ich mir überlegen kann, welche Frage ich auswählen möchte. ([Jira DGP-18](https://dhbw-se.atlassian.net/browse/DGP-18))  
- Als Nutzer (aktiver Spielleiter) möchte ich das von den Spielern gewünschte Feld (Kategorie und Schwierigkeitsstufe) anklicken, damit die zugehörige Frage oder Aufgabe angezeigt wird. ([Jira DGP-17](https://dhbw-se.atlassian.net/browse/DGP-17))
- Als Spielleiter möchte ich Punkte vergeben und den Spielstand sehen.
- Als Nutzer (aktiver Mitspieler) möchte ich im Spiel live die aktuelle Punktzahl der Teams sehen, damit ich weiß, ob wir gewinnen. ([Jira DGP-16](https://dhbw-se.atlassian.net/browse/DGP-16))

**UI-MockUps:**

- Desktop-Ansicht Fragenübersicht / Fragenauswahl

![UI_MockUp_Fragentabelle](https://github.com/Finn-B-DHBW/Der-Grosse-Preis/raw/c88e13f35f9df5fadff18ee8408522f71886e5ab/Projektdateien/Figma%20GUI%20Mockups/Auswahl%20thema%20und%20Punkte%20Desktop.png)

- Desktop-Ansicht Frage mit Antwortmöglichkeiten

![UI_MockUp_Fragenansicht](https://github.com/Finn-B-DHBW/Der-Grosse-Preis/raw/c88e13f35f9df5fadff18ee8408522f71886e5ab/Projektdateien/Figma%20GUI%20Mockups/Ausgewaehlte%20Frage%20screen%20Desktop.png)

- Mobile Ansicht Fragenübersicht / Fragenauswahl

![UI_MockUp_Fragenauswahl_Mobil](https://github.com/Finn-B-DHBW/Der-Grosse-Preis/raw/c88e13f35f9df5fadff18ee8408522f71886e5ab/Projektdateien/Figma%20GUI%20Mockups/Auswahl%20thema%20und%20Punkte%20Handy.png)

- Endstand

![UI_MockUp_Endstand](https://github.com/Finn-B-DHBW/Der-Grosse-Preis/raw/c88e13f35f9df5fadff18ee8408522f71886e5ab/Projektdateien/Figma%20GUI%20Mockups/Endstand%20Screen%20Desktop.png)

**Voraussetzungen:**  
- Das Spiel wurde gestartet.  
- Teams und Spieler sind definiert.

**Nachbedingungen:**  
- Spielstand ist aktualisiert.  
- Das Spiel kann fortgesetzt oder beendet werden.

**Geschätzter Aufwand:** Hoch


#### 2.5 Datenverwaltung und Speicherung

**Beschreibung:**  
Das System speichert Spielkonfigurationen und Spielstände persistent in einer Datenbank oder einem Speichermedium. Es ermöglicht das Laden und Exportieren von Daten.

**User Stories:**  
- Als Nutzer möchte ich Spielstände speichern und laden, um Spiele später fortsetzen zu können.  
- Als Nutzer möchte ich Konfigurationen exportieren und importieren.

**Voraussetzungen:**  
- Mindestens eine Spielkonfiguration oder ein Spielstand existiert.

**Nachbedingungen:**  
- Daten sind persistent gespeichert und für weitere Sitzungen verfügbar.

**Geschätzter Aufwand:** Mittel


### 3. Nicht-funktionale Anforderungen

- **Benutzerfreundlichkeit:** Die Oberfläche ist einfach und intuitiv bedienbar, besonders für Nutzer ohne technische Vorkenntnisse. Während des Spiels soll die Reaktion der Software ohne Verzögerungen erfolgen.

- **Zuverlässigkeit:** Die Software darf beim Laden von Spielkonfigurationen mehrere Sekunden brauchen, während eines aktiven Spiels dürfen keine nennenswerten Verzögerungen auftreten. Datenverluste sind zu vermeiden.

- **Leistung:** Ladezeiten für Spielkonfigurationen sind akzeptabel, der Spielbetrieb erfolgt in Echtzeit ohne spürbare Verzögerungen.

- **Installationsaufwand:** Die Software soll möglichst ohne Installation laufen oder maximal ein einmaliges Setup benötigen, das wir bereitstellen.

- **Wartbarkeit:** Der modulare Aufbau erleichtert Erweiterungen und Fehlerbehebung.

- **Sicherheit:** Es werden keine sensiblen personenbezogenen Daten gespeichert. Onlineverbindungen sind verschlüsselt.

- **Flexibilität:** Die Software läuft auf allen Systemen mit Java-Laufzeitumgebung.


### 4. Technische Einschränkungen
- Die Software basiert auf Java und benötigt mindestens eine installierte Java-Laufzeitumgebung (JRE) Version 8 oder höher.

- Die Anwendung soll plattformunabhängig laufen und wird sowohl unter Windows, macOS als auch Linux getestet.

- Das System benötigt keine dedizierten Server, da es vor allem lokal verwendet wird. Online-Zugriff erfolgt optional und ist von der Verfügbarkeit eines Internets abhängig.

- Für den Online-Zugriff wird keine spezielle Serverinfrastruktur vorausgesetzt; stattdessen werden öffentliche APIs und Cloud-Dienste genutzt.

- Die Software wird unter einer Open-Source-Lizenz entwickelt, die die Nutzung, Änderung und Weitergabe regelt (z.B. MIT oder GPL).

- Es gibt eine technische Einschränkung beim erstmaligen Laden von Spielkonfigurationen, da diese einige Sekunden in Anspruch nehmen können. Während des Spiels sollen keine Verzögerungen erkennbar sein.

- Die Software erfordert möglichst keine Installation, darf aber ein einmaliges Setup zur einfachen Einrichtung bereitstellen.