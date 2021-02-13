package ru.mihail.weather_bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mihail.weather_bot.models.UserChat;
import ru.mihail.weather_bot.repos.UserChatRepo;

@Service
public class UserChatServiceImpl implements UserChatService {

    @Autowired
    private UserChatRepo userChatRepo;


    @Override
    @Transactional
    public void saveUser(UserChat userChat) {
        userChatRepo.save(userChat);
    }

    @Override
    @Transactional
    public UserChat getUser(Long chatId) {
        UserChat userChat = userChatRepo.findByChatId(chatId);
        return userChat;
    }
}
