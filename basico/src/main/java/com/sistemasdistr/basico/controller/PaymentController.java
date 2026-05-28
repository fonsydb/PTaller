package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.dto.PaymentDto;
import com.sistemasdistr.basico.model.Transaction;
import com.sistemasdistr.basico.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private TransactionRepository transactionRepository;

    // Página de pago
    @GetMapping
    public String showPaymentForm(Model model) {
        model.addAttribute("paymentDto", new PaymentDto());
        model.addAttribute("active", "payment");
        model.addAttribute("content", "payment/form");
        return "layout";
    }

    // Procesar pago
    @PostMapping("/process")
    public String processPayment(@ModelAttribute PaymentDto paymentDto,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Validaciones básicas
            String validationError = validatePayment(paymentDto);
            if (validationError != null) {
                redirectAttributes.addFlashAttribute("error", validationError);
                return "redirect:/payment";
            }

            // Simular procesamiento de pago
            String transactionId = generateTransactionId();
            boolean paymentSuccess = simulatePaymentProcessing(paymentDto);

            // Crear transacción
            Transaction transaction = new Transaction();
            transaction.setTransactionId(transactionId);
            transaction.setCardNumber(maskCardNumber(paymentDto.getCardNumber()));
            transaction.setCardHolder(paymentDto.getCardHolder().toUpperCase());
            transaction.setAmount(paymentDto.getAmount());
            transaction.setStatus(paymentSuccess ? "COMPLETED" : "FAILED");
            transaction.setDescription(paymentDto.getDescription());
            transaction.setPaymentMethod("CREDIT_CARD");
            transaction.setTransactionDate(LocalDateTime.now());

            transactionRepository.save(transaction);

            if (paymentSuccess) {
                redirectAttributes.addFlashAttribute("success", "✅ Pago procesado correctamente");
                redirectAttributes.addFlashAttribute("transactionId", transactionId);
                redirectAttributes.addFlashAttribute("amount", paymentDto.getAmount());
                redirectAttributes.addFlashAttribute("lastFour", maskCardNumber(paymentDto.getCardNumber()));
            } else {
                redirectAttributes.addFlashAttribute("error", "❌ El pago fue rechazado. Verifica los datos de la tarjeta.");
            }

            return "redirect:/payment/result";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar el pago: " + e.getMessage());
            return "redirect:/payment";
        }
    }

    // Página de resultado
    @GetMapping("/result")
    public String paymentResult() {
        return "payment/result";
    }

    // Historial de transacciones
    @GetMapping("/history")
    public String paymentHistory(Model model) {
        List<Transaction> transactions = transactionRepository.findAllByOrderByTransactionDateDesc();
        model.addAttribute("transactions", transactions);
        model.addAttribute("active", "payment");
        model.addAttribute("content", "payment/history");
        return "layout";
    }

    // Validar datos de pago
    private String validatePayment(PaymentDto paymentDto) {
        // Validar número de tarjeta (16 dígitos)
        String cardNumber = paymentDto.getCardNumber().replaceAll("\\s", "");
        if (!cardNumber.matches("\\d{16}")) {
            return "Número de tarjeta inválido (debe tener 16 dígitos)";
        }

        // Validar titular
        if (paymentDto.getCardHolder() == null || paymentDto.getCardHolder().trim().isEmpty()) {
            return "Nombre del titular es requerido";
        }

        // Validar mes (01-12)
        String month = paymentDto.getExpiryMonth();
        if (!month.matches("(0[1-9]|1[0-2])")) {
            return "Mes de expiración inválido (01-12)";
        }

        // Validar año (año actual o futuro)
        int year = Integer.parseInt(paymentDto.getExpiryYear());
        int currentYear = LocalDateTime.now().getYear() % 100;
        if (year < currentYear || year > currentYear + 10) {
            return "Año de expiración inválido";
        }

        // Validar CVV (3 dígitos)
        if (!paymentDto.getCvv().matches("\\d{3}")) {
            return "CVV inválido (debe tener 3 dígitos)";
        }

        // Validar monto
        if (paymentDto.getAmount() == null || paymentDto.getAmount().compareTo(new java.math.BigDecimal("0.01")) < 0) {
            return "Monto inválido";
        }

        return null;
    }

    // Simular procesamiento de pago
    private boolean simulatePaymentProcessing(PaymentDto paymentDto) {
        // Simulación: aceptar tarjetas que terminan en 0,2,4,6,8
        String lastDigit = paymentDto.getCardNumber().replaceAll("\\s", "").substring(15);
        int digit = Integer.parseInt(lastDigit);
        return digit % 2 == 0;
    }

    // Generar ID de transacción único
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() +
                "-" + System.currentTimeMillis() % 10000;
    }

    // Enmascarar número de tarjeta
    private String maskCardNumber(String cardNumber) {
        String cleaned = cardNumber.replaceAll("\\s", "");
        return "**** **** **** " + cleaned.substring(12);
    }
}