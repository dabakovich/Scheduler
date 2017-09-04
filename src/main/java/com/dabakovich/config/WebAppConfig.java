package com.dabakovich.config;

import com.dabakovich.service.HttpsService;
import com.dabakovich.service.TelegramService;
import com.dabakovich.service.utils.LanguageContainer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.ServletContext;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dabak on 16.08.2017, 15:18.
 */
@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan("com.dabakovich")
@EnableMongoRepositories("com.dabakovich.repository")
@PropertySource(value = {"classpath:config.properties"}, encoding = "UTF-8")
public class WebAppConfig extends WebMvcConfigurerAdapter {

    @Bean
    public MongoTemplate mongoTemplate() throws UnknownHostException {
        return new MongoTemplate(new MongoClient("localhost"), "scheduler");
    }

    @Bean
    public TelegramService telegramService(@Value("${telegram.serverURL}") String serverURL,
                                           @Value("${telegram.token}") String token,
                                           @Value("${telegram.cert}") String certPath,
                                           @Autowired HttpsService httpsService) {
        return new TelegramService(serverURL, token, certPath, httpsService);
    }

    @Bean
    public ReloadableResourceBundleMessageSource resourceBundleMessageSource(ServletContext context) {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setDefaultEncoding("UTF-8");
        source.addBasenames("classpath:books", "classpath:messages");

        return source;
    }

    @Bean
    public LanguageContainer languageContainer() {
        LanguageContainer languageContainer = new LanguageContainer();
        languageContainer.setLanguages(Arrays.asList("en:English", "uk:Українська", "ru:Русский"));

        return languageContainer;
    }

    @Bean
    public HttpsService httpsService() {
        return new HttpsService();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jackson2ObjectMapperBuilder mapperBuilder = new Jackson2ObjectMapperBuilder()
                .indentOutput(true)
                .dateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        ParameterNamesModule parameterNamesModule = new ParameterNamesModule();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        mapperBuilder.modulesToInstall(parameterNamesModule, javaTimeModule);
//        ObjectMapper mapper = new ObjectMapper();

        mapperBuilder.indentOutput(true);
        converters.add(new MappingJackson2HttpMessageConverter(mapperBuilder.build()));
        super.configureMessageConverters(converters);
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        super.configureViewResolvers(registry);
    }
}
