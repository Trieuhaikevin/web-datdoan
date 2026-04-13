package com.foodorder.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.foodorder.dto.FoodRequest;
import com.foodorder.dto.FoodResponse;
import com.foodorder.model.Category;
import com.foodorder.model.Food;
import com.foodorder.model.Order;
import com.foodorder.model.OrderItem;
import com.foodorder.repository.CategoryRepository;
import com.foodorder.repository.FoodRepository;
import com.foodorder.repository.OrderItemRepository;
import com.foodorder.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
    private final CategoryRepository categoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    public List<FoodResponse> getAllFoods() {
        return foodRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<FoodResponse> getAvailableFoods() {
        return foodRepository.findByAvailableTrueAndStockQuantityGreaterThan(0).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<FoodResponse> getFoodsByCategory(Long categoryId) {
        Objects.requireNonNull(categoryId, "Category ID cannot be null");
        return foodRepository.findByCategory_Id(categoryId).stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<FoodResponse> getFoodById(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        return foodRepository.findById(id)
                .map(this::toResponse);
    }

    public FoodResponse createFood(FoodRequest request) {
        validateRequest(request);
        Food food = new Food();
        applyRequest(food, request);
        return toResponse(foodRepository.save(food));
    }

    public FoodResponse updateFood(Long id, FoodRequest request) {
        Objects.requireNonNull(id, "ID cannot be null");
        validateRequest(request);

        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Không tìm thấy món ăn"));
        applyRequest(food, request);
        return toResponse(foodRepository.save(food));
    }

    public FoodResponse updateStock(Long id, Integer stockQuantity) {
        Objects.requireNonNull(id, "ID cannot be null");
        int normalizedStock = normalizeStockQuantity(stockQuantity);

        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Không tìm thấy món ăn"));
        food.setStockQuantity(normalizedStock);
        if (normalizedStock == 0) {
            food.setAvailable(false);
        }
        return toResponse(foodRepository.save(food));
    }

    public void deleteFood(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");

        var orderItems = orderItemRepository.findAllByFoodId(id);
        for (OrderItem orderItem : orderItems) {
            Order order = orderItem.getOrder();
            if (order != null) {
                if (order.getStatus() == Order.Status.PENDING || order.getStatus() == Order.Status.CONFIRMED) {
                    order.setStatus(Order.Status.CANCELLED);
                }
                orderItem.setFood(null);
                orderRepository.save(order);
            }
        }
        foodRepository.deleteById(id);
    }

    private void validateRequest(FoodRequest request) {
        if (request == null
                || !StringUtils.hasText(request.name())
                || request.price() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Tên món và giá không được để trống");
        }
    }

    private void applyRequest(Food food, FoodRequest request) {
        int normalizedStock = normalizeStockQuantity(request.stockQuantity());
        Boolean requestedAvailable = request.available();
        boolean available = requestedAvailable != null ? requestedAvailable : food.isAvailable();

        food.setName(request.name().trim());
        food.setDescription(hasText(request.description()) ? request.description().trim() : null);
        food.setImageUrl(hasText(request.imageUrl()) ? request.imageUrl().trim() : null);
        food.setPrice(request.price());
        food.setStockQuantity(normalizedStock);
        food.setAvailable(normalizedStock > 0 && available);
        food.setCategory(resolveCategory(request.categoryId()));
    }

    private Category resolveCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Không tìm thấy danh mục"));
    }

    private int normalizeStockQuantity(Integer stockQuantity) {
        int normalized = stockQuantity == null ? 0 : stockQuantity;
        if (normalized < 0) {
            throw new ResponseStatusException(BAD_REQUEST, "Số lượng tồn kho không hợp lệ");
        }
        return normalized;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private FoodResponse toResponse(Food food) {
        Category category = food.getCategory();
        return new FoodResponse(
                food.getId(),
                food.getName(),
                food.getDescription(),
                food.getImageUrl(),
                food.getPrice(),
                food.isAvailable(),
                food.getStockQuantity(),
                category != null ? category.getId() : null,
                category != null ? category.getName() : null
        );
    }
}
