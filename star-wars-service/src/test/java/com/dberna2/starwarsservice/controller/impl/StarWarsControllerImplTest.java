package com.dberna2.starwarsservice.controller.impl;

import com.dberna2.starwarsservice.controller.StarWarsController;
import com.dberna2.starwarsservice.dto.PlanetDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.Instant;

@SpringBootTest
class StarWarsControllerImplTest {

    @Autowired
    private StarWarsController controller;

    @Test
    void getPlanetById() {

        PlanetDto planetDto = new PlanetDto();
        planetDto.setName("Tatooine");
        planetDto.setGravity("1 standard");
        planetDto.setClimate("arid");
        planetDto.setTerrain("desert");
        planetDto.setCreated(Instant.parse("2014-12-09T13:50:49.641Z"));
        planetDto.setEdited(Instant.parse("2014-12-20T20:58:18.411Z"));
        planetDto.setDiameter(10465L);
        planetDto.setPopulation(200000L);

        String planetId = "1";
        StepVerifier.create(controller.getPlanetById(planetId))
                .expectNext(planetDto).verifyComplete();

    }
}