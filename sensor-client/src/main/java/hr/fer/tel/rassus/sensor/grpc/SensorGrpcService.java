package hr.fer.tel.rassus.sensor.grpc;

import hr.fer.tel.rassus.sensor.beans.SensorReading;
import hr.fer.tel.rassus.sensor.services.SensorReadingService;
import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;

public class SensorGrpcService extends SensorServiceGrpc.SensorServiceImplBase {
    private static final Logger logger = Logger.getLogger(SensorGrpcService.class.getName());
    private final SensorReadingService readingService;

    public SensorGrpcService(SensorReadingService readingService) {
        this.readingService = readingService;
    }
    @Override
    public void fetchLatestReading(SensorRequest request, StreamObserver<SensorReadingMessage> responseObserver) {
        logger.info("Got a new message: " + request.getRequester());
        SensorReading reading = readingService.getSensorReading();

        SensorReadingMessage message = SensorReadingMessage.newBuilder()
                .setTemperature(reading.getTemperature())
                .setPressure(reading.getPressure())
                .setHumidity(reading.getHumidity())
                .setCo(reading.getCo())
                .setNo2(reading.getNo2())
                .setSo2(reading.getSo2())
                .build();

        responseObserver.onNext(message);
        logger.info("Responding with: " + String.format("Temp=%.2f, Pressure=%.2f, Humidity=%.2f, CO=%.2f, NO2=%.2f, SO2=%.2f",
                reading.getTemperature(),
                reading.getPressure(),
                reading.getHumidity(),
                reading.getCo(),
                reading.getNo2(),
                reading.getSo2()));
        responseObserver.onCompleted();
    }
}
