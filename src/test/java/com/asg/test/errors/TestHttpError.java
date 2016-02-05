package com.asg.test.errors;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;

public class TestHttpError {

    private ObjectMapper mapper;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Test
    public void testSerializationDeserialization() throws IOException {
        ErrorDetail detail = new AdapterUnauthorizedErrorDetail("Unauthorized adapter access", "vdr", "Basic");
        HttpError error = new HttpError(401, "Unauthorized", detail);
        String jsonError = mapper.writeValueAsString(error);
        System.out.println(jsonError);
        Assert.assertTrue("Serialization should be successful", jsonError.contains("Unauthorized") && jsonError.contains("Basic"));
        HttpError readError = mapper.readValue(jsonError, HttpError.class);
        Assert.assertEquals("Deserialization of the HttpError should be successful",
                "Unauthorized", readError.getMessage());
        Assert.assertEquals("Deserialization of the AdapterUnauthorizedErrorDetail should be successful",
                "Basic", ((AdapterUnauthorizedErrorDetail)readError.getDetails().get(0)).getAuthorization());
    }

    @Test
    public void testBase64() {
        String value = new BASE64Encoder().encode("vdr:vdr".getBytes());
        System.out.println(value);
    }

}
