package com.tensorwrench.jackson.hal.serializer;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.tensorwrench.jackson.hal.HalLinkCreator;
import com.tensorwrench.jackson.hal.util.HalUtils;

import java.util.ArrayList;
import java.util.List;

public class HalSerializerModifier extends BeanSerializerModifier {
    HalLinkCreator linkCreator;

    public HalSerializerModifier(HalLinkCreator linkCreator) {
        this.linkCreator = linkCreator;
    }

    @Override
    public BeanSerializerBuilder updateBuilder(final SerializationConfig config, final BeanDescription beanDesc, final BeanSerializerBuilder builder) {
        final LinkPropertyWriter links = new LinkPropertyWriter(linkCreator);
        final EmbeddedPropertyWriter embedded = new EmbeddedPropertyWriter();
        final List<BeanPropertyWriter> remove = new ArrayList<BeanPropertyWriter>();

        // pull out all of the HAL _embedded or _links elements
        for (final BeanPropertyWriter p : builder.getProperties()) {
            if (HalUtils.isHalLink(p)) {
                links.addProp(p);
                remove.add(p);
            } else if (HalUtils.isHalEmbedded(p)){
                embedded.addProp(p);
                remove.add(p);
            }
        }

        // remove all the props we've used
        for (final BeanPropertyWriter p : remove) {
            builder.getProperties().remove(p);
        }

        if (embedded.hasProperties()){
            builder.getProperties().add(0, embedded);
        }

        if (links.hasProperties()){
            builder.getProperties().add(0, links);
        }

        return builder;
    }

}
