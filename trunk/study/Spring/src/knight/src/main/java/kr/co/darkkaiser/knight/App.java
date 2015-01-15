package kr.co.darkkaiser.knight;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;

public class App {
    public static void main( String[] args ) throws QuestException {
    	ApplicationContext context = new ClassPathXmlApplicationContext("knight.xml");
    	KnightOfTheRoundTable knight = (KnightOfTheRoundTable)context.getBean("knight");

    	HolyGrail embarkOnQuest = knight.embarkOnQuest();
    	embarkOnQuest.say();
    }
}
