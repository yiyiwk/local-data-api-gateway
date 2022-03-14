package com.sharkapi.web.uiservice.pojo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "sharkconfig.jdbc")
public class JdbcCatalog {

    private Integer timeout = 10000;
    private Map<String, Map<String, String>> catalog = new LinkedHashMap();

    public JdbcCatalog() {}

    public Integer getTimeout() {
        return this.timeout;
    }

    public Map<String, Map<String, String>> getCatalog() {
        return this.catalog;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public void setCatalog(Map<String, Map<String, String>> catalog) {
        this.catalog = catalog;
    }

}
