package com.sistemasdistr.basico.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public String handleDatabaseException(DataAccessException ex, Model model) {
        model.addAttribute("error", "Error de base de datos: " + ex.getMessage());
        model.addAttribute("titulo", "Excepción de Base de Datos");
        return "error";
    }

    @ExceptionHandler(RestClientException.class)
    public String handleApiException(RestClientException ex, Model model) {
        model.addAttribute("error", "Error al llamar a API externa: " + ex.getMessage());
        model.addAttribute("titulo", "Excepción de API");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("error", "Error inesperado: " + ex.getMessage());
        model.addAttribute("titulo", "Excepción General");
        return "error";
    }
}