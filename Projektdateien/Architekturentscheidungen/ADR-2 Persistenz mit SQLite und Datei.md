# Persistenz: SQLite für Konfiguration, Datei (JSON) für Spielstände

## Kontext und Problemstellung

Die Anwendung benötigt dauerhafte Speicherung für zwei unterschiedliche Datenarten:

1. Spielkonfigurationen mit Kategorien und Fragen, die häufig wiederverwendet und ggf. durchsucht oder verändert werden.

2. Temporäre Informationen zum aktuellen Spiel (Teams, aktueller Spielstand), die hauptsächlich während eines Spielabends benötigt werden.
Es musste entschieden werden, wie diese Daten persistent gespeichert werden.


## Betrachtete Varianten

* Alle Daten nur in Dateien (z.B. JSON oder XML) speichern
* Alle Daten in einer relationalen Datenbank (z.B. SQLite) speichern
* Konfigurationen in SQLite speichern, Spielstände in einer Datei (z.B. JSON)

## Entscheidung

Gewählte Variante: „Konfigurationen in SQLite, Spielstände in Datei (JSON)“, denn Konfigurationen profitieren von der Struktur und Abfragemöglichkeiten einer Datenbank, während Spielstände eher leichtgewichtige, temporäre Daten sind, die einfach in eine Datei geschrieben und bei Bedarf als Backup wieder geladen werden können.


## Status
Angenommen

## Konsequenzen

* Gut, weil SQLite eine strukturierte und zuverlässige Speicherung von Kategorien und Fragen ermöglicht (z.B. spätere Filter-, Such- oder Sortierfunktionen).
* Gut, weil eine einfache Datei (z.B. JSON) für Spielstände leicht zu erstellen, zu sichern (Backups) und wiederherzustellen ist.
* Gut, weil SQLite als eingebettete Datenbank ohne separaten Server auskommt und gut für Desktop-Anwendungen geeignet ist.
* Schlecht, weil zwei unterschiedliche Persistenzmechanismen gepflegt werden müssen (Datenbank + Dateiformat).
* Schlecht, weil die Datenbankstruktur für Konfigurationen bei späteren Erweiterungen (z.B. neue Spielmodi) mit Migrationsaufwand angepasst werden muss.

