package com.monitoring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OsvQueryRequest {

    private String version;

    @JsonProperty("package")
    private OsvPackageInfo packageInfo;
}
