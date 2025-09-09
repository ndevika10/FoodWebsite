package com.example.FoodWebsite.controller;

import com.example.FoodWebsite.model.*;
import com.example.FoodWebsite.repository.*;
import com.example.FoodWebsite.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute("user") User user, Model model, HttpSession session) {
        User existingUser = userRepository.findByEmail(user.getEmail()).orElse(null);
        if (existingUser != null && existingUser.getPassword().equals(user.getPassword())) {
            session.setAttribute("loggedInUser", existingUser); // ✅ Store user in session
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logoutUser(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/admin/login")
    public String showAdminLoginForm(Model model) {
        model.addAttribute("admin", new Admin());
        return "admin-login";
    }

    @PostMapping("/admin/login")
    public String adminLogin(@ModelAttribute("admin") Admin admin, Model model) {
        Admin hardcodedAdmin = new Admin();
        if ("admin".equals(hardcodedAdmin.getUsername()) && "admin123".equals(hardcodedAdmin.getPassword())) {
            if ("admin123".equals(admin.getPassword())) {
                return "admin-dashboard";
            } else {
                model.addAttribute("error", "Invalid admin password");
                return "admin-login";
            }
        } else {
            model.addAttribute("error", "Admin credentials misconfigured");
            return "admin-login";
        }
    }

    @GetMapping("/admin/manage-users")
    public String showManageUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "manage-users";
    }

    @PostMapping("/admin/update-user")
    public String updateUser(@RequestParam Long id, @RequestParam String email, @RequestParam String address, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setEmail(email);
        String[] addressParts = address.split(",\\s*");
        if (addressParts.length > 0) user.setStreetAddress(addressParts[0].trim());
        if (addressParts.length > 1) user.setCity(addressParts[1].trim());
        if (addressParts.length > 2) user.setZipCode(addressParts[2].trim());
        userRepository.save(user);
        model.addAttribute("users", userRepository.findAll());
        return "manage-users";
    }

    // ------------------- FOOD ITEM CRUD BELOW -------------------
    @GetMapping("/admin/manage-food-items")
    public String showFoodItems(Model model) {
        model.addAttribute("newFoodItem", new FoodItem());
        model.addAttribute("foodItems", foodItemRepository.findAll());
        return "manage-food-items";
    }

    @PostMapping("/admin/save-food-items")
    public String saveFoodItems(
            @ModelAttribute("newFoodItem") FoodItem newFoodItem,
            @RequestParam(required = false) List<String> batchNames,
            @RequestParam(required = false) List<Double> batchPrices,
            @RequestParam(required = false) List<String> batchDescriptions,
            @RequestParam(required = false) List<Integer> batchQuantities, // new field
            Model model) {

        if (newFoodItem.getName() != null && !newFoodItem.getName().isEmpty()) {
            foodItemRepository.save(newFoodItem);
        }

        if (batchNames != null && batchPrices != null && batchDescriptions != null && batchQuantities != null) {
            List<FoodItem> itemsToSave = new ArrayList<>();
            for (int i = 0; i < batchNames.size(); i++) {
                String name = batchNames.get(i);
                Double price = batchPrices.get(i);
                String description = batchDescriptions.get(i);
                Integer quantity = batchQuantities.get(i);

                if (name != null && !name.isEmpty() && price != null) {
                    FoodItem item = new FoodItem();
                    item.setName(name);
                    item.setPrice(price);
                    item.setDescription(description);
                    item.setQuantity(quantity != null ? quantity : 0);
                    itemsToSave.add(item);
                }
            }
            if (!itemsToSave.isEmpty()) {
                foodItemRepository.saveAll(itemsToSave);
            }
        }

        model.addAttribute("newFoodItem", new FoodItem());
        model.addAttribute("foodItems", foodItemRepository.findAll());
        return "manage-food-items";
    }

    @PostMapping("/admin/update-food-item")
    public String updateFoodItem(@RequestParam Long id,
                                 @RequestParam String name,
                                 @RequestParam double price,
                                 @RequestParam String description,
                                 @RequestParam int quantity) {
        FoodItem item = foodItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid food item ID"));
        item.setName(name);
        item.setPrice(price);
        item.setDescription(description);
        item.setQuantity(quantity);
        foodItemRepository.save(item);
        return "redirect:/admin/manage-food-items";
    }

    @PostMapping("/admin/delete-food-item")
    public String deleteFoodItem(@RequestParam Long id) {
        foodItemRepository.deleteById(id);
        return "redirect:/admin/manage-food-items";
    }

    @GetMapping("/dashboard")
    public String userDashboard(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "asc") String sortDir,
                                HttpSession session,
                                Model model) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by("price").ascending() : Sort.by("price").descending();
        Pageable pageable = PageRequest.of(page, 6, sort);
        Page<FoodItem> foodPage = foodItemRepository.findAll(pageable);

        model.addAttribute("username", loggedInUser.getFullName());
        model.addAttribute("foodItems", foodPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", foodPage.getTotalPages());
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "user-dashboard";
    }



    @GetMapping("/account")
    public String showAccountPage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", loggedInUser);
        return "my-account"; // Thymeleaf template
    }

    @PostMapping("/account/update")
    public String updateAccount(@ModelAttribute("user") User updatedUser,
                                @RequestParam String currentPassword,
                                @RequestParam(required = false) String newPassword,
                                @RequestParam(required = false) String confirmPassword,
                                HttpSession session,
                                Model model) {
        User sessionUser = (User) session.getAttribute("loggedInUser");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (!sessionUser.getPassword().equals(currentPassword)) {
            model.addAttribute("error", "Current password is incorrect.");
            model.addAttribute("user", sessionUser);
            return "my-account";
        }

        // Update fields
        sessionUser.setFullName(updatedUser.getFullName());
        sessionUser.setStreetAddress(updatedUser.getStreetAddress());
        sessionUser.setCity(updatedUser.getCity());
        sessionUser.setZipCode(updatedUser.getZipCode());

        // Update password if provided and confirmed
        if (newPassword != null && !newPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "New passwords do not match.");
                model.addAttribute("user", sessionUser);
                return "my-account";
            }
            sessionUser.setPassword(newPassword);
        }

        userRepository.save(sessionUser);
        session.setAttribute("loggedInUser", sessionUser); // Refresh session user
        model.addAttribute("user", sessionUser);
        model.addAttribute("success", "Account updated successfully.");
        return "my-account";
    }
    @Autowired
    private CartService cartService;

    @GetMapping("/add-to-cart/{id}")
    public String addToCart(@PathVariable Long id, HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        // Increment quantity if already in cart
        cart.put(id, cart.getOrDefault(id, 0) + 1);

        session.setAttribute("cart", cart);
        return "redirect:/dashboard";
    }
    @GetMapping("/increase-quantity/{id}")
    public String increaseQuantity(@PathVariable Long id, HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart != null && cart.containsKey(id)) {
            cart.put(id, cart.get(id) + 1);
        }
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    @GetMapping("/decrease-quantity/{id}")
    public String decreaseQuantity(@PathVariable Long id, HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart != null && cart.containsKey(id)) {
            int qty = cart.get(id);
            if (qty > 1) {
                cart.put(id, qty - 1);
            } else {
                cart.remove(id); // Remove item if quantity is 1
            }
        }
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    @GetMapping("/remove-from-cart/{id}")
    public String removeFromCart(@PathVariable Long id, HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart != null) {
            cart.remove(id);
        }
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String showCart(Model model, HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        List<FoodItem> cartItems = new ArrayList<>();
        double total = 0;

        if (cart != null) {
            for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
                Optional<FoodItem> optionalItem = foodItemRepository.findById(entry.getKey());
                if (optionalItem.isPresent()) {
                    FoodItem item = optionalItem.get();
                    item.setQuantity(entry.getValue());
                    cartItems.add(item);
                    total += item.getPrice() * entry.getValue();
                }
            }
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", total);
        return "cart";
    }







}
