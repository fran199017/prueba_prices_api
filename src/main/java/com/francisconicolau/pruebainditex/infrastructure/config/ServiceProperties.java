package com.francisconicolau.pruebainditex.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@EnableConfigurationProperties
@ConfigurationProperties(prefix = "services")
@Data
@Component
public class ServiceProperties {

    private Map<Integer, String> statusMap = new HashMap<>();

    private String statusOkMessage;

    private static final String DEFAULT_MESSAGE = "NOT CONFIGURED THIS STATUS CODE IN PROPERTIES: ";

    public String getStatusMessage(final Integer idStatus) {
        String message = statusMap.get(idStatus);
        return message != null ? message : DEFAULT_MESSAGE + idStatus;
    }

}