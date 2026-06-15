package com.muhammadahmed.perf.support;

import java.util.concurrent.atomic.AtomicLong;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

@Component
public class SqlStatementCounter implements StatementInspector {

    private final AtomicLong count = new AtomicLong(0);

    @Override
    public String inspect(String sql) {
        count.incrementAndGet();
        return sql;
    }

    public void reset() {
        count.set(0);
    }

    public long getCount() {
        return count.get();
    }
}
