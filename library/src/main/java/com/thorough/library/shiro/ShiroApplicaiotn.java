//package com.thorough.core.shiro;
//
//
//import com.thorough.core.listener.ThoroughApplicationListener;
//import com.thorough.core.shiro.filter.UsernamePasswordToken;
//import com.thorough.core.system.model.dao.UserDao;
//import com.thorough.core.utils.SpringContextHolder;
//import com.thorough.core.system.model.entity.User;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.authc.AuthenticationException;
//import org.apache.shiro.mgt.SecurityManager;
//import org.apache.shiro.subject.Subject;
////import org.junit.Assert;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
//import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//
//@SpringBootApplication
//@ComponentScan({"com.thorough"})
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
//public class ShiroApplicaiotn {
//    public static void main(String[] args) {
//        ConfigurableApplicationContext context = new SpringApplicationBuilder(ShiroApplicaiotn.class).
//                initializers(new ThoroughApplicationListener()).
//                run(args);
////        RedisUtils.set("cls","123");
//        UserDao userDao = context.getBean(UserDao.class);
//        User user = userDao.get("1");
//        SecurityManager securityManager = context.getBean(SecurityManager.class);
//        SecurityUtils.setSecurityManager(securityManager);
//        Subject subject = SecurityUtils.getSubject();
//        UsernamePasswordToken token = new UsernamePasswordToken();
//        token.setUsername("songzhigang");
//        token.setPassword("thoroughsong".toCharArray());
//        token.setDevice("ios");
//        try {
//            subject.login(token);
//        } catch (AuthenticationException e) {
//            //5、身份验证失败
//        }
////        Assert.assertEquals(true, subject.isAuthenticated()); //断言用户已经登录
//        //6、退出
//        subject.logout();
//        System.out.println("hello");
//    }
//
//    @Bean
//    public SpringContextHolder springContextHolder(){
//        return new SpringContextHolder();
//    }
//}
