package com.TFteamAI.team1AI.controller;

import com.TFteamAI.team1AI.dto.fashion.FashionDataDTO;
import com.TFteamAI.team1AI.dto.fashion.FashionTemperature;
import com.TFteamAI.team1AI.entity.board.Board;
import com.TFteamAI.team1AI.entity.fashion.Fashion;
import com.TFteamAI.team1AI.repository.fashion.FashionRepository;
import com.TFteamAI.team1AI.service.board.BoardService;
import com.TFteamAI.team1AI.service.fashion.FashionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;


@RestController
@RequestMapping("/tf/*")
@CrossOrigin(origins = "백엔드 서버 IP")
@Log4j2
public class RestReqController {

    @Autowired
    private FashionRepository fashionRepository;

    @Autowired
    private FashionService fashionService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private WebClient webClient;

    @PostMapping("/service")
    public String detectService(MultipartFile file, String message) {
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
        try {
            Fashion savedData = fashionService.saveFashionData(receive);

            if (savedData != null) {
                return Map.of(
                        "status", "success",
                        "message", "데이터 저장 완료",
                        "data", savedData
                );
            } else {
                return Map.of(
                        "status", "skip",
                        "message", "중복 데이터 또는 저장 실패"
                );
            }
        } catch (Exception e) {
            log.error("데이터 처리 오류: {}", e.getMessage());
            return Map.of(
                    "status", "error",
                    "message", e.getMessage()
            );
        }
    }


    @PostMapping("/tf/temperature/save")
    public ResponseEntity<?> saveTemperature(@RequestBody FashionTemperature request) {
        try {
            double temperature = request.getTemperature();

            // 현재 탐지된 의류 데이터 가져오기
            FashionDataDTO currentFashionData = fashionService.getFashionData();

            // 게시글 자동 생성
            Board savedBoard = boardService.createTemperatureRecord(
                    (float)temperature,
                    currentFashionData
            );

            return ResponseEntity.ok()
                    .body(Map.of(
                            "message", "Temperature set to " + temperature + "°C",
                            "boardId", savedBoard.getBoardId()
                    ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to set temperature: " + e.getMessage()));
        }
    }
}
