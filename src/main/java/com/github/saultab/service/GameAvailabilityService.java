package com.github.saultab.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;

@ApplicationScoped
public class GameAvailabilityService {

    public enum AvailabilityStatus {
        AVAILABLE,
        WEEKEND,
        PUBLIC_HOLIDAY,
        CONNECTION_ERROR
    }

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    public Uni<AvailabilityStatus> checkAvailability() {
        LocalDate today = LocalDate.now();
        DayOfWeek day = today.getDayOfWeek();

        // Check weekend
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return Uni.createFrom().item(AvailabilityStatus.WEEKEND);
        }

        // Check holiday
        return checkPublicHoliday()
                .onItem().transform(isHoliday -> {
                    if (isHoliday == null)
                        return AvailabilityStatus.CONNECTION_ERROR;
                    if (isHoliday)
                        return AvailabilityStatus.PUBLIC_HOLIDAY;
                    return AvailabilityStatus.AVAILABLE;
                });
    }

    private Uni<Boolean> checkPublicHoliday() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://date.nager.at/api/v3/IsTodayPublicHoliday/IT"))
                .GET()
                .build();

        return Uni.createFrom()
                .completionStage(() -> httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding()))
                .onItem().transform(resp -> resp.statusCode() == 200)
                .onFailure().recoverWithItem((Boolean) null);
    }
}