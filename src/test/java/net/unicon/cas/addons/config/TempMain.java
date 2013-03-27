package net.unicon.cas.addons.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Dmitriy Kopylenko
 */
public class TempMain {

	public static void main(String... args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("/applicationContext.xml");
		System.out.println();
	}

}
