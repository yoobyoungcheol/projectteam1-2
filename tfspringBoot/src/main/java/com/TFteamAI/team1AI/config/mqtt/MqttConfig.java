
package com.TFteamAI.team1AI.config.mqtt;

import lombok.extern.log4j.Log4j2;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Log4j2
public class MqttConfig {

    /* Python Server에서 MQTT 브로커 사용함으로 데이터와의 송수신을 하려면 SpringBoot Server에도 build.gradle, properties에서
    MQTT 관련 코드를 기재해야 하고, IP, TOPIC, URL, Port 등 같게 해야 한다. */

    @Value("${mqtt.broker.url}")
    private String broker;

    @Value("${mqtt.topic}")
    private String topic;

    @Bean
    public MqttClient mqttClient() {
        try {
            MqttClient client = new MqttClient(
                    broker,
                    MqttClient.generateClientId(),
                    new MemoryPersistence() // 메모리에만 저장하고 디스크에는 저장하지 않음(workspace 폴더의 MQTT Client 재생성 방지)
            );

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);// 브로커와의 연결이 끊어졌을 때 자동으로 재연결을 시도
            options.setCleanSession(true);// 클라이언트의 세션 상태 관리 방식
            options.setConnectionTimeout(5);  // 타임아웃 5초로 설정

            try {
                client.connect(options);
                return client;
            } catch (MqttException e) {
                log.error("MQTT 브로커 연결 실패: {}", e.getMessage());
                // 연결 실패해도 클라이언트 객체는 반환
                return client;
            }
        } catch (MqttException e) {
            log.error("MQTT 클라이언트 생성 실패: {}", e.getMessage());
            throw new RuntimeException("MQTT 클라이언트 생성 실패", e);
        }
    }
}
