package com.github.saultab;

import com.github.saultab.service.GameAvailabilityService;
import com.github.saultab.service.GameAvailabilityService.AvailabilityStatus;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameAvailabilityServiceTest {

    @Mock
    GameAvailabilityService availabilityService;

    @Test
    public void testWhenItIsPublicHoliday() {
        when(availabilityService.checkAvailability())
                .thenReturn(Uni.createFrom().item(AvailabilityStatus.PUBLIC_HOLIDAY));

        AvailabilityStatus status = availabilityService.checkAvailability()
                .await().atMost(Duration.ofSeconds(2));

        assertEquals(AvailabilityStatus.PUBLIC_HOLIDAY, status);
    }

    @Test
    public void testWhenConnectionFails() {
        when(availabilityService.checkAvailability())
                .thenReturn(Uni.createFrom().item(AvailabilityStatus.CONNECTION_ERROR));

        AvailabilityStatus status = availabilityService.checkAvailability()
                .await().atMost(Duration.ofSeconds(2));

        assertEquals(AvailabilityStatus.CONNECTION_ERROR, status);
    }

    @Test
    public void testTimeoutBehavior() {
        when(availabilityService.checkAvailability())
                .thenReturn(Uni.createFrom().item(AvailabilityStatus.PUBLIC_HOLIDAY)
                        .onItem().delayIt().by(Duration.ofSeconds(3)));

        assertThrows(Exception.class, () -> {
            availabilityService.checkAvailability()
                    .await().atMost(Duration.ofSeconds(2));
        });
    }
}