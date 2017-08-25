package com.dabakovich.config;

import com.dabakovich.service.HttpsService;
import com.dabakovich.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by dabak on 16.08.2017, 20:46.
 */
@Configuration
@EnableScheduling
@PropertySource(value = {"classpath:config.properties"}, encoding = "UTF-8")
public class AppConfig {

    @Bean
    public ReloadableResourceBundleMessageSource resourceBundleMessageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setDefaultEncoding("UTF-8");
        source.addBasenames("classpath:books", "classpath:messages");
        return source;
    }

    @Bean
    public TelegramService telegramService(@Value("${telegram.token}") String token, @Autowired HttpsService httpsService) {
        return new TelegramService(token, httpsService);
    }

    @Bean
    public HttpsService httpsService() {
        return new HttpsService();
    }
}
