//package com.thorough.redis;
//
//import com.thorough.listener.ThoroughApplicationListener;
//import com.thorough.redis.utils.RedisUtils;
//import com.thorough.utils.SpringContextHolder;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//
//@SpringBootApplication
//@ComponentScan(basePackages = "com.thorough.redis")
//@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
//public class CacheApplication {
//    public static void main(String[] args) {
//        ConfigurableApplicationContext context = new SpringApplicationBuilder(CacheApplication.class).
//                initializers(new ThoroughApplicationListener()).
//                run(args);
//        RedisUtils.set("cls","123");
//        System.out.println(new String(RedisUtils.get("cls")).toString());
//    }
//
//    @Bean
//    public SpringContextHolder springContextHolder(){
//        return new SpringContextHolder();
//    }
//}
