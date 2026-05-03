package com.yigit.ecommerce.assistant.dto;

import jakarta.validation.constraints.NotBlank;

public class AssistantRequest {

    @NotBlank(message = "Mesaj bos olamaz")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}