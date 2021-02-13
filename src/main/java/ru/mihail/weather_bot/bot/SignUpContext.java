package ru.mihail.weather_bot.bot;

import org.apache.catalina.User;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.mihail.weather_bot.models.UserChat;

public class SignUpContext {

    private UserChat userChat;

    public SignUpContext(UserChat userChat) {
        this.userChat = userChat;
    }

    public void enterUsername(Message message) {
        if (userChat.getState() == 0) {
            WeatherBot weatherBot = new WeatherBot();
            weatherBot.sendMsg(message, "Enter your name:");
            if (!StringUtils.isEmpty(message.getText())) {
                userChat.setUsername(message.getText());
                userChat.setState(1);
            }
        }
    }

    public void enterCity(Message message) {
        if (userChat.getState() == 0) {
            WeatherBot weatherBot = new WeatherBot();
            weatherBot.sendMsg(message, "Enter your name:");
            if (!StringUtils.isEmpty(message.getText())) {
                userChat.setUsername(message.getText());
                userChat.setState(2);
            }
        }
    }
}

