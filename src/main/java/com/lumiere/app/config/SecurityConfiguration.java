package com.lumiere.app.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.lumiere.app.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import tech.jhipster.config.JHipsterProperties;

import java.util.List;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final JHipsterProperties jHipsterProperties;

    public SecurityConfiguration(JHipsterProperties jHipsterProperties) {
        this.jHipsterProperties = jHipsterProperties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("http://localhost:5173"));
                config.setAllowedMethods(List.of("GET","POST","PUT","DELETE"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                return config;
            })).csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz ->
                authz
                    .requestMatchers("/api/media/**").permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/authenticate")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/authenticate")).permitAll()
                    .requestMatchers(mvc.pattern("/api/register")).permitAll()
                    .requestMatchers(mvc.pattern("/api/activate")).permitAll()
                    .requestMatchers(mvc.pattern("/api/account/reset-password/init")).permitAll()
                    .requestMatchers(mvc.pattern("/api/account/reset-password/finish")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/orders/create-guest-order")).permitAll()
                    // Product APIs - cho phép khách vãng lai xem sản phẩm
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/products/search")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/products/{id}")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/products/{id}/images-map")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/products")).permitAll()
                    // Product Variant APIs - cho phép khách vãng lai xem variants
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/product-variants")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/product-variants/by-product-ids")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/product-variants/{id}")).permitAll()
                    // Product Review APIs - cho phép khách vãng lai xem đánh giá
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/product-reviews/by-product/{productId}")).permitAll()
                    // Home APIs - cho phép khách vãng lai xem trang chủ
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/home/**")).permitAll()
                    // Flash Sale APIs - cho phép khách vãng lai xem flash sale
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/flash-sales/**")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/flash-sale-products/**")).permitAll()
                    .requestMatchers(mvc.pattern("/api/admin/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(mvc.pattern("/api/**")).authenticated()
                    .requestMatchers(mvc.pattern("/v3/api-docs/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(mvc.pattern("/management/health")).permitAll()
                    .requestMatchers(mvc.pattern("/management/health/**")).permitAll()
                    .requestMatchers(mvc.pattern("/management/info")).permitAll()
                    .requestMatchers(mvc.pattern("/management/prometheus")).permitAll()
                    .requestMatchers("/ws/**", "/sockjs/**", "/app/**", "/topic/**", "/queue/**", "/ws/info").permitAll()
                    .requestMatchers(mvc.pattern("/management/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions ->
                exceptions
                    .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                    .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
}
