package com.dabakovich.service.bpp;

import com.dabakovich.service.annotation.Profiling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dabak on 15.08.2017, 13:56.
 */
@Component
public class ProfilingBeanPostProcessor implements BeanPostProcessor {

    private Logger logger = LoggerFactory.getLogger("Profiling");

    private Map<String, Class> beanClasses = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class beanClass = bean.getClass();
        Method[] methods = beanClass.getMethods();
        for (Method method : methods) {
            if (method.getAnnotation(Profiling.class) != null) {
                logger.info("Found bean " + beanName + " with annotation @Profiling");
                beanClasses.put(beanName, beanClass);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Object newBean = bean;
        Class beanClass = beanClasses.get(beanName);
        if (beanClass != null) {
            newBean = Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), (proxy, method, args) -> {
                if (bean.getClass().getMethod(method.getName(), method.getParameterTypes()).getAnnotation(Profiling.class) != null) {
                    System.currentTimeMillis();
                    long timeBefore = System.currentTimeMillis();
                    Object result = method.invoke(bean, args);
                    long timeAfter = System.currentTimeMillis();
                    long difference = timeAfter - timeBefore;
                    logger.info("Time for executing method '" + method.getName() + "' is " + difference + " ms");
                    return result;
                } else return method.invoke(bean, args);
            });
        }
        return newBean;
    }
}
