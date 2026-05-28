package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.dto.UserDto;
import com.sistemasdistr.basico.model.Role;
import com.sistemasdistr.basico.model.User;
import com.sistemasdistr.basico.repository.RoleRepository;
import com.sistemasdistr.basico.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("active", "users");
        model.addAttribute("content", "users/list");
        return "layout";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("active", "users");
        model.addAttribute("content", "users/form");
        return "layout";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute UserDto userDto, RedirectAttributes redirectAttributes) {
        try {
            // Validar que las contraseñas coincidan
            if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
                return "redirect:/users/create";
            }

            // Verificar si el usuario ya existe
            User existingUser = userRepository.findUserByUsername(userDto.getUsername());
            if (existingUser != null && (userDto.getId() == null || !existingUser.getId().equals(userDto.getId()))) {
                redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya existe");
                return "redirect:/users/create";
            }

            User user;
            if (userDto.getId() != null) {
                user = userRepository.findById(userDto.getId()).orElse(new User());
            } else {
                user = new User();
            }

            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            user.setNombreUsuario(userDto.getNombreUsuario());

            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }

            Role role = roleRepository.findById(userDto.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            user.setUserRole(role);

            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "Usuario guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el usuario: " + e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));

        // Obtener el usuario actualmente logueado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        // Si el usuario a editar es "admin", redirigir con mensaje de error
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "No se puede editar el usuario administrador principal");
            return "redirect:/users";
        }

        // Si el usuario actual no es admin y quiere editar a otro admin
        boolean isAdmin = user.getUserRole().getRoleName().equals("ROL_ADMIN");
        boolean currentIsAdmin = currentUsername.equals("admin");

        if (isAdmin && !currentIsAdmin) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para editar este usuario");
            return "redirect:/users";
        }

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setNombreUsuario(user.getNombreUsuario());
        userDto.setRoleId(user.getUserRole().getId());

        model.addAttribute("userDto", userDto);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("active", "users");
        model.addAttribute("content", "users/form");
        return "layout";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(id).orElse(null);

        // No permitir eliminar al usuario admin
        if (user != null && "admin".equalsIgnoreCase(user.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar el usuario administrador principal");
            return "redirect:/users";
        }

        // No permitir eliminar al usuario 'user'
        if (user != null && "user".equalsIgnoreCase(user.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar el usuario 'user' porque es un usuario por defecto del sistema");
            return "redirect:/users";
        }

        // No permitir eliminar a otros administradores (opcional)
        if (user != null && user.getUserRole().getRoleName().equals("ROL_ADMIN")) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar usuarios con rol ADMIN");
            return "redirect:/users";
        }

        try {
            userRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el usuario");
        }
        return "redirect:/users";
    }
}