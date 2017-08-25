package com.dabakovich.service.utils;

import com.dabakovich.entity.Passage;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.env.Environment;

import java.io.IOException;

/**
 * Created by dabak on 16.08.2017, 16:15.
 */
public class PassageDeserializer extends JsonDeserializer<Passage> {

//    @Autowired
    private Environment env;

    @Override
    public Passage deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String book = node.get("book").asText();
        String verses = node.get("verses").asText();


        return null;
    }

    public static void main(String[] args) {
        String json = "[{\"book\":\"Буття 4, 5\",\"verses\":\"Буття 4, 5\"},{\"book\":\"1 Хронік 1:1-4\",\"verses\":\"1 Хронік 1:1-4\"},{\"book\":\"Буття 6\",\"verses\":\"Буття 6\"}]";

//        JsonParserFactory jpf = Json.createParserFactory();
//        JsonParser jp = new JsonParser();
    }
}
