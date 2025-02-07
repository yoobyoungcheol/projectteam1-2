package com.TFteamAI.team1AI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tf")
@CrossOrigin(origins = "백엔드 서버 IP")
public class HomeController {

    @GetMapping("/ai")
    public String viewAi() {

        return "ai";
    }
}
