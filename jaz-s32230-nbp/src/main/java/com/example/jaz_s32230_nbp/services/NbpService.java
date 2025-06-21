package com.example.jaz_s32230_nbp.services;

import com.example.jaz_s32230_nbp.models.CurrencyRate;
import com.example.jaz_s32230_nbp.repositories.CurrencyRateRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


@Service
public class NbpService {

    private final RestTemplate restTemplate;
    private final CurrencyRateRepository currencyRateRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public NbpService(CurrencyRateRepository currencyRateRepository) {
        this.restTemplate = new RestTemplate();
        this.currencyRateRepository = currencyRateRepository;
    }

    public double getAverageExchangeRate(String currency, LocalDate startDate, LocalDate endDate) {
        String baseUrl = "http://api.nbp.pl/api/exchangerates/rates/A/" + currency + "/"
                + startDate.format(dateFormatter) + "/" + endDate.format(dateFormatter) + "/";

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("format", "json")
                .toUriString();

        Map<String, Object> response;
        try {
            response = restTemplate.getForObject(url, Map.class);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Błąd podczas pobierania danych z NBP dla waluty " + currency + " w zakresie " + startDate + " do " + endDate + ": " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Wystąpił problem z połączeniem do API NBP: " + e.getMessage(), e);
        }

        List<Map<String, Object>> rates = (List<Map<String, Object>>) response.get("rates");

        double averageRate = rates.stream()
                .mapToDouble(rate -> (Double) rate.get("mid"))
                .average()
                .orElseThrow(() -> new RuntimeException("Blad"));

        CurrencyRate newRate = new CurrencyRate();
        newRate.setCurrency(currency);
        newRate.setStartDate(startDate);
        newRate.setEndDate(endDate);
        newRate.setAverageRate(averageRate);
        newRate.setTimestamp(LocalDateTime.now());

        currencyRateRepository.save(newRate);

        return averageRate;
    }
}