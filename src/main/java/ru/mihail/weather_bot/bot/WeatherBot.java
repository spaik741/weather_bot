package ru.mihail.weather_bot.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.mihail.weather_bot.controllers.RequestRestApi;
import ru.mihail.weather_bot.models.UserChat;
import ru.mihail.weather_bot.models.WeatherModel;
import ru.mihail.weather_bot.service.UserChatService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
@PropertySource("classpath:telegram.properties")
public class WeatherBot extends TelegramLongPollingBot {

    @Autowired
    private UserChatService userChatService;

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    private final int[] SIGN_UP_LIST = {1, 2, 3};

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
        int state = userChat.getState();
        boolean contains = IntStream.of(SIGN_UP_LIST).anyMatch(x -> x == state);
        if ("/start".equals(text)) {
            sendMsg(message, "Hello my dear friend!\n" +
                    "Here you can know weather in you city.\n" +
                    "Example: 'weather in Kazan' or else you sing up 'weather'");
        } else if ("/sign_up".equals(text) || contains) {
            signUp(message, userChat);
        } else {
            if (text.contains("weather")) {
                try {
                    if (text.contains("in")) {
                        String city = text.split("in ")[1];
                        WeatherModel model = new RequestRestApi().getWeather(city);
                        sendMsg(message, model.toString());
                    } else {
                        WeatherModel model = new RequestRestApi().getWeather(userChat.getCity());
                        sendMsg(message, model.toString());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    sendMsg(message, "City: '" + userChat.getCity() + "' not found!");
                }
            }
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

    private void signUp(Message message, UserChat userChat) {

        if (message.getText().contains("/sign_up")) {
            userChat.setState(0);
        }
        int state = userChat.getState();
        if (state == 0) {
            userChat.setState(1);
            userChat.setChatId(message.getChatId());
            userChatService.saveUser(userChat);
            sendMsg(message, "Enter your name:");
        } else if (state == 1) {
            userChat.setUsername(message.getText());
            userChat.setState(2);
            userChatService.saveUser(userChat);
            sendMsg(message, "Enter your city:");
        } else if (state == 2) {
            setKeybordYesNo(message, userChat);
            userChat.setState(3);
            userChatService.saveUser(userChat);
        } else if (state == 3) {
            if (message.getText().contains("Yes")) {
                userChat.setState(4);
                userChatService.saveUser(userChat);
                sendMsg(message, "Everything went well!");
            } else {
                userChat.setState(0);
                userChatService.saveUser(userChat);
            }
        }
    }

    private void setKeybordYesNo(Message message, UserChat userChat) {
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
    }
}
