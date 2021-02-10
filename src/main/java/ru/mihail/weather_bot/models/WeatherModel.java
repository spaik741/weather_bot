package ru.mihail.weather_bot.models;

public class WeatherModel {

    private String name;

    private int temp;

    private int pressure;

    private int humidity;

    private String icon;

    public WeatherModel() {
    }

    public WeatherModel(String name, int temp, int pressure, int humidity) {
        this.name = name;
        this.temp = temp;
        this.pressure = pressure;
        this.humidity = humidity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "In city " + name + "weather:\n" + "temp: " + temp +
                "\npressure: " + pressure +
                "\nhumidity: " + humidity;
    }
}
