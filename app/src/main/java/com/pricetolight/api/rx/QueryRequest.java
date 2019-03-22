package com.pricetolight.api.rx;

public class QueryRequest implements RequestKey {

    private String query;

    public QueryRequest(String query) {
        this.query = query;
    }

    @Override
    public String getKey() {
        return query;
    }

}
