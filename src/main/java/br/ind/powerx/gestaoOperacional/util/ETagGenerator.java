package br.ind.powerx.gestaoOperacional.util;

import org.apache.commons.codec.digest.DigestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ETagGenerator {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) 
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); 

    public static String generateETag(Object object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            String md5Hex = DigestUtils.md5Hex(json);
            return "\"" + md5Hex + "\"";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate ETag", e);
        }
    }
}

