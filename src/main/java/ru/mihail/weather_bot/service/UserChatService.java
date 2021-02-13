package ru.mihail.weather_bot.service;

import ru.mihail.weather_bot.models.UserChat;

public interface UserChatService {

    public void saveUser(UserChat userChat);

    public UserChat getUser(Long chatId);
}
