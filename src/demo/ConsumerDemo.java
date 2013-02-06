package demo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConsumerDemo {
	public static void main(String[] args)
	{
		ApplicationContext ctx = new ClassPathXmlApplicationContext("file:src/demo/consumer_spring.xml");
	}
}
