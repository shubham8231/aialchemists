package com.monitoring.service;

import com.monitoring.dto.OsvPackageInfo;
import com.monitoring.dto.OsvQueryRequest;
import com.monitoring.dto.OsvQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class OsvService {

    private static final String OSV_BASE_URL = "https://api.osv.dev";

    private final RestClient restClient;

    public OsvService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl(OSV_BASE_URL)
                .build();
    }

    public OsvQueryResponse scan(String ecosystem, String packageName, String version) {
        log.info("Scanning OSV for package={} ecosystem={} version={}", packageName, ecosystem, version);

        OsvQueryRequest request = OsvQueryRequest.builder()
                .version(version)
                .packageInfo(OsvPackageInfo.builder()
                        .name(packageName)
                        .ecosystem(ecosystem)
                        .build())
                .build();

        try {
            OsvQueryResponse response = restClient.post()
                    .uri("/v1/query")
                    .body(request)
                    .retrieve()
                    .body(OsvQueryResponse.class);

            return response != null ? response : new OsvQueryResponse();
        } catch (Exception e) {
            log.error("OSV scan failed for package={} version={}: {}", packageName, version, e.getMessage());
            return new OsvQueryResponse();
        }
    }
}
