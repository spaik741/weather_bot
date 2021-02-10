package ru.mihail.weather_bot.controllers;

import org.json.JSONObject;
import ru.mihail.weather_bot.models.WeatherModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class RequestRestApi {

    public WeatherModel getWeather(String nameCity){
        String token = "08e64a0ab63aa18f63d3116e8af513c5";
        WeatherModel model = new WeatherModel();
        try {
            URL url = new URL("http://api.openweathermap.org/data/2.5/" +
                    "weather?q="+ nameCity + "&units=metric&appid=" + token);
            Scanner in = new Scanner((InputStream) url.getContent());
            String result = "";
            while (in.hasNext()) {
                result += in.nextLine();
            }

            JSONObject jsonObject = new JSONObject(result);
            model.setName(jsonObject.getString("name"));
            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
            model.setTemp(jsonObjectMain.getInt("temp"));
            model.setPressure(jsonObjectMain.getInt("pressure"));
            model.setHumidity(jsonObjectMain.getInt("humidity"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return model;
    }

}
