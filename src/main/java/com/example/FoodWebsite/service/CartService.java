package com.example.FoodWebsite.service;


import jakarta.servlet.http.HttpSession;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CartService {

    private static final String CACHE_NAME = "cartCache";

    @Cacheable(value = CACHE_NAME, key = "#session.id")
    public Map<Long, Integer> getCart(HttpSession session) {
        // If not cached, initialize a new cart
        return new HashMap<>();
    }

    @CachePut(value = CACHE_NAME, key = "#session.id")
    public Map<Long, Integer> addToCart(HttpSession session, Long itemId) {
        Map<Long, Integer> cart = getCart(session);
        cart.put(itemId, cart.getOrDefault(itemId, 0) + 1);
        return cart;
    }

    @CachePut(value = CACHE_NAME, key = "#session.id")
    public Map<Long, Integer> removeFromCart(HttpSession session, Long itemId) {
        Map<Long, Integer> cart = getCart(session);
        cart.computeIfPresent(itemId, (k, v) -> v > 1 ? v - 1 : null);
        return cart;
    }

    @CachePut(value = CACHE_NAME, key = "#session.id")
    public Map<Long, Integer> removeItem(HttpSession session, Long itemId) {
        Map<Long, Integer> cart = getCart(session);
        cart.remove(itemId);
        return cart;
    }

    @CacheEvict(value = CACHE_NAME, key = "#session.id")
    public void clearCart(HttpSession session) {
        // On order success
    }
}
