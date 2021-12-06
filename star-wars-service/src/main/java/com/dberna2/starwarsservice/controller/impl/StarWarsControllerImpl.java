package com.dberna2.starwarsservice.controller.impl;

import com.dberna2.starwarsservice.controller.StarWarsController;
import com.dberna2.starwarsservice.dto.PlanetDto;
import com.dberna2.starwarsservice.service.StarWarsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class StarWarsControllerImpl implements StarWarsController {

    private final StarWarsService starWarsService;

    @Override
    public Mono<PlanetDto> getPlanetById(String id) {
        return starWarsService.getPlanetById(id);
    }
}
