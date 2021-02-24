package com.adrianbadarau.klist.front_end.config

import com.adrianbadarau.klist.front_end.gateway.accesscontrol.AccessControlFilter
import com.adrianbadarau.klist.front_end.gateway.responserewriting.SwaggerBasePathRewritingFilter
import io.github.jhipster.config.JHipsterProperties
import org.springframework.cloud.netflix.zuul.filters.RouteLocator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayConfiguration {

    @Configuration
    class SwaggerBasePathRewritingConfiguration {
        @Bean
        fun swaggerBasePathRewritingFilter() = SwaggerBasePathRewritingFilter()
    }

    @Configuration
    class AccessControlFilterConfiguration {
        @Bean
        fun accessControlFilter(routeLocator: RouteLocator, jHipsterProperties: JHipsterProperties) =
            AccessControlFilter(routeLocator, jHipsterProperties)
    }
}
