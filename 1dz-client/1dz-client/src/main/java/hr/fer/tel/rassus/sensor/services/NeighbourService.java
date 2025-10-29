package hr.fer.tel.rassus.sensor.services;

import hr.fer.tel.rassus.sensor.beans.Sensor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class NeighbourService {
    private final RestTemplate restTemplate = new RestTemplate();

    public Optional<Sensor> getNearestNeighbour(String serverUrl, Long id) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    serverUrl + "/sensors/nearest?sensorId=" + id, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                // Pretvori Map u SensorReading
                double latitude = ((Number) body.get("latitude")).doubleValue();
                double longitude = ((Number) body.get("longitude")).doubleValue();
                String ip = (String) body.get("ip");
                int port = ((Number) body.get("port")).intValue();

                Sensor nearest = new Sensor(latitude, longitude, ip, port);
                System.out.println("Najbliži susjed: " + nearest.toString());
                return Optional.of(nearest);
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                System.out.println("Nema dostupnog susjeda za senzor ID: " + id);
                return Optional.empty();
            } else {
                System.err.println("Greška prilikom dohvaćanja susjeda: " + response.getStatusCode());
                return Optional.empty();
            }
        } catch (Exception e) {
            System.err.println("HTTP zahtjev nije uspio: " + e.getMessage());
            return Optional.empty();
        }
    }

}
