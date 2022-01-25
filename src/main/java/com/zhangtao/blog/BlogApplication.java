package com.zhangtao.blog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhangtao.blog.utils.HibernateProxyTypeAdapter;
import com.zhangtao.blog.utils.IdWorker;
import com.zhangtao.blog.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.MultipartConfigElement;
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
    public IdWorker createIdWorker() {
        return new IdWorker(0, 0);
    }

    @Bean
    public BCryptPasswordEncoder createPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RedisUtils createRedisUtils() {
        return new RedisUtils();
    }

    @Bean
    public Random createRandom() {
        return new Random();
    }

    @Bean
    public Gson createGson() {
        return new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY).create();
    }

    /**
     * 配置上传文件大小的配置
     *
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //  设置单个文件大小
        factory.setMaxFileSize("102400KB");//KB 或者 MB 都可以 1MB=1024KB。1KB=1024B(字节)
        /// 设置总上传文件大小
        factory.setMaxRequestSize("102400KB");//KB 或者 MB 都可以 1MB=1024KB。1KB=1024B(字节)
        return factory.createMultipartConfig();
    }


}
