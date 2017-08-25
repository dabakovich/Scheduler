package com.dabakovich.service.utils;

import com.dabakovich.entity.Passage;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by dabak on 16.08.2017, 16:13.
 */
public class PassageSerializer extends JsonSerializer<Passage> {

    @Override
    public void serialize(Passage passage, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

    }
}
