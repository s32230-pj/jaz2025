package com.example.jaz_s32230_nbp.controllers;
import com.example.jaz_s32230_nbp.models.CurrencyRate;
import com.example.jaz_s32230_nbp.repositories.CurrencyRateRepository;
import com.example.jaz_s32230_nbp.services.NbpService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/currency")
@Tag(name = "Kursy Walut NBP", description = "Aplikacja służąca do obliczania średniego kursu walut z wybranego okresu. Zgodnie z zadaniem posiada jeden ENDPOINT GET, szczegóły opisane poniżej.")
public class CurrencyRateController {

    private final NbpService nbpService;
    public final CurrencyRateRepository currencyRateRepository;

    public CurrencyRateController(NbpService nbpService, CurrencyRateRepository currencyRateRepository) {
        this.nbpService = nbpService;
        this.currencyRateRepository = currencyRateRepository;
    }

    @Operation(
            summary = "Oblicza i zapisuje średni kurs waluty dla podanego przedziału dat",
            description = "Pobiera dane kursów walut z API NBP dla  waluty i przedziału dat, " +
                    "zapisuje wynik do bazy danych.",
            parameters = {
                    @Parameter(name = "currency", description = "Kod waluty", required = true,
                            schema = @Schema(type = "string", example = "USD")),

                    @Parameter(name = "startDate", description = "Data rozpoczęcia przedziału (YYYY-MM-DD)", required = true,
                            schema = @Schema(type = "string", format = "date", example = "2024-01-01")),

                    @Parameter(name = "endDate", description = "Data zakończenia przedziału (YYYY-MM-DD)", required = true,
                            schema = @Schema(type = "string", format = "date", example = "2024-01-10"))
            },
            responses = {

                    @ApiResponse(responseCode = "500", description = "Wewnetrzny bład serwera",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "\"Wystapil nieoczekiwany bład serwera: [szczegoly]\""))),

                    @ApiResponse(responseCode = "200", description = "Pomyslnie obliczono i zapisano sredni kurs",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "\"Sredni kurs dla USD w zakresie 2024-01-01 do 2024-01-10: 4.0256. Dane zostały zapisane do bazy danych.\""))),


                    @ApiResponse(responseCode = "400", description = "Brak danych z API NBP lub bledny format daty",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "\"Blad: Brak danych kursów dla waluty EUR w podanym przedziale dat.\""))),

            }
    )
    @GetMapping("/average-rate")
    public ResponseEntity<String> getAndSaveAverageRate(
            @RequestParam String currency,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Nie wiem czy mielismy zaokraglac ale brzydkie liczby wychodziły pokroju '4.297667032967033' to zaokrągliłem w bazie danych liczby zapisują się dokładnie.
        double averageRate = (double)Math.round(nbpService.getAverageExchangeRate(currency, startDate, endDate) * 100) / 100;


        String wyliczone = String.format("Średni kurs dla " + currency + " w zakresie: " + startDate + " do " + endDate + " : "  + averageRate + ". Dane zostały zapisane do bazy danych.",
                currency, startDate, endDate, averageRate);
        return ResponseEntity.ok(wyliczone);
    }

    // http://localhost:8080/api/currency/all
    @Operation(
            summary = "Wypisuje wszystkie zapisane dane z bazy danych",
            description = "Pobiera wszystkie pola z bazy danych łącznie z id i wypisuje w liście.")
    @GetMapping("/all")
    public ResponseEntity<List<CurrencyRate>> getAllCurrencyRates() {
        List<CurrencyRate> rates = currencyRateRepository.findAll();
        return ResponseEntity.ok(rates);
    }


}