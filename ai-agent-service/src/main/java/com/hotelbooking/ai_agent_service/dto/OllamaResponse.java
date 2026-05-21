package com.hotelbooking.ai_agent_service.dto;

public class OllamaResponse {

    private String response;

    public OllamaResponse() {
    }

    public OllamaResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}