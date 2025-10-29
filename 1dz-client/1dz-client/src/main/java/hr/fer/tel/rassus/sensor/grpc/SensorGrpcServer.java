package hr.fer.tel.rassus.sensor.grpc;

import hr.fer.tel.rassus.sensor.services.SensorReadingService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SensorGrpcServer{
    private static final Logger logger = Logger.getLogger(SensorGrpcServer.class.getName());
    private Server server;
    private final SensorGrpcService service;
    private final int serverPort;
    public SensorGrpcServer(SensorGrpcService service, int serverPort) {
        this.service = service;
        this.serverPort = serverPort;
    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(serverPort)
                .addService(service)
                .build()
                .start();

        logger.info("Server started on " + serverPort);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down gRPC server since JVM is shutting down");
            try {
                SensorGrpcServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("Server shut down");
        }));
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final SensorGrpcServer server = new SensorGrpcServer(new SensorGrpcService(new SensorReadingService()), 3000);
        server.start();
        server.blockUntilShutdown();
    }

}
