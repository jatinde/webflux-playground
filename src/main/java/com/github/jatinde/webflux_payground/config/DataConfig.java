package com.github.jatinde.webflux_payground.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcAuditing
@EnableR2dbcRepositories(basePackages="com.github.jatinde.webflux_payground.${repo}")
public class DataConfig {
    
}
