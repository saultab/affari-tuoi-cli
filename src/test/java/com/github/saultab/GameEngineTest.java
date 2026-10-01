package com.github.saultab;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.saultab.model.Box;
import com.github.saultab.service.GameEngine;
import com.github.saultab.service.TurnManager;

import java.security.SecureRandom;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class GameEngineTest {

    @Mock
    private TurnManager turnManager;

    @InjectMocks
    private GameEngine engine;
    
    @BeforeEach
    public void setup() {
        long gameSeed = new SecureRandom().nextLong();
        engine.initGame(gameSeed);
        engine.selectPlayerBox(10);
    }

    @Test
    public void testInitialization() {
        assertNotNull(engine.getAllBoxes());
        assertEquals(20, engine.getAllBoxes().size());
        assertNotNull(engine.getPlayerBox());
        verify(turnManager, atLeastOnce()).startNewTurn(anyInt());
    }

    @Test
    public void testBoxOpenableLogic() {
        int playerBoxId = engine.getPlayerBox().getId();
        assertFalse(engine.isBoxOpenable(playerBoxId));

        int otherId = (playerBoxId == 1) ? 2 : 1;
        assertTrue(engine.isBoxOpenable(otherId));

        engine.getAllBoxes().get(otherId).setOpened(true);
        assertFalse(engine.isBoxOpenable(otherId));
    }

    @Test
    public void testSwapPlayerBox() {
        Box oldBox = engine.getPlayerBox();
        int targetId = oldBox.getId();
        Box newBox = engine.getAllBoxes().get(targetId);

        engine.swapPlayerBox(newBox, oldBox);

        assertEquals(targetId, engine.getPlayerBox().getId());
        assertEquals(newBox, engine.getPlayerBox());
    }

    @Test
    public void testRemainingPrizesCount() {
        List<Integer> prizes = engine.getRemainingPrizes();
        assertEquals(20, prizes.size());

        int firstId = engine.getAllBoxes().keySet().iterator().next();
        if (firstId == engine.getPlayerBox().getId()) {
            firstId = (firstId == 20) ? 1 : firstId + 1;
        }

        engine.getAllBoxes().get(firstId).setOpened(true);
        assertEquals(19, engine.getRemainingPrizes().size());
    }

    @Test
    public void testDoctorOfferMultiples() {
        Integer offer = engine.getDoctorDecision();
        if (offer != null) {
            assertEquals(0, offer % 500);
        }
    }
}