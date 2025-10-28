package hr.fer.tel.rassus.sensor.services;

import hr.fer.tel.rassus.sensor.beans.SensorReading;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
@Service
public class SensorReadingService {

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

    public SensorReading getReading() throws IOException {

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


        return new SensorReading(temperature, pressure, humidity, co, no2, so2);
    }

}
