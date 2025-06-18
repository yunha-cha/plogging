package com.website.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 웹 소켓 설정 파일입니다.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){

        registry.addEndpoint("/ws") //웹소켓과 연결하기 위한 엔드포인트이다.
                .setAllowedOrigins("http://localhost:3000") //3000포트 cors 설정
                .withSockJS();  // 소켓을 지원하지 않는 브라우저를 도와줄 수 있는 라이브러리 사용
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트에서 서버로 메세지를 보낼 때 사용하는 경로를 지정한다
        // 클라이언트에서 요청 시 "http://localhost:3000/send/컨트롤러매핑" 으로 보내야 한다.
        registry.setApplicationDestinationPrefixes("/send");

        // 클라이언트에서 구독할 첫 번째 엔드포인트이다.
        // 클라이언트에서 구독 시 "/sub/엔드포인트" 로 구독해야한다.
        registry.enableSimpleBroker("/sub");
    }
}
