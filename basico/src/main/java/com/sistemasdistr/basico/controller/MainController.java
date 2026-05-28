package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.repository.CategoryRepository;
import com.sistemasdistr.basico.repository.ProductRepository;
import com.sistemasdistr.basico.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("active", "dashboard");
        model.addAttribute("content", "index");
        return "layout";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }

    // Endpoint para las estadísticas del dashboard
    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("users", userRepository.count());
        stats.put("products", productRepository.count());
        stats.put("categories", categoryRepository.count());
        return ResponseEntity.ok(stats);
    }
}