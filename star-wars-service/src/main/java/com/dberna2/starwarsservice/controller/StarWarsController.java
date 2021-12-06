package com.dberna2.starwarsservice.controller;

import com.dberna2.starwarsservice.dto.PlanetDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@RequestMapping("/sw/api")
public interface StarWarsController {

    @GetMapping("/planets/{id}")
    Mono<PlanetDto> getPlanetById(@PathVariable String id);
}
