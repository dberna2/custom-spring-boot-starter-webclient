package com.dberna2.webclient.springbootstarterwebclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SpringBootStarterWebclientApplicationTests {

    @Autowired
    private WebClient webClient;

    @Test
    void testContextLoads() {
        assertNotNull(webClient);
    }

}