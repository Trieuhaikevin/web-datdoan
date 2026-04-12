package com.foodorder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodorder.model.Food;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByCategory_Id(Long categoryId);
    List<Food> findByAvailableTrueAndStockQuantityGreaterThan(Integer stockQuantity);
}
