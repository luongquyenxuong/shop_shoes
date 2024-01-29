package com.example.shoes_shop.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TelegramBot extends TelegramLongPollingBot {

    private  String botUsername;
    private  String botToken;
    private  String chatId;



    public void sendMessage(String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);


        try {
            execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Xử lý các sự kiện nhận được (nếu cần)
    }
}
