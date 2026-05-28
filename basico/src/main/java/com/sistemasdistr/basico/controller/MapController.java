package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.model.Store;
import com.sistemasdistr.basico.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/maps")
public class MapController {

    @Autowired
    private StoreRepository storeRepository;

    // Página principal del mapa
    @GetMapping
    public String mapIndex(Model model) {
        List<Store> stores = storeRepository.findAll();
        model.addAttribute("stores", stores);
        return "maps/index";
    }

    // Formulario para agregar tienda
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("store", new Store());
        return "maps/form";
    }

    // API para obtener todas las tiendas (formato JSON)
    @GetMapping("/api/stores")
    @ResponseBody
    public List<Store> getStores() {
        return storeRepository.findAll();
    }

    // Guardar tienda
    @PostMapping("/save")
    public String saveStore(@ModelAttribute Store store, RedirectAttributes redirectAttributes) {
        try {
            storeRepository.save(store);
            redirectAttributes.addFlashAttribute("success", "Tienda agregada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la tienda");
        }
        return "redirect:/maps";
    }

    // Eliminar tienda
    @GetMapping("/delete/{id}")
    public String deleteStore(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            storeRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Tienda eliminada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la tienda");
        }
        return "redirect:/maps";
    }
}