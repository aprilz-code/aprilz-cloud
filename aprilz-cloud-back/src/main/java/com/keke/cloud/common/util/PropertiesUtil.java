package com.keke.cloud.common.util;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

/**
 * 获取properties文件中的value的工具类
 */
@Component
public class PropertiesUtil implements EmbeddedValueResolverAware {
    private StringValueResolver stringValueResolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        stringValueResolver = resolver;
    }
    public String getPropertiesValue(String name){
        return stringValueResolver.resolveStringValue(name);
    }

}
