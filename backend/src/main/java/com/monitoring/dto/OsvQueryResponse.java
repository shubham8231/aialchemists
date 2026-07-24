package com.monitoring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OsvQueryResponse {

    private List<OsvVulnerability> vulns = new ArrayList<>();
}
