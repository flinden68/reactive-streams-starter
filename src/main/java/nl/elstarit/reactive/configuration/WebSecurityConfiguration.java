package nl.elstarit.reactive.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfiguration {

  @Bean
  SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {
    return http
            .csrf().disable()
            .authorizeExchange()
            .pathMatchers("/reactive/streams/**/**").permitAll()
            .pathMatchers("/actuator/**").permitAll()
            .pathMatchers("/**").permitAll()
            .pathMatchers("/v2/**").permitAll()
            .pathMatchers("/swagger-resources/**").permitAll()
            .anyExchange().authenticated()
            .and()
            .build();
  }

}
