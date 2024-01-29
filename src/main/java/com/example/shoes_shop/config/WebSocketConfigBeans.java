//package com.example.shoes_shop.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.messaging.support.ExecutorSubscribableChannel;
//
//@Configuration
//public class WebSocketConfigBeans {
//    @Bean
//    public SimpMessagingTemplate simpMessagingTemplate() {
//        return new SimpMessagingTemplate(messageChannel());
//    }
//
//    @Bean
//    public MessageChannel messageChannel() {
//        return new ExecutorSubscribableChannel();
//    }
//}
