package com.TFteamAI.team1AI.service.fashion;

import com.TFteamAI.team1AI.dto.fashion.FashionDataDTO;
import org.springframework.stereotype.Service;

@Service
public class FashionService {

    public FashionDataDTO getFashionData() {
        // 실제로 MQTT에서 직접 받아와야 하지만, TEST 위해 더미데이터 삽입.

        int coat = 5;
        int jacket = 4;
        int jumper = 2;
        int padding = 5;

        int vest = 2;
        int cardigan = 3;
        int blouse = 3;

        int top = 3;
        int shirt = 3;
        int sweater = 4;

        return new FashionDataDTO(coat, jacket, jumper, padding, vest, cardigan, blouse, top, shirt, sweater);
        // 개별 의류 개수를 넣으면 비율이 자동으로 계산되게끔
    }
}
