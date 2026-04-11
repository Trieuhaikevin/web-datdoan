package com.foodorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodorder.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}