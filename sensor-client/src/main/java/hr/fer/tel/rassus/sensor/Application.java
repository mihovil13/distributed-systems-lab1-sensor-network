package hr.fer.tel.rassus.sensor;

import hr.fer.tel.rassus.sensor.beans.SensorReading;
import hr.fer.tel.rassus.sensor.beans.Sensor;
import hr.fer.tel.rassus.sensor.grpc.SensorGrpcClient;
import hr.fer.tel.rassus.sensor.grpc.SensorGrpcServer;
import hr.fer.tel.rassus.sensor.grpc.SensorGrpcService;
import hr.fer.tel.rassus.sensor.services.NeighbourService;
import hr.fer.tel.rassus.sensor.services.RegistrationService;
import hr.fer.tel.rassus.sensor.services.SensorReadingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.Optional;


@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.setDefaultProperties(Map.of(
                "spring.main.web-application-type", "none"
        ));
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
    public CommandLineRunner run(RegistrationService registrationService,
                                 SensorReadingService readingService,
                                 NeighbourService neighbourService) {
        return args -> {
            String serverUrl = "http://localhost:8090";
            String ip = "127.0.0.1";
            int port = findFreePort();

            registrationService.registerSensor(serverUrl, ip, port);
            System.out.println("Senzor registriran s portom: " + port);

            SensorGrpcServer grpcServer = new SensorGrpcServer(new SensorGrpcService(readingService), port);
            grpcServer.start();
            System.out.println("gRPC server pokrenut na portu: " + port);

            new Thread(() -> {
                Sensor nearest = null;

                while (true) {
                    try {
                        readingService.makeReading();
                        SensorReading myReading = readingService.getSensorReading();
                        if (nearest == null) {
                            Optional<Sensor> nearestOpt = neighbourService.getNearestNeighbour(serverUrl, registrationService.getMySensorId());
                            nearest = nearestOpt.orElse(null);
                            if (nearest != null) {
                                System.out.println("Pronađen početni susjed: " + nearest);
                            } else {
                                System.out.println("Još nemam susjeda.");
                            }
                        }
                        if (nearest != null) {
                            try {
                                SensorGrpcClient grpcClient = new SensorGrpcClient(nearest.getIp(), nearest.getPort(), port);
                                SensorReading neighbourReading = grpcClient.requestSensorRequest();
                                grpcClient.stop();
                                SensorReading avgReading = readingService.averageWithNeighbour(neighbourReading);
                                System.out.println("Prosjek sa susjedom: " + avgReading);
                                readingService.sendReadingToRestServer("http://localhost:8090/readings", avgReading, port);
                            } catch (Exception e) {
                                System.err.println("Ne mogu doći do susjeda, koristim svoja očitanja.");
                                readingService.sendReadingToRestServer("http://localhost:8090/readings", myReading, port);

                            }
                        } else {
                            readingService.sendReadingToRestServer("http://localhost:8090/readings", myReading, port);
                        }

                        Thread.sleep(10000);
                    } catch (Exception e) {
                        System.err.println("Greška u petlji za očitanja: " + e.getMessage());
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }).start();


            grpcServer.blockUntilShutdown();
        };

    }
}
