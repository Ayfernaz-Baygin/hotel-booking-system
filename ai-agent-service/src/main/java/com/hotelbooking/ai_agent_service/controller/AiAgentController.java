package com.hotelbooking.ai_agent_service.controller;

import com.hotelbooking.ai_agent_service.service.AiAgentService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
public class AiAgentController {

    private final AiAgentService aiAgentService;

    public AiAgentController(AiAgentService aiAgentService) {
        this.aiAgentService = aiAgentService;
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {

        String prompt = request.get("prompt");
        String city = request.get("city");
        LocalDate startDate = LocalDate.parse(request.get("startDate"));
        LocalDate endDate = LocalDate.parse(request.get("endDate"));
        Integer people = Integer.parseInt(request.get("people"));

        String response = aiAgentService.chatWithHotelSearch(
                prompt,
                city,
                startDate,
                endDate,
                people
        );

        return Map.of(
                "prompt", prompt,
                "response", response
        );
    }
}