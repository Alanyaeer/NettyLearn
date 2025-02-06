package org.example.netty.Entity;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

public interface MySerializer {
    <T> T deserializer(Class<T> clazz, byte[] bytes);

    <T> byte[] serializer(T obj);
    public enum Algorith implements  MySerializer{
        Java{
            @Override
            public <T> T deserializer(Class<T> clazz, byte[] bytes) {
                return null;
            }

            @Override
            public <T> byte[] serializer(T obj) {
                return null;
            }
        },
        Json{
            @Override
            public <T> T deserializer(Class<T> clazz, byte[] bytes) {
                Gson gson = new Gson();
                String json = new String(bytes, StandardCharsets.UTF_8);
                return gson.fromJson(json, clazz);
            }

            @Override
            public <T> byte[] serializer(T obj) {
                Gson gson = new Gson();
                String json = gson.toJson(obj);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        }
    }
}
