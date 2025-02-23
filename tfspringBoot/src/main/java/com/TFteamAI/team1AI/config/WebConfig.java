
package com.TFteamAI.team1AI.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {//컨트롤러 역할 대행

    // 백엔드 없이 프론트로 파이썬 연결.

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/ai").setViewName("fashion/ai");
    }// html 페이지를 직접 요청할 수 있도록 뷰-컨트롤러 설정 추가
    // http://localhost:80/ai -> ai.html 파일이 실행이 된다.

    @Bean
    public WebMvcConfigurer corsConfig() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 모든 API에 대해 CORS 적용
                        .allowedOrigins("http://백엔드 Server IP") // 허용할 도메인
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                        .allowedHeaders("*") // 모든 헤더 허용
                        .allowCredentials(true); // 쿠키 허용 (필요한 경우)
            }
        };
    }
}
