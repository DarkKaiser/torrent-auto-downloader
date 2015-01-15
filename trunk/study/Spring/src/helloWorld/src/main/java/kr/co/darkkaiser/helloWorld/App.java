package kr.co.darkkaiser.helloWorld;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;

public class App {
    public static void main( String[] args ) {
    	// BeanFactory를 이용한 방법
//    	BeanFactory factory = new XmlBeanFactory(new FileSystemResource("src/main/resources/hello.xml"));
//    	GreetingService greetingService = (GreetingService)factory.getBean("greetingService");

    	// ApplicationContext를 이용한 방법
    	ApplicationContext context = new ClassPathXmlApplicationContext("hello.xml");
    	GreetingService greetingService = (GreetingService)context.getBean("greetingService");
    	
    	greetingService.sayGreeting();
    }
}
