package com.evolve.config;

import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;

import com.evolve.codec.UserDeserializer;
import com.evolve.codec.UserSerializer;
import com.evolve.model.Usr;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.context.annotation.Bean;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        ObjectMapper mapper = customObjectMapper();
        configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(mapper));
        configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper));
    }

    @Bean
    public ObjectMapper customObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Usr.class, new UserSerializer());
        module.addDeserializer(Usr.class, new UserDeserializer());
        mapper.registerModule(module);
        return mapper;
    }
}

