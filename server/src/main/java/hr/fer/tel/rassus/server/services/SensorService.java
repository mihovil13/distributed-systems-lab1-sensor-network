package hr.fer.tel.rassus.server.services;

import hr.fer.tel.rassus.server.beans.Sensor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorService {

    private final SensorRepository repository;

    public SensorService(SensorRepository repository) {
        this.repository = repository;
    }

    public Sensor registerSensor(Sensor sensor) {
        return repository.save(sensor);
    }

    public List<Sensor> getAllSensors() {
        return repository.findAll();
    }

    public Sensor getSensorById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Senzor nije pronađen"));
    }

    public Sensor findNearestNeighbour(Sensor sensor) {
        double R = 6371;
        List<Sensor> sensors = repository.findAll();
        Sensor nearest = null;
        double minDistance = Double.MAX_VALUE;
        for (Sensor s : sensors) {
            if (s.getId().equals(sensor.getId())) continue;
            double lon1 = Math.toRadians(sensor.getLongitude());
            double lat1 = Math.toRadians(sensor.getLatitude());
            double lon2 = Math.toRadians(s.getLongitude());
            double lat2 = Math.toRadians(s.getLatitude());
            double dlon = lon2 - lon1;
            double dlat = lat2 - lat1;
            double a = Math.pow(Math.sin(dlat / 2), 2) +
                    Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = R * c;

            if (distance < minDistance) {
                minDistance = distance;
                nearest = s;
            }
        }

        return nearest;
    }

}

