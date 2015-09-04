package com.tensorwrench.jackson.hal.resolvers;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.tensorwrench.jackson.hal.HalLinkCreator;
import com.tensorwrench.jackson.hal.util.HalUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class AnnotationPatternLinkCreator implements HalLinkCreator {


    static class LinkBuilderImpl {
        private final BeanDescription beanDescription;
        private final BeanPropertyDefinition idProp;
        private final BeanPropertyDefinition linkPrefixProp;
        private final String format;

        public LinkBuilderImpl(BeanDescription bean) throws JsonMappingException {
            beanDescription = bean;
            format = HalUtils.findFormat(bean);
            idProp = HalUtils.findIdProperty(bean);
            linkPrefixProp = HalUtils.findLinkPrefixProperty(bean);
            if (idProp != null) {
                idProp.getAccessor().fixAccess();
            }
            if (linkPrefixProp != null) {
                linkPrefixProp.getAccessor().fixAccess();
            }
        }

        public String make(Object object) {
            if (idProp != null && idProp.getAccessor().getValue(object) != null) {
                Object idValue = idProp.getAccessor().getValue(object);

                if (linkPrefixProp != null && linkPrefixProp.getAccessor().getValue(object) != null) {
                    Object linkPrefixValue = linkPrefixProp.getAccessor().getValue(object);
                    return format.replace("{id}", idValue.toString())
                            .replace("{prefix}", linkPrefixValue.toString());
                }
                return format.replace("{id}", idValue.toString())
                        .replace("{classname}", beanDescription.getBeanClass().getSimpleName())
                        .replace("{classname.lcase}", beanDescription.getBeanClass().getSimpleName().toLowerCase());
            }else if (object instanceof String){
                return (String)object;
            }
            return null;
        }

        private String make(Object val, URI uri) {
            String s = make(val);
            if (uri != null) {
                return uri.resolve("./" + s).toString();
            }
            return s;
        }
    }

    Map<Class<?>, LinkBuilderImpl> cache = new HashMap<Class<?>, LinkBuilderImpl>();

    @Override
    public String makeLink(Object val, SerializerProvider prov) throws JsonMappingException {
        synchronized (cache) {
            LinkBuilderImpl linkBuilder = cache.get(val.getClass());
            if (linkBuilder == null) {
                JavaType javaType = prov.getConfig().getTypeFactory().constructType(val.getClass());
                BeanDescription bean = prov.getConfig().getClassIntrospector().forSerialization(prov.getConfig(), javaType, null);
                cache.put(javaType.getRawClass(), linkBuilder = new LinkBuilderImpl(bean));
            }
            // see if we can get some info about the url from the filter, if it's available
            if (prov.getFilterProvider() != null) {
                UriInfoFilter infoFilter = (UriInfoFilter) prov.getFilterProvider().findFilter(UriInfoFilter.KEY);
                if (infoFilter != null)
                    return linkBuilder.make(val, infoFilter.getUri());
            }
            return linkBuilder.make(val);
        }
    }


}
