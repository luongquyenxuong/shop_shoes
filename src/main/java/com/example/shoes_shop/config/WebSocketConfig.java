package com.example.shoes_shop.config;


import com.example.shoes_shop.entity.User;
import com.example.shoes_shop.exception.BadRequestException;

import com.example.shoes_shop.security.CustomUserDetails;
import com.example.shoes_shop.security.JwtTokenUtil;
import com.example.shoes_shop.service.WebSocketService;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.GrantedAuthority;

import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Collection;
import java.util.Objects;


@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue/");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {

                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                assert accessor != null;
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authToken = Objects.requireNonNull(accessor.getNativeHeader("authorization")).toString();

                    if (authToken != null && authToken.startsWith("[Bearer ")) {
                        String token = authToken.substring(8, authToken.length() - 1);
                        if (jwtTokenUtil.validateToken(token)) {
                            Claims claims = jwtTokenUtil.getClaimsFromToken(token);
                            UsernamePasswordAuthenticationToken user = jwtTokenUtil.getAuthentication(claims);

                            Collection<GrantedAuthority> roles = user.getAuthorities();
                            CustomUserDetails u = (CustomUserDetails) user.getPrincipal();
                            if (roles.stream().noneMatch(authority -> "ROLE_USER".equals(authority.getAuthority())) &&
                                    roles.stream().noneMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()))) {
                                throw new BadRequestException("Access Denied: You do not have permission to connect.");
                            }



                            accessor.setUser(user);

                        }
                    }else{
                        throw new BadRequestException("Access Denied: You do not have permission to connect.");
                    }
                }

                return message;
            }
        });
    }

}

