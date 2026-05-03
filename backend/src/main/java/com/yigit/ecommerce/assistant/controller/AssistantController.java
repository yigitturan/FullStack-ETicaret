package com.yigit.ecommerce.assistant.controller;

import com.yigit.ecommerce.assistant.dto.AssistantRequest;
import com.yigit.ecommerce.assistant.dto.AssistantResponse;
import com.yigit.ecommerce.assistant.service.IAssistantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final IAssistantService assistantService;

    public AssistantController(IAssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping("/ask")
    public ResponseEntity<AssistantResponse> ask(@Valid @RequestBody AssistantRequest request) {

        // kullanici mesajini service'e gonderiyoruz
        AssistantResponse response = assistantService.ask(request);

        return ResponseEntity.ok(response);
    }
}