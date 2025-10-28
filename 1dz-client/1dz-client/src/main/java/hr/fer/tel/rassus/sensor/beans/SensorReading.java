package hr.fer.tel.rassus.sensor.beans;

import org.springframework.context.annotation.Bean;

public class SensorReading {
    private double temperature;
    private double pressure;
    private double humidity;
    private double co;
    private double no2;
    private double so2;

    public SensorReading(double temperature, double pressure, double humidity, double co, double no2, double so2) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.co = co;
        this.no2 = no2;
        this.so2 = so2;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getCo() {
        return co;
    }

    public double getSo2() {
        return so2;
    }

    public double getNo2() {
        return no2;
    }

    @Override
    public String toString() {
        return String.format("Temp: %.2f, Pressure: %.2f, Humidity: %.2f, CO: %.2f, NO2: %.2f, SO2: %.2f",
                temperature, pressure, humidity, co, no2, so2);
    }
}