package com.muhammadahmed.perf.config;

import com.muhammadahmed.perf.support.SqlStatementCounter;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaConfig {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(SqlStatementCounter sqlStatementCounter) {
        return properties -> properties.put(AvailableSettings.STATEMENT_INSPECTOR, sqlStatementCounter);
    }
}
