package org.innov8.tcb.common.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware
{

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> beanClazz)
    {
        return context.getBean(beanClazz);
    }

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    public static <T> T getBean(Class<T> beanClazz, Object... args) {
        return context.getBean(beanClazz, args);
    }

    public static Object getBean(String beanName, Object... args) {
        return context.getBean(beanName, args);
    }

}
