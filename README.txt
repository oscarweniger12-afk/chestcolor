Chest Color (Fabric, Minecraft 26.3, Java 25)

Bauen:
  gradle wrapper --gradle-version 9.5.1     (einmalig, im Projektordner)
  ./gradlew build
Ergebnis: build/libs/chestcolor-1.0.0.jar -> in den mods-Ordner (Fabric Loader + Fabric API nötig)

- Ungeöffnete Kisten: grünlich, geöffnete: rötlich (Kisten, Redstone-Kisten, Endertruhen)
- Doppelkisten werden gemeinsam markiert
- Gespeichert pro Welt/Server in config/chestcolor/
- Früher geöffnete Kisten kennt die Mod nicht, alle starten grün
