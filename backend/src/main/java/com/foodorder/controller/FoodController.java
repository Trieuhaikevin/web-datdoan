package com.foodorder.controller;

import com.foodorder.dto.FoodRequest;
import com.foodorder.dto.FoodResponse;
import com.foodorder.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    @GetMapping
    public List<FoodResponse> getAllFoods() {
        return foodService.getAllFoods();
    }

    @GetMapping("/available")
    public List<FoodResponse> getAvailableFoods() {
        return foodService.getAvailableFoods();
    }

    @GetMapping("/category/{categoryId}")
    public List<FoodResponse> getFoodsByCategory(@PathVariable Long categoryId) {
        return foodService.getFoodsByCategory(categoryId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodResponse> getFoodById(@PathVariable Long id) {
        return foodService.getFoodById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public FoodResponse createFood(@RequestBody FoodRequest request) {
        return foodService.createFood(request);
    }

    @PutMapping("/{id}")
    public FoodResponse updateFood(@PathVariable Long id, @RequestBody FoodRequest request) {
        return foodService.updateFood(id, request);
    }

    @PutMapping("/{id}/stock")
    public FoodResponse updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        return foodService.updateStock(id, quantity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
        return ResponseEntity.ok().build();
    }
}
