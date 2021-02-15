package com.example.webrtc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * Created by DELL(mxd) on 2021/2/14 19:16
 */
@Configuration
public class WebSocketConfig {

    /**
     * 开启WebSocket支持
     * @return 1
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
