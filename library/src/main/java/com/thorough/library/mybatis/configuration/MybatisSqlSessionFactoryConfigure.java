package com.thorough.library.mybatis.configuration;

import com.thorough.library.mybatis.persistence.model.entity.Entity;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
public class MybatisSqlSessionFactoryConfigure {

    @Autowired
    DataSource dataSource;

    @Bean(name="sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean(){
        SqlSessionFactoryBean factorybean = new DynamicMapperSqlSessionBean();
        factorybean.setDataSource(dataSource);
        factorybean.setTypeAliasesPackage("com.thorough");
        factorybean.setTypeAliasesSuperType(Entity.class);
        //添加XML目录
        ResourcePatternResolver configResolver = new PathMatchingResourcePatternResolver();
        ResourcePatternResolver mapperResolver = new PathMatchingResourcePatternResolver();
        try {
            factorybean.setConfigLocation(configResolver.getResource("classpath:/mybatis-config.xml"));
            factorybean.setMapperLocations(mapperResolver.getResources("classpath*:/mappings/modules/*/*.xml"));
            return factorybean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
