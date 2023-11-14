package com.TeamProject.TeamProject.Model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public void saveJsonToDatabase(String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonData);

            for (JsonNode item : jsonNode.get("items")) {
                Restaurant restaurant = new Restaurant();
                restaurant.setTitle(item.get("title").asText());
                restaurant.setCategory(item.get("category").asText());
                restaurant.setAddress(item.get("address").asText());
                restaurant.setRoadAddress(item.get("roadAddress").asText());

                restaurantRepository.save(restaurant);
            }

        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

}
