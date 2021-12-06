package com.dberna2.starwarsservice.service.impl;

import com.dberna2.starwarsservice.dto.PlanetDto;
import com.dberna2.starwarsservice.service.StarWarsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StarWarsServiceImpl implements StarWarsService {

    private final WebClient swClient;

    @Override
    public Mono<PlanetDto> getPlanetById(String id) {
        return swClient.get()
                .uri("/planets/{id}", id)
                .retrieve()
                .bodyToMono(PlanetDto.class);
    }
}
