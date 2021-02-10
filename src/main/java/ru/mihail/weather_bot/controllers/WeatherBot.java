package ru.mihail.weather_bot.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.mihail.weather_bot.models.WeatherModel;

@Component
@PropertySource("classpath:telegram.properties")
public class WeatherBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        message.getText();
        switch (message.getText()){
            case "/start":
                try {
                    execute(new SendMessage().setChatId(message.getChatId()).setText("Hello my dear friend!\n" +
                            "Here you can know weather in you city.\n" +
                            "Example:weather in Kazan"));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            default:
                String city = message.getText().split("in ")[1];
                WeatherModel model = new RequestRestApi().getWeather(city);
                try {
                    execute(new SendMessage().setChatId(message.getChatId()).setText(model.toString()));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
        }
    }
}
