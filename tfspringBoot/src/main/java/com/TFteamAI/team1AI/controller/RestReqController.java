package com.TFteamAI.team1AI.controller;

import com.TFteamAI.team1AI.entity.fashion.Fashion;
import com.TFteamAI.team1AI.repository.fashion.FashionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tf/*")
@CrossOrigin(origins = "http://백엔드 Server IP")
public class RestReqController {

    @Autowired
    private FashionRepository fashionRepository;

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
    public Map<String, Object> receiveJson(@RequestBody List<Map<String, Object>> receive) {

        System.out.println("JSON Data : " + receive);

        // JSON 데이터 수신하기 위한 PostMapping
        for (Map<String, Object> obj : receive) {
            String name = (String) obj.get("name");
            Float confidence = (Float) obj.get("confidence");
            Integer trackId = (Integer) obj.get("trackId");

            Fashion fashion = new Fashion();
            fashion.setName(name);
            fashion.setConfidence(confidence);
            fashion.setTrackId(trackId);
            fashion.setReceiveTime(LocalDateTime.now());
            fashion.setCount(1);


            try {
                fashionRepository.save(fashion);
            } catch (Exception e) {
                System.out.println("DB 데이터 저장 실패 : " + e.getMessage());
            }

        }
        return Map.of("status", "success", "data", receive);
    }
}