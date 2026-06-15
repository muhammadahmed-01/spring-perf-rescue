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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeederConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSeederConfig.class);

    @Bean
    @ConfigurationProperties(prefix = "app.seed")
    SeedProperties seedProperties() {
        return new SeedProperties();
    }

    @Bean
    CommandLineRunner seedDatabase(
            UserRepository userRepository,
            OrderRepository orderRepository,
            SeedProperties seedProperties) {
        return args -> {
            if (!seedProperties.isEnabled()) {
                log.info("Seed disabled");
                return;
            }
            if (userRepository.count() > 0) {
                log.info("Database already seeded ({} users)", userRepository.count());
                return;
            }

            List<User> users = new ArrayList<>();
            for (int u = 1; u <= seedProperties.getUserCount(); u++) {
                users.add(userRepository.save(new User("Customer " + u, "customer" + u + "@example.com")));
            }

            int orderCounter = 0;
            for (User user : users) {
                for (int o = 1; o <= seedProperties.getOrdersPerUser(); o++) {
                    Order order = new Order(user, "PLACED", Instant.now().minusSeconds(orderCounter * 60L));
                    for (int i = 1; i <= seedProperties.getItemsPerOrder(); i++) {
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
                    orderCounter * seedProperties.getItemsPerOrder());
        };
    }

    public static class SeedProperties {
        private boolean enabled = true;
        private int userCount = 10;
        private int ordersPerUser = 10;
        private int itemsPerOrder = 10;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getUserCount() {
            return userCount;
        }

        public void setUserCount(int userCount) {
            this.userCount = userCount;
        }

        public int getOrdersPerUser() {
            return ordersPerUser;
        }

        public void setOrdersPerUser(int ordersPerUser) {
            this.ordersPerUser = ordersPerUser;
        }

        public int getItemsPerOrder() {
            return itemsPerOrder;
        }

        public void setItemsPerOrder(int itemsPerOrder) {
            this.itemsPerOrder = itemsPerOrder;
        }
    }
}
