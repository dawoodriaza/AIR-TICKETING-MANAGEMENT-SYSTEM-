package com.ga.airticketmanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record PageMeta (
    int currentPage,
    int perPage,
    long total,
    int totalPages,
    Integer nextPage,
    Integer prevPage
 ){}
