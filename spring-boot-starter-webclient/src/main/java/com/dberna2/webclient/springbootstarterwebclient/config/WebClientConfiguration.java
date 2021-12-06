package com.dberna2.webclient.springbootstarterwebclient.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(WebClientDefinition.class)
public class WebClientConfiguration {

    private static final String ISO_LOCAL_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    private static final String LOCAL_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String LOCAL_DATE_FORMAT = "yyyy-MM-dd";

    private final WebClientDefinition clientDefinition;

    @Bean
    public WebClient configureWebClient() {
        return WebClient.builder()
                .baseUrl(clientDefinition.getBaseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, clientDefinition.getAccept())
                .exchangeStrategies(this.buildExchangeStrategies())
                .clientConnector(this.buildClientHttpConnector())
                .filters(this::buildFilters)
                .build();
    }

    private ExchangeStrategies buildExchangeStrategies() {
        ObjectMapper objectMapper = this.buildCustomWebClientObjectMapper();
        return ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, APPLICATION_JSON));
                    configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, APPLICATION_JSON));
                }).build();
    }

    private ObjectMapper buildCustomWebClientObjectMapper() {
        return new Jackson2ObjectMapperBuilder()
                .indentOutput(true)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .simpleDateFormat(ISO_LOCAL_DATE_TIME_FORMAT)
                .serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(LOCAL_DATE_FORMAT)))
                .serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_FORMAT)))
                .build();
    }

    private ReactorClientHttpConnector buildClientHttpConnector() {
        HttpClient httpClient = this.createHttpClient();
        return new ReactorClientHttpConnector(httpClient);
    }

    private HttpClient createHttpClient() {
        if (clientDefinition.isProxyEnabled()) {
            Objects.requireNonNull(clientDefinition.getProxy(), "If proxy is enabled clientDefinition cannot be null.");
            return HttpClient.create()
                    .responseTimeout(Duration.ofSeconds(clientDefinition.getResponseTimeOut()))
                    .proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
                            .host(clientDefinition.getProxy().getHost())
                            .port(clientDefinition.getProxy().getPort())
                            .username(clientDefinition.getProxy().getUsername())
                            .password(username -> clientDefinition.getProxy().getPassword())
                            .nonProxyHosts(clientDefinition.getProxy().getNonProxyHosts())
                    )
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, clientDefinition.getConnectTimeOut())
                    .doOnConnected(this::connectionTimeOut);
        } else {
            return HttpClient.create()
                    .responseTimeout(Duration.ofSeconds(clientDefinition.getResponseTimeOut()))
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, clientDefinition.getConnectTimeOut())
                    .doOnConnected(this::connectionTimeOut);
        }
    }

    private void connectionTimeOut(Connection connection) {
        connection.addHandlerLast(new ReadTimeoutHandler(clientDefinition.getReadTimeOut(), TimeUnit.SECONDS));
        connection.addHandlerLast(new WriteTimeoutHandler(clientDefinition.getWriteTimeOut(), TimeUnit.SECONDS));
    }

    private void buildFilters(List<ExchangeFilterFunction> filterFunctions) {
        filterFunctions.add(logFilterRequest());
        filterFunctions.add(logFilterResponse());
    }

    private static ExchangeFilterFunction logFilterRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            StringBuilder builder = new StringBuilder("Request: ");
            builder.append(System.lineSeparator())
                    .append(clientRequest.method()).append(" ").append(clientRequest.url())
                    .append(System.lineSeparator())
                    .append("Headers :");

            clientRequest.headers().forEach((name, values) -> {
                values.forEach(value -> builder.append(name).append(": ").append(value)
                        .append(System.lineSeparator()));
            });
            log.info(builder.toString());
            return Mono.just(clientRequest);
        });
    }

    public static ExchangeFilterFunction logFilterResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            HttpStatus status = response.statusCode();
            log.info("Returned status code ({} {})", status.value(), status.getReasonPhrase());
            return Mono.just(response);
        });
    }
}
