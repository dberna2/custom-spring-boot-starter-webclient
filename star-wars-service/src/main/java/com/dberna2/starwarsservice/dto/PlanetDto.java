package com.dberna2.starwarsservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode
public class PlanetDto {

    private String name;
    private String gravity;
    private String climate;
    private String terrain;
    private Instant created;
    private Instant edited;
    private Long diameter;
    private Long population;
}
