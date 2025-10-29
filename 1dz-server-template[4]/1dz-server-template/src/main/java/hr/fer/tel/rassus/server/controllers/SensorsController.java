package hr.fer.tel.rassus.server.controllers;

import hr.fer.tel.rassus.server.beans.Sensor;
import hr.fer.tel.rassus.server.services.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sensors")
public class SensorsController {
    private final SensorService sensorService;

    public SensorsController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerSensor(@RequestBody Sensor sensor) {
        Sensor created = sensorService.registerSensor(sensor);
        URI location = URI.create("/sensors/" + created.getId());
        System.out.printf("Registriran senzor: ID=%d, lat=%.5f, lon=%.5f, port=%d%n",
                created.getId(), created.getLatitude(), created.getLongitude(), created.getPort());
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<Sensor>> getAllSensors() {
        return ResponseEntity.ok(sensorService.getAllSensors());
    }

    @GetMapping("/nearest")
    public ResponseEntity<Map<String, Object>> getNearestSensor(@RequestParam("sensorId") Long sensorId) {
        Sensor sensor = sensorService.getSensorById(sensorId);
        if (sensor == null) {
            return ResponseEntity.notFound().build();
        }
        Sensor nearest = sensorService.findNearestNeighbour(sensor);
        if (nearest == null) {
            return ResponseEntity.noContent().build();
        }
        Map<String, Object> response = Map.of(
                "latitude", nearest.getLatitude(),
                "longitude", nearest.getLongitude(),
                "ip", nearest.getIp(),
                "port", nearest.getPort()
        );
        return ResponseEntity.ok(response);
    }

}

