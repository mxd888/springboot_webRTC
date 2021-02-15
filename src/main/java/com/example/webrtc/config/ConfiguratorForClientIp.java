package com.example.webrtc.config;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * 在modifyHandshake方法中，可以将储存在httpSession中的clientIp，转移到ServerEndpointConfig中
 *
 * Created by DELL(mxd) on 2021/2/14 20:20
 */
public class ConfiguratorForClientIp extends ServerEndpointConfig.Configurator {

    /**
     * 把HttpSession中保存的ClientIP放到ServerEndpointConfig中
     * @param config 1
     * @param request 1
     * @param response 1
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        config.getUserProperties().put("clientIp", httpSession.getAttribute("clientIp"));
    }
}
