package hr.fer.tel.rassus.sensor.services;

import hr.fer.tel.rassus.sensor.beans.SensorReading;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.client.RestTemplate;

@Service
public class SensorReadingService {
    private final RestTemplate restTemplate = new RestTemplate();
    private SensorReading sensorReading;
    private List<String> lines;
    private final long startTime;

    public SensorReadingService() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("readings.csv").getInputStream()))) {
            this.lines = reader.lines().toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.startTime = System.currentTimeMillis();
    }

    private double parseDoubleSafe(String s) {
        if (s == null || s.isBlank()) return 0.0;
        return Double.parseDouble(s.trim());
    }

    public void makeReading() throws IOException {

        long secondsActive = (System.currentTimeMillis() - startTime) / 1000;
        int rowIndex = (int) ((secondsActive % 100)) + 1;

        String line = this.lines.get(rowIndex);
        String[] parts = line.split(",", -1);
        double temperature = parseDoubleSafe(parts[0]);
        double pressure = parseDoubleSafe(parts[1]);
        double humidity = parseDoubleSafe(parts[2]);
        double co = parseDoubleSafe(parts[3]);
        double no2 = parseDoubleSafe(parts[4]);
        double so2 = parseDoubleSafe(parts[5]);
        this.sensorReading = new SensorReading(temperature, pressure, humidity, co, no2, so2);
    }

    public SensorReading getSensorReading() {
        return sensorReading;
    }

    public SensorReading averageWithNeighbour(SensorReading neighbourReading) {
        SensorReading myReading = getSensorReading();
        Double avgTemp;
        Double avgPressure;
        Double avgHumidity;
        Double avgCo;
        Double avgNo2;
        Double avgSo2;

        // Temperature
        if (myReading.getTemperature() == 0 && neighbourReading.getTemperature() == 0) {
            avgTemp = 0.0;
        } else if (myReading.getTemperature() == 0) {
            avgTemp = neighbourReading.getTemperature();
        } else if (neighbourReading.getTemperature() == 0) {
            avgTemp = myReading.getTemperature();
        } else {
            avgTemp = (myReading.getTemperature() + neighbourReading.getTemperature()) / 2.0;
        }

        // Pressure
        if (myReading.getPressure() == 0 && neighbourReading.getPressure() == 0) {
            avgPressure = 0.0;
        } else if (myReading.getPressure() == 0) {
            avgPressure = neighbourReading.getPressure();
        } else if (neighbourReading.getPressure() == 0) {
            avgPressure = myReading.getPressure();
        } else {
            avgPressure = (myReading.getPressure() + neighbourReading.getPressure()) / 2.0;
        }

        // Humidity
        if (myReading.getHumidity() == 0 && neighbourReading.getHumidity() == 0) {
            avgHumidity = 0.0;
        } else if (myReading.getHumidity() == 0) {
            avgHumidity = neighbourReading.getHumidity();
        } else if (neighbourReading.getHumidity() == 0) {
            avgHumidity = myReading.getHumidity();
        } else {
            avgHumidity = (myReading.getHumidity() + neighbourReading.getHumidity()) / 2.0;
        }

        // CO
        if (myReading.getCo() == 0 && neighbourReading.getCo() == 0) {
            avgCo = 0.0;
        } else if (myReading.getCo() == 0) {
            avgCo = neighbourReading.getCo();
        } else if (neighbourReading.getCo() == 0) {
            avgCo = myReading.getCo();
        } else {
            avgCo = (myReading.getCo() + neighbourReading.getCo()) / 2.0;
        }

        // NO2
        if (myReading.getNo2() == 0 && neighbourReading.getNo2() == 0) {
            avgNo2 = 0.0;
        } else if (myReading.getNo2() == 0) {
            avgNo2 = neighbourReading.getNo2();
        } else if (neighbourReading.getNo2() == 0) {
            avgNo2 = myReading.getNo2();
        } else {
            avgNo2 = (myReading.getNo2() + neighbourReading.getNo2()) / 2.0;
        }

        // SO2
        if (myReading.getSo2() == 0 && neighbourReading.getSo2() == 0) {
            avgSo2 = 0.0;
        } else if (myReading.getSo2() == 0) {
            avgSo2 = neighbourReading.getSo2();
        } else if (neighbourReading.getSo2() == 0) {
            avgSo2 = myReading.getSo2();
        } else {
            avgSo2 = (myReading.getSo2() + neighbourReading.getSo2()) / 2.0;
        }

        return new SensorReading(avgTemp, avgPressure, avgHumidity, avgCo, avgNo2, avgSo2);
    }

    public void sendReadingToRestServer(String serverUrl, SensorReading reading, int sensorPort) {
        try {
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("temperature", reading.getTemperature());
            jsonMap.put("pressure", reading.getPressure());
            jsonMap.put("humidity", reading.getHumidity());
            jsonMap.put("co", reading.getCo());
            jsonMap.put("no2", reading.getNo2());
            jsonMap.put("so2", reading.getSo2());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(jsonMap, headers);

            ResponseEntity<Void> response = restTemplate.exchange(
                    serverUrl + "?port=" + sensorPort,
                    HttpMethod.POST,
                    entity,
                    Void.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED) {
                System.out.println("Očitanje uspješno poslano. Location: " + response.getHeaders().getLocation());
            } else if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                System.out.println("Senzor s tim portom nije pronađen na serveru. Očitanje nije spremljeno.");
            }
        } catch (Exception e) {
            System.err.println("Greška pri slanju očitanja: " + e.getMessage());
        }
    }
}
