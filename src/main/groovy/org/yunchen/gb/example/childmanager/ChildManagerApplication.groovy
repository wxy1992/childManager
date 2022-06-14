package org.yunchen.gb.example.childmanager

import org.apache.catalina.Context
import org.apache.tomcat.util.scan.StandardJarScanner
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.boot.SpringApplication
import org.springframework.context.annotation.Configuration
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.web.WebApplicationInitializer

@ComponentScan(basePackages = ["org.yunchen.gb.example.childmanager", "org.yunchen.gb"])
@SpringBootApplication
@Configuration
class ChildManagerApplication extends SpringBootServletInitializer implements WebApplicationInitializer {

    static void main(String[] args) {
        SpringApplication.run ChildManagerApplication, args
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ChildManagerApplication.class);
    }
    //解决启动时,抛出FileNotFoundException异常的问题
    //此问题是因为tomcat解析jar中MANIFEST.MF文件,判断文件中的class-path属性，并尝试加载其中的jar包
    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                ((StandardJarScanner) context.getJarScanner()).setScanManifest(false);
            }
        };
    }
}
