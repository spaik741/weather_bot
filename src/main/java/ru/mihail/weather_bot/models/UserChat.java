package ru.mihail.weather_bot.models;



import javax.persistence.*;

@Entity
@Table(name = "user_chat")
public class UserChat {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Column(name = "username")
    private String username;

    @Column(name = "city")
    private String city;

    @Column(name = "chatId")
    private long chatId;

    @Column(name = "state")
    private int state;

    public UserChat() {
    }

    public UserChat(String username, long chatId,int state) {
        this.username = username;
        this.chatId = chatId;
    }
    public int getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
