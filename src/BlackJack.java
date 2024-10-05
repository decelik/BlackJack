import java.util.ArrayList; // Importiert ArrayList zur Speicherung der Karten
import java.util.Random; // Importiert Random für das Mischen der Karten

// Importe für die grafische Benutzeroberfläche
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Kartenbilder für die grafische Darstellung (Links)
// https://opengameart.org/content/playing-cards-vector-png
// https://opengameart.org/content/colorful-poker-card-back

public class BlackJack {

    // Innere Klasse "Card" zur Darstellung einer Spielkarte
    private class Card {
        String value; // Der Wert der Karte (2, 3, ..., J, Q, K, A)
        String type;  // Der Typ der Karte (C für Club, D für Diamond, H für Heart, S für Spade)

        // Konstruktor für eine Karte, der Wert und Typ initialisiert
        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        // Überschreibt die toString()-Methode, um die Karte als String darzustellen (z.B. "A-S" für Ass of Spades)
        @Override
        public String toString() {
            return value + "-" + type;
        }

        // Gibt den Wert der Karte zurück (Zahlenkarten behalten ihren Wert, Bildkarten zählen 10, Asse zählen 11)
        public int getValue() {
            if ("AJQK".contains(value)) { // Falls die Karte ein Ass, Bube, Dame oder König ist
                if (value == "A") { // Falls es ein Ass ist
                    return 11; // Ass zählt als 11
                }
                return 10; // Bube, Dame, König zählen jeweils 10
            }
            return Integer.parseInt(value); // Zahlenkarten geben ihren eigenen Wert zurück
        }

        // Prüft, ob die Karte ein Ass ist
        public boolean isAce() {
            return value == "A";
        }

        // Gibt den Pfad zum Bild der Karte zurück (wird später in der GUI verwendet)
        public String getImagePath() {
            return "./Karten/" + toString() + ".png";
        }
    }

    // Liste zur Speicherung der Karten im Deck
    ArrayList<Card> deck;

    // Random Objekt zum Mischen der Karten
    Random random = new Random();

    // Variablen für den Dealer
    Card hiddenCard; // Die verdeckte Karte des Dealers
    ArrayList<Card> dealerHand; // Liste zur Speicherung der Karten des Dealers
    int dealerSum; // Summe der Kartenwerte des Dealers
    int dealerAceCount; // Zählt die Anzahl der Asse beim Dealer

    // Variablen für den Spieler
    ArrayList<Card> playerHand; // Liste zur Speicherung der Karten des Spielers
    int playerSum; // Summe der Kartenwerte des Spielers
    int playerAceCount; // Zählt die Anzahl der Asse beim Spieler

    // Einstellungen für die Benutzeroberfläche (Größe des Spielfelds, der Karten etc.)
    int boardWidth = 800; // Breite des Spielfelds
    int boardHeight = 800; // Höhe des Spielfelds
    int cardWidth = 110; // Breite der Karten
    int cardHeight = 154; // Höhe der Karten  -> Verhältnis von 1.4 besser, sonst sind die Karten verzerrt

    // Hauptfenster für das Spiel
    JFrame frame = new JFrame("BlackJack");

    // Spielfeld , in dem die Karten gezeichnet werden
    JPanel gamePanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g); // Standard Methodenaufruf, um das Panel zu zeichnen

            try {
                // Verdeckte Karte des Dealers zeichnen
                Image hiddenCardImage = new ImageIcon(getClass().getResource("./Karten/backside.png")).getImage();

                // Wenn der "Stay" Button deaktiviert ist, wird die verdeckte Karte des Dealers angezeigt
                if (!stayButton.isEnabled()) {
                    hiddenCardImage = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                }
                g.drawImage(hiddenCardImage, 20, 20, cardWidth, cardHeight, null);

                // Karten des Dealers zeichnen
                for (int i = 0; i < dealerHand.size(); i++) {
                    Card card = dealerHand.get(i);
                    Image cardImage = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImage, cardWidth + 25 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);
                }

                // Karten des Spielers zeichnen
                for (int i = 0; i < playerHand.size(); i++) {
                    Card card = playerHand.get(i);
                    Image cardImage = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImage, 20 + (cardWidth + 5) * i, 320, cardWidth, cardHeight, null );
                }

                // Wenn "Stay" gedrückt wurde, berechne das Endergebnis
                if (!stayButton.isEnabled()) {
                    dealerSum = reduceDealerAce(); // Berechne den Wert des Dealers unter Berücksichtigung der Asse
                    playerSum = reducePlayerAce(); // Berechne den Wert des Spielers unter Berücksichtigung der Asse



                    // Bestimme das Ergebnis des Spiels und zeige es an
                    String message = "";
                    if (playerSum > 21) {
                        message = "Du hast verloren!";
                    } else if (dealerSum > 21) {
                        message = "Du hast gewonnen!";
                    } else if (playerSum == dealerSum) {
                        message = "Unentschieden!";
                    } else if (playerSum > dealerSum) {
                        message = "Du hast gewonnen!";
                    } else if (playerSum < dealerSum) {
                        message = "Du hast verloren!";
                    }

                    // Zeige die Nachricht auf dem Spielfeld an
                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.white);
                    g.drawString(message, 220, 250);
                }

            } catch (Exception e) {
                e.printStackTrace(); // Gibt Fehler im Falle einer Exception aus
            }
        }
    };

    // Benutzeroberfläche: Buttons für "Hit", "Stay" und "Neue Runde"
    JPanel buttonPanel = new JPanel(); // Panel für die Buttons
    JButton hitButton = new JButton("Hit"); // Button für "Hit" (weitere Karte ziehen)
    JButton stayButton = new JButton("Stay"); // Button für "Stay" (keine weitere Karte ziehen)
    JButton replayButton = new JButton("Neue Runde"); // Button für "Neue Runde"

    // Konstruktor der BlackJack-Klasse (Startet das Spiel und baut die Benutzeroberfläche auf)
    BlackJack() {
        startGame(); // Startet eine neue Runde

        // Frame-Einstellungen
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Layout und Hintergrundfarbe des Spielfelds
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53, 101, 77)); // Dunkles Grün (ähnlich einem Filztisch)
        frame.add(gamePanel);

        // Buttons zu ihrem Panel hinzufügen
        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        replayButton.setFocusable(false);
        buttonPanel.add(replayButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Action Listener für den "Hit" Button (wenn der Spieler eine neue Karte ziehen will)
        hitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Card card = deck.remove(deck.size() - 1); // Ziehe die letzte Karte vom Deck
                playerSum += card.getValue(); // Aktualisiere die Summe des Spielers
                playerAceCount += card.isAce() ? 1 : 0; // Falls die Karte ein Ass ist, erhöhe die Anzahl der Asse
                playerHand.add(card); // Füge die Karte zur Hand des Spielers hinzu

                if (reducePlayerAce() > 21) { // Prüfe, ob der Spieler über 21 ist, nachdem Asse reduziert wurden
                    hitButton.setEnabled(false); // Deaktiviere den Hit-Button, wenn der Spieler verloren hat
                }

                gamePanel.repaint(); // Aktualisiere die grafische Darstellung
                replayButton.setEnabled(true); // Aktiviere den Button für eine neue Runde
            }
        });

        // Action Listener für den "Stay" Button (wenn der Spieler keine weiteren Karten ziehen möchte)
        stayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hitButton.setEnabled(false); // Deaktiviere den Hit Button
                stayButton.setEnabled(false); // Deaktiviere den Stay Button

                // Dealer zieht solange Karten, bis er mindestens 17 Punkte hat
                while (dealerSum < 17) {
                    Card card = deck.remove(deck.size() - 1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1 : 0;
                    dealerHand.add(card);
                }
                gamePanel.repaint(); // Aktualisiere die grafische Darstellung
                replayButton.setEnabled(true); // Aktiviere den Button für eine neue Runde
            }
        });

        // Action Listener für den "Neue Runde" Button (um ein neues Spiel zu starten)
        replayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hitButton.setEnabled(true); // Reaktiviere den Hit Button
                stayButton.setEnabled(true); // Reaktiviere den Stay Button
                startGame(); // Starte ein neues Spiel
                gamePanel.repaint(); // Aktualisiere die grafische Darstellung
            }
        });

        gamePanel.repaint(); // Initiale Darstellung des Spielfelds
    }

    // Methode zum Starten des Spiels
    public void startGame() {
        // Erstelle und mische das Deck
        buildDeck();
        shuffleDeck();

        // Initialisiere die Karten des Dealers
        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        // Ziehe die verdeckte Karte des Dealers
        hiddenCard = deck.remove(deck.size() - 1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        // Ziehe die offene Karte des Dealers
        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        System.out.println("Dealer:");
        System.out.println(hiddenCard);
        System.out.println(dealerHand);
        System.out.println(dealerSum);
        System.out.println(dealerAceCount);

        // Initialisiere die Karten des Spielers
        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;

        // Ziehe zwei Karten für den Spieler
        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }

        System.out.println("Spieler:");
        System.out.println(playerHand);
        System.out.println(playerSum);
        System.out.println(playerAceCount);

    }

    // Methode zum Erstellen des Kartendecks
    public void buildDeck() {
        deck = new ArrayList<Card>();
        String[] values = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"}; // Werte der Karten
        String[] types = {"C", "D", "H", "S"}; // Typen der Karten (Clubs, Diamonds, Hearts, Spades)

        // Für jede Kartenfarbe und jeden Wert wird eine Karte erstellt und zum Deck hinzugefügt
        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < values.length; j++) {
                Card card = new Card(values[j], types[i]);
                deck.add(card);
            }
        }

        System.out.println("Erstelle Kartendeck:");
        System.out.println(deck);

    }

    // Methode zum Mischen des Decks
    public void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size()); // Wähle eine zufällige Karte
            Card currentCard = deck.get(i); // Aktuelle Karte
            Card randomCard = deck.get(j); // Zufällige Karte
            deck.set(i, randomCard); // Tausche die aktuelle Karte mit der zufälligen
            deck.set(j, currentCard);
        }

        System.out.println("Karten gemischt:");
        System.out.println(deck);

    }

    // Methode zum Anpassen des Spielerwerts, wenn Asse vorhanden sind und der Wert über 21 ist
    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10; // Reduziere den Wert eines Asses von 11 auf 1
            playerAceCount -= 1; // Verringere die Anzahl der Asse
        }
        return playerSum;
    }

    // Methode zum Anpassen des Dealerwerts, wenn Asse vorhanden sind und der Wert über 21 ist
    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10; // Reduziere den Wert eines Asses von 11 auf 1
            dealerAceCount -= 1; // Verringere die Anzahl der Asse
        }
        return dealerSum;
    }
}