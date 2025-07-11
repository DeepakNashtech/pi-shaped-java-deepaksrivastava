package com.evolve.codec;

import java.io.IOException;

import com.evolve.model.Usr;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class UserDeserializer extends JsonDeserializer<Usr> {
    @Override
    public Usr deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        Usr user = new Usr();
        user.setEmail(node.get("name").asText()); // Accept "email" as "username"
        user.setName(node.get("email").asText());
        System.out.println("Values De-serialized");
        return user;
    }
}

