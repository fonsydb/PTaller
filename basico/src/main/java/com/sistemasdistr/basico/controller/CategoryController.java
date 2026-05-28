package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.model.Category;
import com.sistemasdistr.basico.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String listCategories(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("active", "categories");
        model.addAttribute("content", "categories/list");
        return "layout";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("active", "categories");
        model.addAttribute("content", "categories/form");
        return "layout";
    }

    @PostMapping("/save")
    public String saveCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        try {
            // No permitir crear otra categoría con nombre "General"
            if ("General".equalsIgnoreCase(category.getName())) {
                redirectAttributes.addFlashAttribute("error", "Ya existe la categoría 'General'");
                return "redirect:/categories";
            }
            categoryRepository.save(category);
            redirectAttributes.addFlashAttribute("success", "Categoría guardada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la categoría: " + e.getMessage());
        }
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + id));

        // No permitir editar la categoría "General"
        if ("General".equalsIgnoreCase(category.getName())) {
            redirectAttributes.addFlashAttribute("error", "No se puede editar la categoría 'General'");
            return "redirect:/categories";
        }

        model.addAttribute("category", category);
        model.addAttribute("active", "categories");
        model.addAttribute("content", "categories/form");
        return "layout";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Category category = categoryRepository.findById(id).orElse(null);

        // No permitir eliminar la categoría "General"
        if (category != null && "General".equalsIgnoreCase(category.getName())) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar la categoría 'General' porque es la categoría por defecto");
            return "redirect:/categories";
        }

        try {
            categoryRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Categoría eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar la categoría porque tiene productos asociados");
        }
        return "redirect:/categories";
    }
}