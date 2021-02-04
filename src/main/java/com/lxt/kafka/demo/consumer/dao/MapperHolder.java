package com.lxt.kafka.demo.consumer.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * kafka消费者客户端mapper存取
 *
 * @author lixt90
 */
@Component
@SuppressWarnings("all")
public class MapperHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static final Map<Class<?>, BaseMapper> MAPPER_MAP = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        MapperHolder.applicationContext = applicationContext;
    }

    public static BaseMapper find(Class clazz) {
        BaseMapper baseMapper = MAPPER_MAP.get(clazz);
        if (baseMapper == null) {
            baseMapper = loadMapper(clazz);
            if (baseMapper != null) {
                MAPPER_MAP.put(clazz, baseMapper);
            }
        }
        return baseMapper;
    }

    private static BaseMapper loadMapper(Class clazz) {
        return (BaseMapper) applicationContext.getBean(clazz);
    }
}
