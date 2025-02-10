package com.TFteamAI.team1AI.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class MqttService implements MqttCallback {

    private final String BROKER = "tcp://파이썬 Server IP:1883";
    private final String CLIENT_ID = "springboot_client";
    private final String TOPIC = "cam/objects"; // 파이썬 서버의 TOPIC과 일치
    private MqttClient mqttClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {

        CompletableFuture.runAsync(() -> {
            try {
                System.out.println("MQTT 브로커 연결 시도 중...");
                mqttClient = new MqttClient(BROKER, CLIENT_ID + "_" + System.currentTimeMillis());
                mqttClient.setCallback(this);

                MqttConnectOptions options = new MqttConnectOptions();
                options.setAutomaticReconnect(true);
                options.setCleanSession(true);
                options.setConnectionTimeout(1);

                mqttClient.connect(options);
                mqttClient.subscribe(TOPIC);
                System.out.println("MQTT 브로커 연결 성공!");
            } catch (Exception e) {
                System.err.println("MQTT 연결 실패 : " + e.getMessage());
            }
        });
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        // JSON 데이터 처리
        try {
            // 파이썬에서 보낸 JSON 데이터 파싱
            var jsonData = objectMapper.readTree(payload);
            String base64Image = jsonData.get("image").asText();

            System.out.println("Received image data from Python server");

        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost: " + cause.getMessage());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // 메시지 전송 완료 시 호출
    }
}
