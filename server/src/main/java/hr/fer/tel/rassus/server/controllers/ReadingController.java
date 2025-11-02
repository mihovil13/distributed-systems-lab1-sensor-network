package hr.fer.tel.rassus.server.controllers;

import hr.fer.tel.rassus.server.beans.Reading;
import hr.fer.tel.rassus.server.services.ReadingService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/readings")
public class ReadingController {

    private final ReadingService readingService;

    public ReadingController(ReadingService readingService) {
        this.readingService = readingService;
    }

    @PostMapping
    public ResponseEntity<Void> addReading(@RequestBody Reading reading, @RequestParam int port) {
        try {
            Reading saved = readingService.saveReading(reading, port);
            URI location = URI.create("/readings/" + saved.getSensorId());
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(location);
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/{sensorId}")
    public ResponseEntity<List<Map<String, Object>>> getReadings(@PathVariable Long sensorId) {
        List<Reading> readings = readingService.getReadingsBySensorId(sensorId);

        if (readings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<Map<String, Object>> response = readings.stream()
                .map(r -> Map.<String, Object>of(
                        "id", r.getId(),
                        "temperature", r.getTemperature(),
                        "pressure", r.getPressure(),
                        "humidity", r.getHumidity(),
                        "co", r.getCo(),
                        "no2", r.getNo2(),
                        "so2", r.getSo2()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

}
