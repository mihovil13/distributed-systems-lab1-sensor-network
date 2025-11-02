package hr.fer.tel.rassus.server.beans;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double latitude;
    private double longitude;
    private String ip;
    private int port;

    public Sensor() {}

    public Sensor(Long id, double latitude, double longitude, String ip, int port) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ip = ip;
        this.port = port;
    }

    public Long getId() { return id; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getIp() { return ip; }
    public int getPort() { return port; }

    public void setId(Long id) { this.id = id; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setIp(String ip) { this.ip = ip; }
    public void setPort(int port) { this.port = port; }

}
