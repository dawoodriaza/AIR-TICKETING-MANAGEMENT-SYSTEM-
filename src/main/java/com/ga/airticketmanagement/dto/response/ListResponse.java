package com.ga.airticketmanagement.dto.response;

import java.util.List;
import java.util.Map;

public record ListResponse<T>(List<T> data, PageMeta meta) {}
