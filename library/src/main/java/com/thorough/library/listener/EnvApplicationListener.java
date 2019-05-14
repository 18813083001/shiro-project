package com.thorough.library.listener;


import com.thorough.library.utils.PropertyUtil;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

public class EnvApplicationListener implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public EnvApplicationListener(){
        super();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        initGlobal(applicationContext.getEnvironment());
    }

    private void initGlobal(Environment environment){
        PropertyUtil.setEnvironment(environment);
    }
}
