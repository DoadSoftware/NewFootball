package com.football.config.core;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import com.football.config.WebMvcConfig;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;

public class SpringMvcInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] {WebMvcConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

   @Override
   protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setMultipartConfig(new MultipartConfigElement(
            "",          // location - "" uses default temp dir
            20971520,    // max file size: 20MB
            41943040,    // max request size: 40MB
            0            // file size threshold: write to disk immediately
        ));
    }
}