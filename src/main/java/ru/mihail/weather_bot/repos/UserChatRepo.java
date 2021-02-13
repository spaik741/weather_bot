package ru.mihail.weather_bot.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mihail.weather_bot.models.UserChat;

public interface UserChatRepo extends JpaRepository<UserChat, Long> {

    public UserChat findByChatId(Long chatId);
}
