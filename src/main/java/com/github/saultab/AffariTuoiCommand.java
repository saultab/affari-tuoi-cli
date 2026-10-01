package com.github.saultab;

import com.github.saultab.model.Box;
import com.github.saultab.service.GameAvailabilityService;
import com.github.saultab.service.GameAvailabilityService.AvailabilityStatus;
import com.github.saultab.service.GameEngine;
import com.github.saultab.service.TurnManager;
import com.github.saultab.ui.GameUI;
import com.github.saultab.ui.InputProvider;

import io.quarkus.runtime.QuarkusApplication;
import jakarta.inject.Inject;

import java.time.Duration;
import java.util.Optional;

import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class AffariTuoiCommand implements QuarkusApplication {

    @Inject
    GameUI ui;
    @Inject
    GameEngine engine;
    @Inject
    TurnManager turnManager;
    @Inject
    GameAvailabilityService validator;
    @Inject
    InputProvider input;

    @Override
    public int run(String... args) {
        ui.displayWelcome();

        // Check availabilityService
        if (!isGameValidToStart()) {
            return 0;
        }

        // 0. Start game
        long gameSeed = resolveSeed(args);
        engine.initGame(gameSeed);

        // 1. Box of the player
        ui.displayBoard(engine.getAllBoxes(), Optional.empty());
        ui.printSystemMessage("Scegli il tuo pacco fortunato (1-20): ");
        int playerChoice = getBoxChoice(true);
        engine.selectPlayerBox(playerChoice);
        ui.printSystemMessage("Hai scelto il pacco " + playerChoice + ". Mettiamolo da parte!");

        // 2. Loop of the game
        boolean gameOver = false;
        while (engine.getRemainingBoxes().size() != 2 && !gameOver) {
            playTurn();
            gameOver = makeOfferPhase();
        }

        // 3. Big final
        ui.printFinalReveal(engine.getPlayerBox().getId(), engine.getPlayerBox().getValue());

        // 4. Reveal the game seed at the end to ensure transparency
        ui.printNotaryVerification(gameSeed);

        return 0;
    }

    private long resolveSeed(String[] args) {
        if (args.length > 0) {
            try {
                long seed = Long.parseLong(args[0]);
                ui.printSystemMessage("Seed personalizzato caricato: " + seed);
                return seed;
            } catch (NumberFormatException e) {
                ui.printSystemMessage("Seed non valido. Generazione seed casuale...");
            }
        }
        return new java.security.SecureRandom().nextLong();
    }

    private boolean isGameValidToStart() {
        // We block here because the CLI cannot proceed without this answer
        AvailabilityStatus status = validator.checkAvailability()
                .await().atMost(Duration.ofSeconds(5));

        if (status == AvailabilityStatus.AVAILABLE) {
            return true;
        }

        // Handle all error cases
        String errorMessage = switch (status) {
            case WEEKEND -> "Affari Tuoi non va in onda il sabato e la domenica.";
            case PUBLIC_HOLIDAY -> "Oggi è un giorno di festa nazionale: Affari Tuoi non va in onda.";
            case CONNECTION_ERROR -> "Impossibile verificare la messa in onda (manca connessione internet).";
            default -> "Errore sconosciuto durante la validazione.";
        };

        ui.printSystemMessage(errorMessage);
        ui.printSystemMessage("Il programma tornerà in onda alla prossima data utile.");
        return false;
    }

    private void playTurn() {
        // Continue until the required boxes for this turn are opened
        while (!turnManager.isTurnOver()) {
            ui.displayPrizeList(engine.getRemainingPrizes());
            ui.displayBoard(engine.getAllBoxes(), Optional.of(engine.getPlayerBox().getId()));
            ui.printRemaingTurn(turnManager.getRemainingInTurn());

            // getBoxChoice loops internally until a valid and openable ID is provided
            int toOpen = getBoxChoice(false);

            // At this point, toOpen is guaranteed to be valid
            engine.openBox(toOpen);

            final var value = engine.getBoxValue(toOpen);
            ui.printBoxOpening(toOpen, value);
        }
    }

    private boolean makeOfferPhase() {
        Integer offer = engine.getDoctorDecision();
        String decision = readDecision(offer);

        if (decision.equalsIgnoreCase("A")) {
            if (offer != null) {
                ui.printOfferAccepted(offer);
                return true;
            } else {
                ui.printSystemMessage("Scegli il pacco sul tabellone con cui vuoi scambiare il tuo.");

                int chosenId = getBoxChoice(false);
                Box newBox = engine.getAllBoxes().get(chosenId);
                Box oldBox = engine.getPlayerBox();

                engine.swapPlayerBox(oldBox, newBox);
                ui.printExchangeConfirmed(oldBox.getId(), newBox.getId());
                turnManager.startNewTurn(3);
            }
        } else {
            ui.printSystemMessage("Hai rifiutato l'offerta del Dottore. Si va avanti!");
            // After first turn of 6, now 3 round
            turnManager.startNewTurn(3);
        }
        return false;
    }

    public String readDecision(Integer offer) {
        ui.printDoctorCall();
        ui.displayPrizeList(engine.getRemainingPrizes());
        ui.displayBoard(engine.getAllBoxes(), Optional.of(engine.getPlayerBox().getId()));

        if (offer != null) {
            ui.printMoneyOffer(offer);
        } else {
            ui.printExchange();
        }

        while (true) {
            String choice = input.readNext().toUpperCase();

            if (choice.equals("A") || choice.equals("R")) {
                return choice;
            }

            ui.printErrorDoctorCall();
            ui.printSystemMessage("Inserisci 'A' per Accettare o 'R' per Rifiutare.");
        }
    }

    private int getBoxChoice(boolean isInitialSelection) {
        while (true) {
            if (!isInitialSelection) {
                ui.printSystemMessage("Inserisci il numero del pacco: ");
            }
            String text = input.readNext();
            try {
                int choice = Integer.parseInt(text);
                if (choice < 1 || choice > 20) {
                    ui.printSystemMessage("Scegli un numero tra 1 e 20.");
                    continue;
                }

                if (isInitialSelection) {
                    return choice;
                } else {
                    if (engine.isBoxOpenable(choice)) {
                        return choice;
                    } else {
                        ui.printSystemMessage("Pacco non disponibile (già aperto o è il tuo!).");
                    }
                }
            } catch (NumberFormatException e) {
                ui.printSystemMessage("Errore: Inserisci un numero valido.");
            }
        }
    }
}