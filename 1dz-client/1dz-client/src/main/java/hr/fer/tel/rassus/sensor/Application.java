package hr.fer.tel.rassus.sensor;

import hr.fer.tel.rassus.sensor.beans.SensorReading;
import hr.fer.tel.rassus.sensor.beans.Senzor;
import hr.fer.tel.rassus.sensor.services.NeighbourService;
import hr.fer.tel.rassus.sensor.services.RegistrationService;
import hr.fer.tel.rassus.sensor.services.SensorReadingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.Optional;


@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    int port = findFreePort();
    SpringApplication app = new SpringApplication(Application.class);
    app.setDefaultProperties(Map.of("server.port", port));
    app.run(args);
  }

  public static int findFreePort() {
    try (ServerSocket socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    } catch (IOException e) {
      throw new RuntimeException("Nije moguće pronaći slobodan port", e);
    }
  }

  @Bean
  public CommandLineRunner run(RegistrationService registrationService, Environment env, SensorReadingService readingService, NeighbourService neighbourService) {
    return args -> {
      String serverUrl = "http://localhost:8090";
      String ip = "127.0.0.1";

      String portStr = env.getProperty("local.server.port");
      int port = Integer.parseInt(portStr);

      registrationService.registerSensor(serverUrl, ip, port);
      System.out.println("Senzor registriran s portom: " + port);

      Optional<Senzor> nearestOpt = neighbourService.getNearestNeighbour(serverUrl, registrationService.getMySensorId());

      if (nearestOpt.isPresent()) {
        Senzor nearest = nearestOpt.get();
        // Pošalji zahtjev susjedu za očitanja
      } else {
        // Nema susjeda – koristi vlastita očitanja
      }
      while (true) {
        try {
          SensorReading reading = readingService.getReading();
          System.out.println(reading);

          Thread.sleep(10000); // pauza 10 sekunda
        } catch (Exception e) {
          System.err.println("Greška pri generiranju/šaljenju očitanja: " + e.getMessage());
        }
      }

    };
  }
}
