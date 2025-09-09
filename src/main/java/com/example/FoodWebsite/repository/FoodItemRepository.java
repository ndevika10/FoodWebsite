package com.example.FoodWebsite.repository;

import com.example.FoodWebsite.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
}