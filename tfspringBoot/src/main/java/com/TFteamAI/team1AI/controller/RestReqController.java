package com.TFteamAI.team1AI.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@RestController
@RequestMapping("/tf/*")
@CrossOrigin(origins = "백엔드 서버 IP")
public class RestReqController {

    @Autowired
    private WebClient webClient;

    @PostMapping("/service")
    public String detectService(MultipartFile file, String message){
        System.out.println("java server_detect 시스템 실행----------");
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("message", message);
        bodyBuilder.part("file", file.getResource());
        String result = webClient.post().uri("/detect")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .retrieve().bodyToMono(String.class)
                .block();
        System.out.println("result 수행------");
        return result;
    }

    @PostMapping("/dataReceive")
    public Map<String, Object> receiveJson(@RequestBody Map<String, Object> receive) {

        System.out.println("JSON Data : " + receive);
        return receive;

    }
}
