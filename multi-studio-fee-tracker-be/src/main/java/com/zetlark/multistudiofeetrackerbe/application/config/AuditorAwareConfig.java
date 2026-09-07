package com.zetlark.multistudiofeetrackerbe.application.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class AuditorAwareConfig {

    private static final String ANONYMOUS_PRINCIPAL = "anonymousUser";

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null
                    || !authentication.isAuthenticated()
                    || ANONYMOUS_PRINCIPAL.equals(authentication.getPrincipal())) {
                return Optional.empty();
            }
            return Optional.of(authentication.getName());
        };
    }
}
