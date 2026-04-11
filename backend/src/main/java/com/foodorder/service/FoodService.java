package com.foodorder.service;

import com.foodorder.model.Food;
import com.foodorder.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;

    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    public List<Food> getAvailableFoods() {
        return foodRepository.findByAvailableTrue();
    }

    public List<Food> getFoodsByCategory(Long categoryId) {
        return foodRepository.findByCategory_Id(categoryId);
    }

    public Optional<Food> getFoodById(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        return foodRepository.findById(id);
    }

    public Food createFood(Food food) {
        Objects.requireNonNull(food, "Food cannot be null");
        return foodRepository.save(food);
    }

    public void deleteFood(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        foodRepository.deleteById(id);
    }
}