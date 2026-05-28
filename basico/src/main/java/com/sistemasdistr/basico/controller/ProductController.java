package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.model.Category;
import com.sistemasdistr.basico.model.Product;
import com.sistemasdistr.basico.repository.CategoryRepository;
import com.sistemasdistr.basico.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("active", "products");
        model.addAttribute("content", "products/list");
        return "layout";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("active", "products");
        model.addAttribute("content", "products/form");
        return "layout";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        try {
            // Si no se seleccionó ninguna categoría, asignar "General"
            if (product.getCategory() == null || product.getCategory().getId() == null) {
                Optional<Category> optionalGeneral = categoryRepository.findByName("General");
                Category generalCategory;

                if (optionalGeneral.isPresent()) {
                    generalCategory = optionalGeneral.get();
                } else {
                    // Si por algún motivo no existe "General", la creamos
                    generalCategory = new Category();
                    generalCategory.setName("General");
                    generalCategory.setDescription("Categoría general por defecto");
                    categoryRepository.save(generalCategory);
                }
                product.setCategory(generalCategory);
            }

            productRepository.save(product);
            redirectAttributes.addFlashAttribute("success", "Producto guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el producto: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("active", "products");
        model.addAttribute("content", "products/form");
        return "layout";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el producto");
        }
        return "redirect:/products";
    }
}