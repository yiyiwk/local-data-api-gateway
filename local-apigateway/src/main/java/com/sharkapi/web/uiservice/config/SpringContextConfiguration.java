package com.sharkapi.web.uiservice.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextConfiguration implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public SpringContextConfiguration() {
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextConfiguration.applicationContext = applicationContext;
    }

    public static Object getBean(String beanName) {
        try {
            return applicationContext.getBean(beanName);
        } catch (Exception var2) {
            return null;
        }
    }

    public static <T> T getBean(Class<T> beanClass) {
        try {
            return applicationContext.getBean(beanClass);
        } catch (Exception var2) {
            return null;
        }
    }

    public static <T> T getBean(String beanName, Class<T> beanClass) {
        try {
            return applicationContext.getBean(beanName, beanClass);
        } catch (Exception var3) {
            return null;
        }
    }
}
