package com.evolve.codec;

import java.io.IOException;

import com.evolve.model.Usr;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class UserSerializer extends JsonSerializer<Usr> {
    @Override
    public void serialize(Usr user, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("email", user.getEmail()); // Rename "username" to "email"
        gen.writeStringField("name", user.getName());
        System.out.println("Values Serialized");
        gen.writeEndObject();
    }
}

