package com.hotelbooking.ai_agent_service.service;

import com.hotelbooking.ai_agent_service.dto.HotelResponse;
import com.hotelbooking.ai_agent_service.dto.OllamaRequest;
import com.hotelbooking.ai_agent_service.dto.OllamaResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class AiAgentService {

    private final WebClient.Builder webClientBuilder;

    private static final String API_GATEWAY_URL =
            "http://api-gateway-lb-env-env.eba-qwz3nust.eu-north-1.elasticbeanstalk.com";

    private static final String LOCAL_OLLAMA_URL =
            "http://host.docker.internal:11434";

    public AiAgentService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public String chatWithHotelSearch(
            String userPrompt,
            String city,
            LocalDate startDate,
            LocalDate endDate,
            Integer people
    ) {
        String lowerPrompt = userPrompt.toLowerCase();

        if (lowerPrompt.contains("hello") || lowerPrompt.contains("hi") || lowerPrompt.contains("hey")) {
            return "Hello! I am your AI hotel booking assistant. I can help you find hotels, compare prices, and make reservations.";
        }

        if (lowerPrompt.contains("how are you")) {
            return "I am doing great! Ready to help you book the perfect hotel.";
        }

        if (lowerPrompt.contains("thank")) {
            return "You are welcome! Let me know if you need help finding a hotel.";
        }

        List<HotelResponse> hotels = getAvailableHotelsFromHotelService(city, startDate, endDate, people);

        if (hotels == null || hotels.isEmpty()) {
            return "No available hotels found in " + city + " for the selected dates.";
        }

        try {
            String ollamaResponse = askOllama(buildPrompt(userPrompt, hotels));

            if (ollamaResponse != null && !ollamaResponse.isBlank()) {
                return ollamaResponse;
            }
        } catch (Exception error) {
            System.out.println("Ollama is unavailable. Fallback recommendation mode is used.");
        }

        return buildFallbackRecommendation(hotels);
    }

    private String askOllama(String prompt) {
        WebClient ollamaClient = webClientBuilder
                .baseUrl(LOCAL_OLLAMA_URL)
                .build();

        OllamaRequest request = new OllamaRequest("llama3.2", prompt, false);

        OllamaResponse response = ollamaClient.post()
                .uri("/api/generate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OllamaResponse.class)
                .timeout(Duration.ofSeconds(8))
                .block();

        return response != null ? response.getResponse() : null;
    }

    private String buildPrompt(String userPrompt, List<HotelResponse> hotels) {
        StringBuilder hotelData = new StringBuilder();

        for (HotelResponse hotel : hotels) {
            hotelData.append("Hotel Name: ").append(hotel.getName()).append("\n");
            hotelData.append("City: ").append(hotel.getCity()).append("\n");
            hotelData.append("Address: ").append(hotel.getAddress()).append("\n");
            hotelData.append("Rating: ").append(hotel.getRating()).append("\n");
            hotelData.append("Price Per Night: ").append(hotel.getPricePerNight()).append("\n");
            hotelData.append("Pool: ").append(hotel.getHasPool()).append("\n");
            hotelData.append("Wi-Fi: ").append(hotel.getHasWifi()).append("\n");
            hotelData.append("Breakfast: ").append(hotel.getHasBreakfast()).append("\n");
            hotelData.append("-------------------------\n");
        }

        return """
                You are an AI hotel booking assistant.
                Use ONLY the hotel data provided below.
                Do NOT invent hotel names, prices, cities, or amenities.
                Recommend suitable hotels based on the user's request.
                Keep the answer short and clear.

                User request:
                %s

                Available hotel data:
                %s
                """.formatted(userPrompt, hotelData);
    }

    private String buildFallbackRecommendation(List<HotelResponse> hotels) {
        HotelResponse bestHotel = hotels.stream()
                .max(Comparator.comparingDouble(hotel ->
                        hotel.getRating() != null ? hotel.getRating() : 0.0
                ))
                .orElse(hotels.get(0));

        return "I recommend " + bestHotel.getName()
                + " in " + bestHotel.getCity()
                + ". It has a rating of " + bestHotel.getRating()
                + " and costs " + bestHotel.getPricePerNight()
                + " TL per night. This response was generated using fallback recommendation mode.";
    }

    private List<HotelResponse> getAvailableHotelsFromHotelService(
            String city,
            LocalDate startDate,
            LocalDate endDate,
            Integer people
    ) {
        WebClient hotelClient = webClientBuilder
                .baseUrl(API_GATEWAY_URL)
                .build();

        return hotelClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/hotels/search/available")
                        .queryParam("city", city)
                        .queryParam("startDate", startDate)
                        .queryParam("endDate", endDate)
                        .queryParam("people", people)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<HotelResponse>>() {})
                .block();
    }
}