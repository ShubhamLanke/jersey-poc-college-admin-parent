package com.dss.api.impl.config;

import com.dss.api.impl.service.CategoryServiceImpl;
import com.dss.api.impl.service.ProductServiceImpl;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig(){
        register(ProductServiceImpl.class);
        register(CategoryServiceImpl.class);
    }
}
