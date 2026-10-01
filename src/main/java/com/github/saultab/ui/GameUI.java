package com.github.saultab.ui;

import com.github.saultab.model.Box;
import com.github.saultab.model.PrizeTable;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class GameUI {

    // Costant ANSI
    public static final String RESET = "\u001B[0m";
    public static final String GOLD = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";
    public static final String BOLD = "\u001B[1m";
    public static final String BG_BLUE = "\u001B[44m";

    public void displayWelcome() {
        System.out.println(GOLD + BOLD + " 📦 ============================================= 📦" + RESET);
        System.out.println(GOLD + BOLD + " |" + RESET + BG_BLUE + BOLD
                + "           AFFARI TUOI - CLI EDITION           " + RESET + GOLD + BOLD + "|" + RESET);
        System.out.println(GOLD + BOLD + " 📦 ============================================= 📦" + RESET);
        System.out.println();
    }

    public void printSystemMessage(String message) {
        System.out.println("\n" + CYAN + "[Sistema] " + message + RESET);
    }

    public void displayBoard(Map<Integer, Box> boxes, Optional<Integer> playerId) {
        System.out.println("\n" + BOLD + "PACCHI IN GIOCO:" + RESET);
        for (int id = 1; id <= 20; id++) {
            Box b = boxes.get(id);

            if (b == null) {
                System.out.print("[    ] ");
            } else if (playerId.isPresent() && playerId.get() == id) {
                System.out.print(CYAN + "[ TU ] " + RESET);
            } else if (b.isOpened()) {
                System.out.print(RED + "[ XX ] " + RESET);
            } else {
                System.out.printf(GOLD + "[ %2d ] " + RESET, b.getId());
            }

            if (id % 5 == 0) {
                System.out.println();
            }
        }
    }

    public void displayPrizeList(List<Integer> remainingPrizes) {
        System.out.println("\n" + BOLD + "TABELLONE PREMI RIMASTI:" + RESET);

        final var bluePrizes = PrizeTable.BLUE_PRIZES;
        final var redPrizes = PrizeTable.RED_PRIZES;

        for (int i = 0; i < 10; i++) {
            Integer blueVal = bluePrizes.get(i);
            Integer redVal = redPrizes.get(i);

            // Blue Column
            if (remainingPrizes.contains(blueVal)) {
                System.out.printf(BLUE + "%12d €" + RESET, blueVal);
            } else {
                System.out.print("              ");
            }

            System.out.print(BOLD + "   |   " + RESET);

            // Red Column
            if (remainingPrizes.contains(redVal)) {
                System.out.printf(RED + "%-12d €" + RESET, redVal);
            } else {
                System.out.print("              ");
            }
            System.out.println();
        }
    }

    public void printBoxOpening(int id, Integer value) {
        String color = value >= 5000 ? RED : BLUE;
        System.out.println("\n" + BOLD + "Apertura pacco " + id + "..." + RESET);
        System.out.println(
                color + BOLD + ">> ABBIAMO TROVATO: " + String.format("%d", value) + " € <<" + RESET + "\n");
    }

    public void printDoctorCall() {
        System.out.println("\n" + RED + BOLD + "☎️  DRRRIIIINNN! Il Dottore è al telefono..." + RESET + "\n");
    }

    public void printErrorDoctorCall() {
        System.out.println(
                "\n" + RED + "Scelta non valida. Digita 'A' per accettare o 'R' per rifiutare." + RESET + "\n");
    }

    public void printFinalReveal(int boxId, Integer value) {
        System.out.println("\nNel tuo pacco numero " + BOLD + boxId + RESET + " ci sono:");

        System.out.println(GOLD + "********************************" + RESET);
        System.out.printf(GOLD + "        %d €" + RESET + "\n", value);
        System.out.println(GOLD + "********************************" + RESET);
    }

    public void printNotaryVerification(long seed) {
        System.out.println("\n" + BOLD + "--- VERIFICA NOTARILE ---" + RESET);
        System.out.println("Per verificare l'onestà della partita e l'assegnazione");
        System.out.println("casuale dei premi, il seed generato è: " + GOLD + seed + RESET);
    }

    public void printOfferAccepted(Integer amount) {
        System.out.println("\n" + GOLD + BOLD + "💰 TRATTATIVA CONCLUSA!" + RESET);
        System.out.printf("Hai accettato l'offerta di " + GOLD + "%d €" + RESET + "\n", amount);
        System.out.println("La partita termina qui.");
    }

    public void printExchangeConfirmed(int oldId, int newId) {
        System.out.println("\n" + GOLD + BOLD + "🔄 CAMBIO EFFETTUATO!" + RESET);
        System.out.println("Hai lasciato il pacco numero " + RED + oldId + RESET +
                " e hai preso il pacco numero " + GOLD + newId + RESET + ".");
        System.out.println("La fortuna ti assisterà? Continuiamo!");
    }

    public void printExchange() {
        System.out.print(
                "\nIl Dottore ti propone il " + GOLD + "CAMBIO" + RESET + ". [" + BOLD + "A" + RESET + "]ccetti o ["
                        + BOLD + "R" + RESET + "]ifiuti? ");
    }

    public void printMoneyOffer(Integer offer) {
        System.out.printf(
                "\nIl Dottore ti offre " + GOLD + "%d €" + RESET + ". [" + BOLD + "A" + RESET + "]ccetti o [" + BOLD
                        + "R" + RESET + "]ifiuti? ",
                offer);
    }

    public void printRemaingTurn(Integer turn) {
        System.out.printf("\nDevi ancora aprire %d pacchi per terminare il turno. ", turn);
    }
}