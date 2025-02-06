package org.example.netty.config;

import org.example.netty.Entity.MySerializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    static Properties properties;
    static {
        try(InputStream in = Config.class.getResourceAsStream("/application.properties")){

            properties = new Properties();
            properties.load(in);

        } catch (IOException e){

            throw new ExceptionInInitializerError(e);
        }
    }
    public static MySerializer.Algorith getMySerializerAlgorithm(){
        final String value  = properties.getProperty("mySerializer.algorithm");
        if(value == null){
            return MySerializer.Algorith.Json;
        }
        else {
            return MySerializer.Algorith.valueOf(value);
        }
    }
}
