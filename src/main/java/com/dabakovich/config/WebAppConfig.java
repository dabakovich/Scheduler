package com.dabakovich.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by dabak on 16.08.2017, 15:18.
 */
@Configuration
@EnableWebMvc
@ComponentScan("com.dabakovich")
@EnableMongoRepositories("com.dabakovich.repository")
public class WebAppConfig extends WebMvcConfigurerAdapter {

    @Bean
    public MongoTemplate mongoTemplate() throws UnknownHostException {
        return new MongoTemplate(new MongoClient("localhost"), "scheduler");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jackson2ObjectMapperBuilder mapperBuilder = new Jackson2ObjectMapperBuilder()
                .indentOutput(true)
                .dateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        ParameterNamesModule parameterNamesModule = new ParameterNamesModule();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        mapperBuilder.modulesToInstall(parameterNamesModule, javaTimeModule);
        ObjectMapper mapper = new ObjectMapper();

        mapperBuilder.indentOutput(true);
        converters.add(new MappingJackson2HttpMessageConverter(mapperBuilder.build()));
        super.configureMessageConverters(converters);
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        super.configureViewResolvers(registry);
    }
}
