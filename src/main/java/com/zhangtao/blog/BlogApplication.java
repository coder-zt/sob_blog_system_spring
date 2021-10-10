package com.zhangtao.blog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhangtao.blog.utils.HibernateProxyTypeAdapter;
import com.zhangtao.blog.utils.IdWorker;
import com.zhangtao.blog.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Random;

@Slf4j
@SpringBootApplication
@EnableSwagger2
public class BlogApplication {

    public static void main(String[] args) {
        log.info("开始....");
        SpringApplication.run(BlogApplication.class, args);
    }

    @Bean
    public IdWorker createIdWorker(){
        return new IdWorker(0, 0);
    }

    @Bean
    public BCryptPasswordEncoder createPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RedisUtils createRedisUtils(){
        return new RedisUtils();
    }

    @Bean
    public Random createRandom(){
        return new Random();
    }

    @Bean
    public Gson createGson(){
        return new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY).create();
    }
}
