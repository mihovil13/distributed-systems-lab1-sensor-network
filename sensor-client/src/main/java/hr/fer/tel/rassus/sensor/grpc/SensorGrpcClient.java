package hr.fer.tel.rassus.sensor.grpc;

import hr.fer.tel.rassus.sensor.beans.Sensor;
import hr.fer.tel.rassus.sensor.beans.SensorReading;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;


import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SensorGrpcClient {
    private static final Logger logger = Logger.getLogger(SensorGrpcClient.class.getName());
    private final ManagedChannel channel;
    private final SensorServiceGrpc.SensorServiceBlockingStub sensorServiceBlockingStub;
    private final int requesterPort;

    public SensorGrpcClient(String host, int port, int requesterPort) {
        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.sensorServiceBlockingStub = SensorServiceGrpc.newBlockingStub(channel);
        this.requesterPort = requesterPort;
    }

    public void stop() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public SensorReading requestSensorRequest() {
        SensorRequest request = SensorRequest.newBuilder()
                .setRequester(String.valueOf(requesterPort))
                .build();

        logger.info("Sending request from port: " + request.getRequester());
        try {
            SensorReadingMessage response = sensorServiceBlockingStub.fetchLatestReading(request);
            logger.info(String.format("Received reading: Temp=%.2f, Pressure=%.2f, Humidity=%.2f, CO=%.2f, NO2=%.2f, SO2=%.2f",
                    response.getTemperature(),
                    response.getPressure(),
                    response.getHumidity(),
                    response.getCo(),
                    response.getNo2(),
                    response.getSo2()));
            return new SensorReading(response.getTemperature(),
                    response.getPressure(),
                    response.getHumidity(),
                    response.getCo(),
                    response.getNo2(),
                    response.getSo2());
        } catch (StatusRuntimeException e) {
            logger.info("RPC failed: " + e.getMessage());
        }
        return null;
    }
}