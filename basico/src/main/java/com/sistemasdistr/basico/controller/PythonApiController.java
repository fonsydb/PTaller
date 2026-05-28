package com.sistemasdistr.basico.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

@Controller
@RequestMapping("/python-api")
public class PythonApiController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String PYTHON_API_URL = "http://localhost:5000/api";

    @GetMapping
    public String showPythonApiPage(Model model) {
        model.addAttribute("resultado", "");
        model.addAttribute("error", "");
        model.addAttribute("loading", false);
        model.addAttribute("active", "python");
        model.addAttribute("content", "python-api");
        return "layout";
    }

    @PostMapping("/test")
    public String testException(@RequestParam String tipoError, Model model) {
        model.addAttribute("loading", true);
        model.addAttribute("resultado", "");
        model.addAttribute("error", "");
        model.addAttribute("active", "python");

        String url = PYTHON_API_URL + "/test-exception/" + tipoError;

        try {
            System.out.println("Llamando a Python API: " + url);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            model.addAttribute("resultado", "✅ Éxito: " + response.getBody());
            model.addAttribute("tipoProbado", tipoError);
        } catch (RestClientException e) {
            System.err.println("Error al llamar Python API: " + e.getMessage());
            model.addAttribute("error", "❌ Error conectando con API Python: " + e.getMessage());
            model.addAttribute("tipoProbado", tipoError);
        } catch (Exception e) {
            model.addAttribute("error", "❌ Error inesperado: " + e.getMessage());
            model.addAttribute("tipoProbado", tipoError);
        }

        model.addAttribute("loading", false);
        model.addAttribute("content", "python-api");
        return "layout";
    }

    @GetMapping("/health")
    @ResponseBody
    public String checkPythonHealth() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:5000/api/health", String.class);
            return "Python API: " + response.getBody();
        } catch (Exception e) {
            return "Python API no disponible: " + e.getMessage();
        }
    }
}