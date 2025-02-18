package com.TFteamAI.team1AI.service.fashion;

import com.TFteamAI.team1AI.dto.fashion.FashionDataDTO;
import com.TFteamAI.team1AI.entity.fashion.Fashion;
import com.TFteamAI.team1AI.repository.fashion.FashionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class FashionService {

    @Autowired
    private final FashionRepository fashionRepository;

    // 통계 데이터 조회
    public FashionDataDTO getFashionData() {
        return new FashionDataDTO(
                getCountByName("coat"),
                getCountByName("jacket"),
                getCountByName("jumper"),
                getCountByName("padding"),
                getCountByName("vest"),
                getCountByName("cardigan"),
                getCountByName("blouse"),
                getCountByName("shirt"),
                getCountByName("sweater")
        );
    }

    private int getCountByName(String name) {
        return fashionRepository.getTotalCountByName(name) != null ?
                fashionRepository.getTotalCountByName(name) : 0;
    }

    // 실시간 데이터 저장 메소드
    public Fashion saveFashionData(Map<String, Object> data) {
        try {
            // result 데이터 가져오기
            Object resultObj = data.get("result");
            if (!(resultObj instanceof List)) {
                log.error("result가 List 형식이 아닙니다");
                return null;
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = (List<Map<String, Object>>) resultObj;
            if (results.isEmpty()) {
                return null;
            }

            // 첫 번째 결과 처리
            Map<String, Object> item = results.get(0);

            // 필수 데이터 확인 및 변환
            Object trackIdObj = item.get("trackId");
            Object nameObj = item.get("name");
            Object confidenceObj = item.get("confidence");

            if (trackIdObj == null || nameObj == null || confidenceObj == null) {
                log.error("필수 데이터가 누락되었습니다");
                return null;
            }

            Integer trackId = Integer.valueOf(trackIdObj.toString());
            String name = nameObj.toString();
            Float confidence = Float.valueOf(confidenceObj.toString());

            // 중복 체크
            if (fashionRepository.findByTrackId(trackId) != null) {
                return null;
            }

            // 탐지된 의류 데이터 저장
            Fashion fashion = new Fashion(
                    name,   // 의류 이름
                    confidence, // 확률
                    trackId,// 추적 ID
                    LocalDateTime.now(), // 데이터 수신일자
                    1   // 카운트
            );
            // fashionRepository.save 로 DB에 저장한다.
            return fashionRepository.save(fashion);

        } catch (Exception e) {
            log.error("저장 실패: {}", e.getMessage());
            return null;
        }
    }

}
