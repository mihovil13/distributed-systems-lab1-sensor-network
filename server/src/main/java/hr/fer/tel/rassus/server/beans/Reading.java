package hr.fer.tel.rassus.server.beans;

import jakarta.persistence.*;

@Entity
public class Reading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sensorId;

    private double temperature;
    private double pressure;
    private double humidity;
    private double co;
    private double no2;
    private double so2;

    public Reading() {}

    public Reading(Long sensorId, double temperature, double pressure, double humidity,
                   double co, double no2, double so2) {
        this.sensorId = sensorId;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.co = co;
        this.no2 = no2;
        this.so2 = so2;
    }

    public Long getId() { return id; }
    public Long getSensorId() { return sensorId; }
    public double getTemperature() { return temperature; }
    public double getPressure() { return pressure; }
    public double getHumidity() { return humidity; }
    public double getCo() { return co; }
    public double getNo2() { return no2; }
    public double getSo2() { return so2; }

    public void setId(Long id) { this.id = id; }
    public void setSensorId(Long sensorId) { this.sensorId = sensorId; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public void setPressure(double pressure) { this.pressure = pressure; }
    public void setHumidity(double humidity) { this.humidity = humidity; }
    public void setCo(double co) { this.co = co; }
    public void setNo2(double no2) { this.no2 = no2; }
    public void setSo2(double so2) { this.so2 = so2; }
}
