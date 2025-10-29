package hr.fer.tel.rassus.server.services;

import hr.fer.tel.rassus.server.beans.Reading;
import hr.fer.tel.rassus.server.beans.Sensor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadingService {

    private final ReadingRepository readingRepository;
    private final SensorService sensorService;

    public ReadingService(ReadingRepository readingRepository, SensorService sensorService) {
        this.readingRepository = readingRepository;
        this.sensorService = sensorService;
    }

    public Reading saveReading(Reading reading, int port) {
        List<Sensor> sensors = sensorService.getAllSensors();
        Sensor sensor = sensors.stream()
                .filter(s -> s.getPort() == port)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sensor s portom " + port + " nije pronađen"));

        reading.setSensorId(sensor.getId());
        return readingRepository.save(reading);
    }

    public List<Reading> getReadingsBySensorId(Long sensorId) {
        return readingRepository.findBySensorId(sensorId);
    }
}
