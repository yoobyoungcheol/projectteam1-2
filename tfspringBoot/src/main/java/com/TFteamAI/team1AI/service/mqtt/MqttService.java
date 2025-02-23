package com.TFteamAI.team1AI.service.mqtt;

import com.TFteamAI.team1AI.dto.fashion.FashionDataDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class MqttService implements MqttCallback {

    private final MqttClient mqttClient;
    private final String topic;
    private ObjectMapper objectMapper = new ObjectMapper();

    // 최신 탐지 데이터를 저장할 변수
    private FashionDataDTO updateFashionData = new FashionDataDTO(0, 0, 0);

    @Autowired
    public MqttService(MqttClient mqttClient, @Value("${mqtt.topic}") String topic) {
        this.mqttClient = mqttClient;
        this.topic = topic;

        try {
            this.mqttClient.setCallback(this);
            this.mqttClient.subscribe(topic);
            log.info("MQTT 구독 설정 완료");
        } catch (MqttException e) {
            log.error("MQTT 구독 실패: " + e.getMessage());
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        try {
            // 파이썬에서 보낸 JSON 데이터 파싱
            var jsonData = objectMapper.readTree(payload);
            String base64Image = jsonData.get("image").asText();

            // 의류 객체 탐지 영상 JSON 데이터 파싱
            int coat = jsonData.get("coat").asInt();
            int jacket = jsonData.get("jacket").asInt();
            int jumper = jsonData.get("jumper").asInt();
            int padding = jsonData.get("padding").asInt();
            int vest = jsonData.get("vest").asInt();
            int cardigan = jsonData.get("cardigan").asInt();
            int blouse = jsonData.get("blouse").asInt();
            int top = jsonData.get("top").asInt();
            int shirt = jsonData.get("shirt").asInt();
            int sweater = jsonData.get("sweater").asInt();
            updateFashionData = new FashionDataDTO(coat, jacket, jumper, padding, vest, cardigan, blouse, top, shirt, sweater);
            log.info("Received image data from Python server");
            log.info("Update Fashion Data : " + updateFashionData);

        } catch (Exception e) {
            log.error("Error processing message: " + e.getMessage());
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        // 연결 끊어졌을 때의 메세지 표현
        log.error("Connection lost: " + cause.getMessage());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // 메시지 전송 완료 시 호출
    }

    public FashionDataDTO getFasionData() {
        return updateFashionData;
    }
}