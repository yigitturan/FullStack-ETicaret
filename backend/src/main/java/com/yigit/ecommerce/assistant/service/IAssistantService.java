package com.yigit.ecommerce.assistant.service;

import com.yigit.ecommerce.assistant.dto.AssistantRequest;
import com.yigit.ecommerce.assistant.dto.AssistantResponse;

public interface IAssistantService {

    AssistantResponse ask(AssistantRequest request);
}