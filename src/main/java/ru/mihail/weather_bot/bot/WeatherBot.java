package ru.mihail.weather_bot.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.mihail.weather_bot.controllers.RequestRestApi;
import ru.mihail.weather_bot.models.UserChat;
import ru.mihail.weather_bot.models.WeatherModel;
import ru.mihail.weather_bot.service.UserChatService;

import java.util.ArrayList;
import java.util.List;

@Component
@PropertySource("classpath:telegram.properties")
public class WeatherBot extends TelegramLongPollingBot {

    @Autowired
    private UserChatService userChatService;

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

        UserChat userChat = userChatService.getUser(message.getChatId());
        if (userChat == null) {
            userChat = new UserChat();
            userChat.setState(0);
            userChatService.saveUser(userChat);
        }
        String text = message.getText();
        if ("/start".equals(text)) {
            sendMsg(message, "Hello my dear friend!\n" +
                    "Here you can know weather in you city.\n" +
                    "Example:weather in Kazan");
        } else if ("/sign_up".equals(text)) {
            userChat.setState(1);
            userChat.setChatId(message.getChatId());
            userChatService.saveUser(userChat);
            sendMsg(message, "Enter your name:");
        } else if (userChat.getState() == 1) {
            userChat.setUsername(message.getText());
            userChat.setState(2);
            userChatService.saveUser(userChat);
            sendMsg(message, "Enter your city:");
        } else if (userChat.getState() == 2) {
            userChat.setCity(message.getText());
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
            sendMessage.setText("Your name:" + userChat.getUsername() +
                    "\nYour city:" + userChat.getCity() +
                    "\nSave data?");
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(false);

            List<KeyboardRow> keyboardRows = new ArrayList<>();
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(new KeyboardButton("Yes" + "\uD83D\uDC4D"));
            keyboardRow.add(new KeyboardButton("No" + "\uD83D\uDC4E"));

            keyboardRows.add(keyboardRow);
            replyKeyboardMarkup.setKeyboard(keyboardRows);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            userChat.setState(3);
            userChatService.saveUser(userChat);
        } else if (userChat.getState() == 3) {
            if (message.getText().contains("Yes")) {
                userChat.setState(4);
                userChatService.saveUser(userChat);
                sendMsg(message, "Everything went well!");
            } else {
                userChat.setState(0);
                userChatService.saveUser(userChat);
            }
            ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
            replyKeyboardRemove.getRemoveKeyboard();
        } else {
            String city = message.getText().split("in ")[1];
            WeatherModel model = new RequestRestApi().getWeather(city);
            sendMsg(message, model.toString());
        }
    }

    public void sendMsg(Message message, String text) {
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(message.getChatId());
            sendMessage.setReplyToMessageId(message.getMessageId());
            sendMessage.setText(text);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

//    private void signUp(Message message) {
//        UserChat userChat = userChatService.getUser(message.getChatId());
//        int stateUserChate = 0;
//        if (userChat != null) {
//            stateUserChate = userChat.getState();
//        }else {
//            userChat = new UserChat();
//        }
//        while (stateUserChate != 4) {
//            if (stateUserChate == 0) {
//                sendMsg(message, "Enter your name:");
//                if (!StringUtils.isEmpty(message.getText())) {
//                    userChat.setUsername(message.getText());
//                    stateUserChate = 1;
//                    userChat.setState(stateUserChate);
//                }
//            }
//            if (stateUserChate == 1) {
//                sendMsg(message, "Enter your city:");
//                if (!StringUtils.isEmpty(message.getText())) {
//                    userChat.setCity(message.getText());
//                    stateUserChate = 2;
//                    userChat.setState(stateUserChate);
//                }
//            }
//            if (stateUserChate == 2) {
//                sendMsg(message, "Your name:" + userChat.getUsername() +
//                        "\nYour city:" + userChat.getCity() +
//                        "\nSave data?");
//                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
//                SendMessage sendMessage = new SendMessage();
//                sendMessage.setReplyMarkup(replyKeyboardMarkup);
//                replyKeyboardMarkup.setSelective(true);
//                replyKeyboardMarkup.setResizeKeyboard(true);
//                replyKeyboardMarkup.setOneTimeKeyboard(false);
//
//                List<KeyboardRow> keyboardRows = new ArrayList<>();
//                KeyboardRow keyboardRow = new KeyboardRow();
//                keyboardRow.add(new KeyboardButton("Yes" + "\\xF0\\x9F\\x91\\x8D"));
//                keyboardRow.add(new KeyboardButton("No" + "\\xF0\\x9F\\x91\\x8E"));
//
//                keyboardRows.add(keyboardRow);
//                replyKeyboardMarkup.setKeyboard(keyboardRows);
//                stateUserChate = 3;
//                userChat.setState(stateUserChate);
//            }
//            if (stateUserChate == 3) {
//                if (message.getText().contains("Yes")) {
//                    userChat.setChatId(message.getChatId());
//                    userChatService.saveUser(userChat);
//                    sendMsg(message, "Everything went well!");
//                    stateUserChate = 4;
//                    userChat.setState(stateUserChate);
//                }
//            }
//        }
//    }
}
