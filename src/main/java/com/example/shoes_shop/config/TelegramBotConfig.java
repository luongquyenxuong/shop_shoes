package com.example.shoes_shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfig {
    @Value("${spring.telegram.bot.username}")
    private String botUsername;

    @Value("${spring.telegram.bot.token}")
    private String botToken;

    @Value("${spring.telegram.chat.id}")
    private String chatId;

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot (botUsername, botToken, chatId);
    }
}
