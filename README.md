# Star Wars Inventar

Android-App (Kotlin, Jetpack Compose) zur Verwaltung von Artikeln und Verträgen für ein Theaterstück im Star-Wars-Universum. Die Oberfläche ist als Datapad gestaltet.

## Starten

1. ZIP entpacken (am besten in einen kurzen Pfad, z. B. `C:\Projekte\StarWarsInventar`).
2. In Android Studio **File > Open** wählen und den entpackten Ordner `StarWarsInventar` öffnen (den Ordner mit `settings.gradle.kts`, nicht `app`).
3. Den Gradle-Sync abwarten. Beim ersten Mal lädt Gradle 8.11.1 und alle Bibliotheken herunter.
4. Fehlt die Android-Plattform 35, bietet Android Studio im Sync-Fenster einen Link zur Installation an. Alternativ unter **Settings > Languages & Frameworks > Android SDK** die Plattform *Android 15 (API 35)* anhaken.
5. Emulator anlegen (**Device Manager**) oder ein Gerät per USB verbinden (ab Android 8.0, API 26) und **Run** drücken.

Der Gradle-Wrapper (`gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`) liegt bei. Die Datei `local.properties` mit dem SDK-Pfad legt Android Studio beim Öffnen selbst an. Sie gehört nicht ins Projekt.

Schlägt Android Studio ein Update des Android Gradle Plugins vor, kannst du es annehmen, die App funktioniert aber auch mit der eingetragenen Version (8.7.3).

## Aufbau

```
app/src/main/java/com/starwars/inventar/
├── MainActivity.kt          Einstiegspunkt, Edge-to-Edge, Theme
├── InventarApplication.kt   Datenbank und Repository
├── data/                    Room: Entities, DAOs, Datenbank, Repository
└── ui/
    ├── InventoryViewModel.kt
    ├── Format.kt            Credits, Datum, Dezimalzahlen
    ├── navigation/          Routen und NavHost
    ├── theme/               Farben, Typografie, Theme
    ├── components/          Datapad-Bausteine (Screen, Panel, Buttons, Felder)
    └── screens/             Start, Artikel (Liste, Bearbeiten), Verträge (Liste, Bearbeiten)
```

## Datenmodell

**Artikel:** Bezeichnung, Kategorie, Status, Beschreibung, Besitzer, Partner, Stückzahl, Lagerort, Szene/Akt, Kosten, Preis, Vertrag, Notizen.

**Vertrag:** Nummer, Titel, Art, Status, Besitzer/Auftraggeber, Partner, Beginn, Ende, Kosten, Preis, Kaution, Unterzeichnet, Bedingungen, Notizen.

Ein Artikel kann einem Vertrag zugeordnet werden. Wird ein Vertrag gelöscht, bleibt der Artikel bestehen und verliert nur die Zuordnung. Beträge werden in Credits (cr) angezeigt.

## Datapad-Look

Dunkles Blauschwarz, Cyan- und Bernstein-Akzente, abgeschrägte Panel-Ecken, Scanlines über dem Hintergrund, blinkende Statusleuchte und Monospace-Schrift. Farben stehen in `ui/theme/Color.kt`, die Bausteine in `ui/components/`.

## Nächste Schritte

- Echte Aurebesh-Schrift als Font-Ressource einbinden (Lizenz prüfen)
- Fotos zu Artikeln
- Export als CSV oder PDF für Proben und Abrechnung
- Dashboard-Auswertungen, z. B. Artikel pro Szene oder auslaufende Verträge
