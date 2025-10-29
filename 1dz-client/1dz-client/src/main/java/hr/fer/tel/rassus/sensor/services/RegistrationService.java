package hr.fer.tel.rassus.sensor.services;

import hr.fer.tel.rassus.sensor.beans.Sensor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Service
public class RegistrationService {
    private final RestTemplate restTemplate = new RestTemplate();
    private double LAT_MIN = 45.75;
    private double LAT_MAX = 45.85;
    private double LON_MIN = 15.87;
    private double LON_MAX = 16.00;
    private Long mySensorId;

    public Long getMySensorId() {
        return mySensorId;
    }

    public void setMySensorId(Long mySensorId) {
        this.mySensorId = mySensorId;
    }

    public String registerSensor(String serverUrl, String ip, int port) {
        Random random = new Random();
        double latitude = LAT_MIN + random.nextDouble(LAT_MAX - LAT_MIN);
        Random random2 = new Random();
        double longitude = LON_MIN + random2.nextDouble(LON_MAX - LON_MIN);

        Sensor request = new Sensor(latitude, longitude, ip, port);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Sensor> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(serverUrl + "/sensors/register", entity, String.class);

            if (response.getStatusCode() == HttpStatus.CREATED) {
                String location = response.getHeaders().getLocation().toString();
                System.out.println("Senzor uspješno registriran! Odgovor: " + response.getStatusCode() + " resurs " + location);
                String[] parts = location.split("/");
                setMySensorId(Long.parseLong(parts[parts.length - 1]));
                return location;
            } else {
                System.err.println("Neuspješna registracija: " + response.getStatusCode());
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Greška prilikom registracije: " + e.getMessage());
            System.exit(1);
        }

        return null;
    }
}

