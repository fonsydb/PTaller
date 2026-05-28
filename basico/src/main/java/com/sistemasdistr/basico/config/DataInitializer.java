package com.sistemasdistr.basico.config;

import com.sistemasdistr.basico.model.Category;
import com.sistemasdistr.basico.model.Role;
import com.sistemasdistr.basico.model.User;
import com.sistemasdistr.basico.model.Store;
import com.sistemasdistr.basico.model.Transaction;
import com.sistemasdistr.basico.repository.CategoryRepository;
import com.sistemasdistr.basico.repository.RoleRepository;
import com.sistemasdistr.basico.repository.UserRepository;
import com.sistemasdistr.basico.repository.StoreRepository;
import com.sistemasdistr.basico.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        System.out.println("=== Verificando datos iniciales ===");

        // Crear roles si no existen
        Role adminRole = null;
        Role userRole = null;

        if (roleRepository.count() == 0) {
            System.out.println("Creando roles...");
            adminRole = new Role();
            adminRole.setRoleName("ROLE_ADMIN");
            adminRole.setShowOnCreate(1);
            roleRepository.save(adminRole);

            userRole = new Role();
            userRole.setRoleName("ROLE_USER");
            userRole.setShowOnCreate(1);
            roleRepository.save(userRole);
            System.out.println("Roles creados");
        } else {
            for (Role role : roleRepository.findAll()) {
                if ("ROLE_ADMIN".equals(role.getRoleName())) {
                    adminRole = role;
                } else if ("ROLE_USER".equals(role.getRoleName())) {
                    userRole = role;
                }
            }
        }

        // Crear usuario admin si no existe
        if (userRepository.findUserByUsername("admin") == null && adminRole != null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@sistdistrib.com");
            admin.setNombreUsuario("Administrador");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setUserRole(adminRole);
            userRepository.save(admin);
            System.out.println("Usuario admin creado");
        }

        // Crear usuario user si no existe
        if (userRepository.findUserByUsername("user") == null && userRole != null) {
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@sistdistrib.com");
            user.setNombreUsuario("Usuario Normal");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setUserRole(userRole);
            userRepository.save(user);
            System.out.println("Usuario user creado");
        }

        // Crear categoría "General" por defecto si no existe
        boolean existeGeneral = false;
        for (Category cat : categoryRepository.findAll()) {
            if ("General".equals(cat.getName())) {
                existeGeneral = true;
                System.out.println("Categoría 'General' ya existe");
                break;
            }
        }

        if (!existeGeneral) {
            Category general = new Category();
            general.setName("General");
            general.setDescription("Categoría por defecto");
            categoryRepository.save(general);
            System.out.println("✅ Categoría 'General' creada");
        }

        // Crear tiendas de ejemplo
        if (storeRepository.count() == 0) {
            Store store1 = new Store();
            store1.setName("Tienda Central");
            store1.setAddress("Calle Mayor, 1, Madrid");
            store1.setLatitude(40.416775);
            store1.setLongitude(-3.703790);
            store1.setPhone("+34 91 123 45 67");
            store1.setSchedule("Lunes a Viernes 9:00-20:00");
            storeRepository.save(store1);

            Store store2 = new Store();
            store2.setName("Sucursal Norte");
            store2.setAddress("Avenida de América, 50, Madrid");
            store2.setLatitude(40.435000);
            store2.setLongitude(-3.660000);
            store2.setPhone("+34 91 234 56 78");
            store2.setSchedule("Lunes a Sábado 10:00-21:00");
            storeRepository.save(store2);

            Store store3 = new Store();
            store3.setName("Sucursal Sur");
            store3.setAddress("Paseo de la Castellana, 89, Madrid");
            store3.setLatitude(40.430000);
            store3.setLongitude(-3.690000);
            store3.setPhone("+34 91 345 67 89");
            store3.setSchedule("Lunes a Domingo 10:00-22:00");
            storeRepository.save(store3);

            System.out.println("✅ Tiendas de ejemplo creadas");
        }

        // Crear transacciones de ejemplo
        if (transactionRepository.count() == 0) {
            Transaction tx1 = new Transaction();
            tx1.setTransactionId("TXN-" + System.currentTimeMillis());
            tx1.setCardNumber("**** **** **** 1234");
            tx1.setCardHolder("ADMIN TEST");
            tx1.setAmount(new BigDecimal("99.99"));
            tx1.setStatus("COMPLETED");
            tx1.setDescription("Compra de prueba");
            tx1.setPaymentMethod("CREDIT_CARD");
            transactionRepository.save(tx1);
        }

        System.out.println("=== Inicialización completa ===");
    }
}