** 原始
dependencies {
implementation("org.javassist:javassist:3.28.0-GA")
implementation('commons-lang:commons-lang:2.6')
implementation("org.grails:gorm-hibernate5-spring-boot:${gormVersion}")
implementation("org.grails:grails-datastore-core:${gormVersion}")
implementation("org.grails:grails-datastore-gorm:${gormVersion}")
implementation("org.grails:grails-datastore-gorm-hibernate5:${gormVersion}")
implementation("org.hibernate:hibernate-ehcache:${hibernate5Version}")
implementation("org.springframework.boot:spring-boot-starter-tomcat:${springBootVersion}")
implementation("org.apache.tomcat.embed:tomcat-embed-jasper:${tomcatVersion}")

	implementation('org.yunchen.gb:gb-core:1.2.1.4')
//	implementation('org.yunchen.gb:gb-plugin-auditlog:1.2.0')
implementation('org.yunchen.gb:gb-plugin-poi:1.2.0')
implementation('org.yunchen.gb:gb-plugin-simplecaptcha:1.2.0')
implementation('org.yunchen.gb:gb-plugin-springsecurity:1.2.1')
implementation('org.yunchen.gb:gb-plugin-springsecurity-captcha:1.2.1')
implementation('org.yunchen.gb:gb-plugin-springsecurity-rest:1.2.1')
implementation('org.yunchen.gb:gb-plugin-springsecurity-rest:1.2.1')

    implementation("org.springframework.security:spring-security-core:${springSecurityVersion}")
    implementation("org.springframework.boot:spring-boot-starter-jdbc:${springBootVersion}")
    implementation("org.springframework.boot:spring-boot-starter-web:${springBootVersion}") {
        exclude module: "spring-boot-starter-tomcat"
        exclude module: "spring-boot-starter-logging"
        exclude module: "logback-classic"
    }
//    //thymeleaf
//	implementation("org.springframework.boot:spring-boot-starter-thymeleaf:${springBootVersion}")
//	implementation("org.thymeleaf:thymeleaf-spring5:3.0.15.RELEASE")
//	implementation("org.thymeleaf:thymeleaf:3.0.15.RELEASE")
//	implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.0.0")
//	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5:3.0.4.RELEASE")
//    //end thymeleaf
runtimeOnly("com.h2database:h2:1.4.192")
implementation("ch.qos.logback:logback-classic:1.2.3")
//implementation("com.oracle:ojdbc6:12.1.0.2")
//implementation("org.springframework.boot:spring-boot-autoconfigure:${springBootVersion}")
implementation ("org.codehaus.groovy:groovy:${groovyVersion}")
implementation ("org.codehaus.groovy:groovy-templates:${groovyVersion}")
implementation ("org.codehaus.groovy:groovy-sql:${groovyVersion}")
implementation ("org.codehaus.groovy:groovy-datetime:${groovyVersion}")
implementation ("org.codehaus.groovy:groovy-dateutil:${groovyVersion}")
//	testImplementation("org.springframework.boot:spring-boot-starter-test:${springBootVersion}")
//    testImplementation("org.springframework.boot:spring-boot-test-autoconfigure:${springBootVersion}")
//    testImplementation("org.springframework.security:spring-security-test:${springSecurityVersion}")
//		//views
//		implementation("org.webjars.bower:bootstrap:4.3.1")
//		implementation("org.webjars.bower:bootstrap:3.4.1")
//		implementation("org.webjars.bower:bootstrap-fileinput:4.3.9")
//		implementation("org.webjars.bower:bootstrap-table:1.15.4")
//		implementation ('org.webjars.bower:startbootstrap-sb-admin-2:4.0.7'){
//			exclude module: "fortawesome__fontawesome-free"
//			exclude module: "jquery.easing"
//			exclude module: "datatables.net"
//			exclude module: "datatables.net-bs4"
//			exclude module: "chart.js"
//			exclude module: "jquery"
//		}
//
//		implementation("org.webjars:jquery:1.12.4")
//		implementation("org.webjars:jquery-validation:1.19.0")
//		implementation("org.webjars:bootstrap-datepicker:1.9.0")
//		implementation("org.webjars:bootstrap-select:1.13.11")
//		implementation('org.webjars:popper.js:1.15.0')
//		implementation("org.webjars:zTree:3.5.28")
//
//		implementation ('org.webjars.npm:startbootstrap-coming-soon:5.0.6'){
//			exclude module: "fortawesome__fontawesome-free"
//			exclude module: "bootstrap"
//			exclude module: "jquery"
//		}
}
