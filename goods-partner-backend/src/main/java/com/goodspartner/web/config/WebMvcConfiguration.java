package com.goodspartner.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/{x:[\\w\\-]+}").setViewName("forward:/index.html");
        registry.addViewController("/{x:^(?!api$).*$}/**/{y:[\\w\\-]+}").setViewName("forward:/index.html");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // TODO: Cors enabled for everything... Looks dangerous !
        registry.addMapping("/**");
    }


}
