package com.TFteamAI.team1AI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tf")
public class HomeController {

    @GetMapping("/")
    public String home() {

        return "index"; // index -> resouces/templates/index.html로 찾아간다.
    }

    @GetMapping("/ai")
    public String viewAi() {

        return "ai";
    }// http://localhost:80/tf/ai
}
