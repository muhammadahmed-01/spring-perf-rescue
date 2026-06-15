package com.muhammadahmed.perf.support;

import org.springframework.stereotype.Component;

@Component
public class QueryCountHolder {

    private final SqlStatementCounter sqlStatementCounter;

    public QueryCountHolder(SqlStatementCounter sqlStatementCounter) {
        this.sqlStatementCounter = sqlStatementCounter;
    }

    public void reset() {
        sqlStatementCounter.reset();
    }

    public long getQueryCount() {
        return sqlStatementCounter.getCount();
    }
}
