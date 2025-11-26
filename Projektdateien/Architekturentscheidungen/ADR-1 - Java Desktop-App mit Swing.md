# Java-Desktop-Anwendung mit Swing für UI

## Kontext und Problemstellung

Die Anwendung „Dein großer Preis“ soll Gruppenleitern eine einfache Möglichkeit bieten, Spiele zu konfigurieren und durchzuführen. Die UI muss leicht verständlich, offline nutzbar und auf typischen Laptops lauffähig sein. Es musste entschieden werden, mit welcher Technologie die Benutzeroberfläche umgesetzt wird (z.B. Web vs. Desktop, JavaFX vs. Swing).

## Betrachtete Varianten

* Java Desktop-Anwendung mit Swing
* Java Desktop-Anwendung mit JavaFX
* Webanwendung (HTML/CSS/JavaScript im Browser)

## Entscheidung

Gewählte Variante: „Java Desktop-Anwendung mit Swing“, denn das Team hat bereits Erfahrung mit Java und Swing aus der Vorlesung, kann vorhandenes Wissen wiederverwenden und die UI ohne zusätzlichen Technologie-Stack umsetzen. Außerdem lässt sich eine Swing-Anwendung einfach als ausführbare Desktop-App bereitstellen und offline nutzen.

## Status
Angenommen

## Konsequenzen

* Gut, weil vorhandene Kenntnisse aus der Vorlesung genutzt werden und das Team produktiv entwickeln kann.
* Gut, weil eine Desktop-Anwendung ohne Server offline betrieben werden kann und damit gut zu typischen Spieleabenden mit Beamer passt.
* Gut, weil Java-Swing-Anwendungen auf verschiedenen Betriebssystemen laufen können, sofern eine passende Java-Laufzeitumgebung vorhanden ist.
* Schlecht, weil Swing im Vergleich zu moderneren UI-Frameworks (z.B. JavaFX oder Web-Frameworks) eingeschränkte Möglichkeiten für moderne Oberflächen bietet.
* Schlecht, weil UI-Layouting in Swing teilweise aufwendig ist und zusätzliche Sorgfalt für eine gute Benutzbarkeit erforderlich macht.
