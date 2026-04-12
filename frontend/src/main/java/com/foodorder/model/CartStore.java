package com.foodorder.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CartStore {
    private static final CartStore INSTANCE = new CartStore();

    private final List<CartItem> items = new ArrayList<>();

    private CartStore() {
    }

    public static CartStore getInstance() {
        return INSTANCE;
    }

    public synchronized List<CartItem> getItems() {
        return Collections.unmodifiableList(new ArrayList<>(items));
    }

    public synchronized void addOrIncrease(FoodModel food) {
        for (CartItem item : items) {
            if (item.getFoodId().equals(food.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        items.add(new CartItem(food.getId(), food.getName(), food.getPrice(), 1));
    }

    public synchronized void removeByFoodId(Long foodId) {
        items.removeIf(item -> item.getFoodId().equals(foodId));
    }

    public synchronized void clear() {
        items.clear();
    }

    public synchronized boolean isEmpty() {
        return items.isEmpty();
    }

    public synchronized BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }
}
