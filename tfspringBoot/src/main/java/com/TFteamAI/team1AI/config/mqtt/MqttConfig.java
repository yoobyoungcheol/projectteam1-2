package com.TFteamAI.team1AI.config.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import java.util.concurrent.CompletableFuture;

@Configuration
public class MqttConfig {

    /* Python Server에서 MQTT 브로커 사용함으로 데이터와의 송수신을 하려면 SpringBoot Server에도 build.gradle, properties에서
    MQTT 관련 코드를 기재해야 하고, IP, TOPIC, URL, Port 등 같게 해야 한다. */

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.client.id}")
    private String clientId;

    @Value("${mqtt.topic}")
    private String topic;

    @EventListener(ApplicationReadyEvent.class)
    public void startAfterSpringBoot() {
        CompletableFuture.runAsync(() -> {
            try {
                MqttClient client = new MqttClient(brokerUrl, clientId + "_" + System.currentTimeMillis());
                MqttConnectOptions options = new MqttConnectOptions();
                options.setAutomaticReconnect(true);
                options.setCleanSession(true);
                options.setConnectionTimeout(1);

                System.out.println("MQTT 브로커 연결 시도 중...");
                client.connect(options);
                client.subscribe(topic);
                System.out.println("MQTT 브로커 연결 성공!");
            } catch (MqttException e) {
                System.err.println("MQTT 연결 실패 : " + e.getMessage());
            }
        });
    }
}