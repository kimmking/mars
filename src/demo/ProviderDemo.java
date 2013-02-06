package demo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ProviderDemo {
	public static void main(String[] args)
	{
		// 注意:请确认启动zookeeper
		ApplicationContext ctx = new ClassPathXmlApplicationContext("file:src/demo/provider_spring.xml");
	}
}
