package com.fastprint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private com.fastprint.service.AiService aiService;

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String response = aiService.generateResponse(message);

        Map<String, String> result = new HashMap<>();
        result.put("response", response);
        return result;
    }
}
