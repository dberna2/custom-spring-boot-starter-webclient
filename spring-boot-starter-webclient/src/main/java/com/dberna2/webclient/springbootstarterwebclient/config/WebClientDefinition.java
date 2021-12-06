package com.dberna2.webclient.springbootstarterwebclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "webclient")
public class WebClientDefinition {

    private String baseUrl;
    private String accept = "application/json";
    /* Connection timeout in millis */
    private int connectTimeOut = 30000;
    /* Connection timeout in seconds */
    private int responseTimeOut = 30;
    /* Connection timeout in seconds */
    private int readTimeOut = 30;
    /* Connection timeout in seconds */
    private int writeTimeOut = 30;
    /* Enabled proxy authentication */
    private boolean proxyEnabled = false;
    /* Custom proxy definition */
    @NestedConfigurationProperty
    private ProxyDefinition proxy;

    @Data
    static class ProxyDefinition {
        private String host;
        private int port;
        private String username;
        private String password;
        public String nonProxyHosts;
    }
}
