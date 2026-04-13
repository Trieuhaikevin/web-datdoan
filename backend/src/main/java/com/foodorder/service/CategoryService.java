package com.foodorder.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.foodorder.model.Category;
import com.foodorder.repository.CategoryRepository;
import com.foodorder.repository.FoodRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final FoodRepository foodRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        return categoryRepository.findById(id);
    }

    public Category createCategory(Category category) {
        Objects.requireNonNull(category, "Category cannot be null");
        return categoryRepository.save(category);
    }

    public Category updateCategory(Category category) {
        Objects.requireNonNull(category, "Category cannot be null");
        Objects.requireNonNull(category.getId(), "Category ID cannot be null");
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");

        var foods = foodRepository.findByCategory_Id(id);
        for (var food : foods) {
            food.setCategory(null);
        }
        foodRepository.saveAll(foods);

        categoryRepository.deleteById(id);
    }
}