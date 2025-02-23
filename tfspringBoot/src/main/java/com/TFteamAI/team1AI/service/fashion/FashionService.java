package com.TFteamAI.team1AI.service.fashion;

import com.TFteamAI.team1AI.dto.fashion.FashionDataDTO;
import org.springframework.stereotype.Service;

@Service
public class FashionService {

    public FashionDataDTO getFashionData() {
        // 실제로 MQTT에서 직접 받아와야 하지만, TEST 위해 더미데이터 삽입.

        int coat = 2;
        int jacket = 3;
        int jumper = 2;
        int padding = 2;

        int vest = 1;
        int cardigan = 2;
        int blouse = 1;

        int top = 4;
        int shirt = 9;
        int sweater = 7;

        // 여기의 수치를 조정하게 되면 비율이 자동적으로 계산

        return new FashionDataDTO(coat, jacket, jumper, padding, vest, cardigan, blouse, top, shirt, sweater);
        // 개별 의류 개수를 넣으면 비율이 자동으로 계산되게끔
    }
}