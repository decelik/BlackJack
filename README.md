# BlackJack Spiel mit grafischer Benutzeroberfläche

Dieses Java-Programm implementiert ein BlackJack-Spiel mit einer grafischen Benutzeroberfläche (GUI). Der Spieler tritt gegen den Dealer an und versucht, mit seinen Karten so nah wie möglich an 21 Punkte zu kommen, ohne diesen Wert zu überschreiten.

## Programmübersicht

Das Programm besteht aus zwei Hauptteilen:
1. **App.java**: Startet das Programm, indem es eine Instanz der `BlackJack`-Klasse erstellt.
2. **BlackJack.java**: Enthält die Spielmechanik, die Logik zur Berechnung der Kartenwerte und die grafische Darstellung des Spiels.

## Funktionen

- **Kartenlogik**: 
  - Das Programm verwendet ein Standard-52-Karten-Deck.
  - Jede Karte hat einen Wert (2–10, J, Q, K, A) und einen Typ (Clubs, Diamonds, Hearts, Spades).
  - Assen kann je nach Spielsituation der Wert 1 oder 11 zugewiesen werden.
  
- **Spielablauf**:
  - Der Spieler erhält zwei Karten und kann entscheiden, ob er weitere Karten zieht ("Hit") oder keine weiteren Karten nimmt ("Stay").
  - Der Dealer zieht Karten, bis er mindestens 17 Punkte erreicht.
  - Das Spiel endet, wenn der Spieler oder der Dealer 21 überschreitet oder sich für "Stay" entscheidet.

- **Gewinnbedingungen**:
  - Der Spieler gewinnt, wenn er näher an 21 Punkte kommt als der Dealer, ohne den Wert zu überschreiten.
  - Wenn der Dealer über 21 Punkte kommt, gewinnt der Spieler automatisch.

## Benutzeroberfläche

Das Programm bietet eine grafische Oberfläche, in der die Karten des Spielers und des Dealers angezeigt werden.

- **Spielkarten**: Die Karten werden mit Bildern dargestellt, die über den Pfad `./Karten/` geladen werden.
- **Interaktion**:
  - Der Spieler kann über die Buttons "Hit", "Stay" und "Neue Runde" Aktionen ausführen.
  - Das Spielfeld hat eine grüne Farbe, die einem traditionellen Kartentisch ähnelt.

## Installation

1. Lade die Kartenbilder herunter und speichere sie im Verzeichnis `./Karten/`:

2. Kompiliere und führe die `App.java`-Datei aus:

```bash
javac App.java
java App


![Screenshot 2024-10-05 124732](https://github.com/user-attachments/assets/0b278199-00e1-40f5-ba07-850e0402594d)
