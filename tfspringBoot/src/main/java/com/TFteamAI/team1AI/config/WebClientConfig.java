package com.TFteamAI.team1AI.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    //WebClient를 구성하고 Bean으로 정의하여 애플리케이션에서 사용할 수 있도록 함
    @Value("${pythonURI}")
    private String pythonServer; //applicaion.properties에 정의

    @Bean
    WebClient webClient() {
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)).build())
                .baseUrl(pythonServer)
                .build();
    }
}
