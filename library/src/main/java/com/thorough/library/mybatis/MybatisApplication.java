//package com.thorough.core.mybatis;
//
//import com.thorough.core.mybatis.dao.Image;
//import com.thorough.core.mybatis.dao.ImageDao;
//import com.thorough.core.mybatis.dao.MyabtisTestDao;
//import com.thorough.core.system.model.dao.UserDao;
//import com.thorough.core.system.model.entity.User;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.context.annotation.ComponentScan;
//
//import java.util.List;
//
//@SpringBootApplication
//@ComponentScan(basePackages = "com.thorough")
//@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
//public class MybatisApplication {
//
//	public static void main(String[] args) {
//		ConfigurableApplicationContext context = SpringApplication.run(MybatisApplication.class, args);
//		ImageDao imageDao = context.getBean(ImageDao.class);
//		Image image = imageDao.selectByPrimaryKey("1");
//		MyabtisTestDao testDao = context.getBean(MyabtisTestDao.class);
//		List mybatisTest = testDao.get();
//		String s = (String) context.getBean("stringTest");
//		UserDao userDao = context.getBean(UserDao.class);
//		User user = userDao.get("1");
//		System.out.println(s.toString());
//	}
//}
