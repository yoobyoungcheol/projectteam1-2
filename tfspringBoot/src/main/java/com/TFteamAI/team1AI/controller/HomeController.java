package com.TFteamAI.team1AI.controller;

import com.TFteamAI.team1AI.dto.fashion.FashionDataDTO;
import com.TFteamAI.team1AI.service.fashion.FashionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/tf")
@CrossOrigin(origins = "백엔드 서버 IP")

public class HomeController {

    private final FashionService fashionService;

    public HomeController(FashionService fashionService) {
        this.fashionService = fashionService;
    }

    @GetMapping("/main")
    public String main() {
        // main.html 열어주는 GetMapping

        return "main";
    }

    @GetMapping("/ai")
    public String viewAi() {
        // ai.html(실시간 영상) 열어주는 GetMapping

        return "ai";
    }

    @GetMapping("/data")
    public String jsonData() {
        // Python Server에서 JSON 데이터 수신하기 위한 페이지(dataReceive.html) 열어주는 GetMapping

        return "dataReceive";
    }

    @GetMapping("/fashionStream")
    public String fashionStreamPage() {
        // 실시간 의류 영상 스트리밍 페이지(fashionStream.html)

        return "/fashion/fashionAi";
    }
    @GetMapping("/statistics")
    public String FashionDetectionPage(Model model) {
        // 실시간 의류 영상의 데이터 통계를 보여주는 페이지(fashionStatistics.html)

        FashionDataDTO fashionData = fashionService.getFashionData();
        model.addAttribute("fashionData", fashionData);
        return "/fashion/fashionStatistics";
    }

    @GetMapping("/fashion")
    @ResponseBody
    public FashionDataDTO getFashionData() {

        return fashionService.getFashionData();
    }

    @GetMapping("/fashion/list")
    @ResponseBody
    public FashionDataDTO getFashionList() {

        return fashionService.getFashionData();
    }

}
