package com.muhammadahmed.perf.config;

import com.muhammadahmed.perf.domain.Order;
import com.muhammadahmed.perf.domain.OrderItem;
import com.muhammadahmed.perf.domain.User;
import com.muhammadahmed.perf.repository.OrderRepository;
import com.muhammadahmed.perf.repository.UserRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DataSeederConfig.SeedProperties.class)
public class DataSeederConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSeederConfig.class);

    @Bean
    CommandLineRunner seedDatabase(
            UserRepository userRepository,
            OrderRepository orderRepository,
            SeedProperties seedProperties) {
        return args -> {
            if (!seedProperties.enabled()) {
                log.info("Seed disabled");
                return;
            }
            if (userRepository.count() > 0) {
                log.info("Database already seeded ({} users)", userRepository.count());
                return;
            }

            List<User> users = new ArrayList<>();
            for (int u = 1; u <= seedProperties.userCount(); u++) {
                users.add(userRepository.save(new User("Customer " + u, "customer" + u + "@example.com")));
            }

            int orderCounter = 0;
            for (User user : users) {
                for (int o = 1; o <= seedProperties.ordersPerUser(); o++) {
                    Order order = new Order(user, "PLACED", Instant.now().minusSeconds(orderCounter * 60L));
                    for (int i = 1; i <= seedProperties.itemsPerOrder(); i++) {
                        order.addItem(new OrderItem(
                                "SKU-" + user.getId() + "-" + o + "-" + i,
                                1 + (i % 3),
                                BigDecimal.valueOf(9.99 + i)));
                    }
                    orderRepository.save(order);
                    orderCounter++;
                }
            }

            log.info(
                    "Seeded {} users, {} orders, ~{} line items",
                    users.size(),
                    orderCounter,
                    orderCounter * seedProperties.itemsPerOrder());
        };
    }

    @ConfigurationProperties(prefix = "app.seed")
    public record SeedProperties(
            boolean enabled,
            int userCount,
            int ordersPerUser,
            int itemsPerOrder) {

        public SeedProperties() {
            this(true, 10, 10, 10);
        }
    }
}
