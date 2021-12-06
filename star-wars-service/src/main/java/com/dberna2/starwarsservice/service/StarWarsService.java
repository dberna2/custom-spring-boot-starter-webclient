package com.dberna2.starwarsservice.service;

import com.dberna2.starwarsservice.dto.PlanetDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StarWarsService {

    Mono<PlanetDto> getPlanetById(String id);
}
