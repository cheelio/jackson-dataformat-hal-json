package com.tensorwrench.jackson.hal.serializer;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.tensorwrench.jackson.hal.util.HalUtils;

import java.io.IOException;

public class EmbeddedPropertyWriter extends BaseHalPropertyWriter {
    public EmbeddedPropertyWriter() {
        super("_embedded");
        assignNullSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

            }
        });
    }

    @Override
    public void serializeAsField(Object obj, JsonGenerator jgen, SerializerProvider prov) throws Exception {
        if (properties.size() > 0 && HalUtils.hasValues(properties, obj)) {
            jgen.writeObjectFieldStart(getName());
            for (BeanPropertyWriter p : properties) {
                Object propertyValue = p.get(obj);
                if (HalUtils.shouldWriteProperty(propertyValue)) {
                    jgen.writeFieldName(p.getName());
                    JsonSerializer<Object> serializer = prov.findValueSerializer(p.getType(), p);
                    if (serializer != null) {
                        serializer.serialize(propertyValue, jgen, prov);
                    } else {
                        jgen.writeObject(p.get(obj));
                    }
                }
            }
            jgen.writeEndObject();
        }
    }
}
