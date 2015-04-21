package com.cron_manager.manager;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by honcheng on 2015/4/21.
 */
@Component
public class SpringContextDelegate implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextDelegate.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> type){
        if(applicationContext == null){
            throw new RuntimeException("ApplicationContext is initialized yet!");
        }
        return applicationContext.getBean(type);
    }

    public static <T> T getBean(Class<T> type, String name){
        if(applicationContext == null){
            throw new RuntimeException("ApplicationContext is initialized yet!");
        }
        return applicationContext.getBean(name, type);
    }
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name){
        if(applicationContext == null){
            throw new RuntimeException("ApplicationContext is initialized yet!");
        }
        return (T)applicationContext.getBean(name);
    }
}
