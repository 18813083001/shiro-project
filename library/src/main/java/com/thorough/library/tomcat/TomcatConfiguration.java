package com.thorough.library.tomcat;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;

@Configuration
public class TomcatConfiguration {

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.setUriEncoding(Charset.forName("UTF-8"));
        tomcat.addConnectorCustomizers(getCustomizer());
        return tomcat;
    }

    private TomcatConnectorCustomizer getCustomizer(){
        TomcatConnectorCustomizer connectorCustomizer = new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                connector.setMaxPostSize(8388608);
            }
        };
        return connectorCustomizer;
    }

}
