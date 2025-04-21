package com.bytebandit.gateway.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.permitted")
@Getter
@Setter
public class PermittedRoutesConfig {

    private List<String> routes;
}
