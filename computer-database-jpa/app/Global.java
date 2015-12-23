import loading.DemoDataGenerator;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import play.Application;

/**
 * 
 * @author Peter Romanoff
 *
 */
public class Global extends play.GlobalSettings{
	
	/**
	 * wow, we have a spring here
	 */
	private static ApplicationContext  ctx;
	
	@Override
	public void onStart(Application app) {
		//create app context
		ctx = new ClassPathXmlApplicationContext("application-context.xml");
		
		//create demo data in the DB
		//see the content of this class to understand how the process definition deployed
		final DemoDataGenerator demoDataGenerator = (DemoDataGenerator)ctx.getBean("demoDataGenerator");
		demoDataGenerator.init();
	}

	/**
	 * required to fetch controllers from spring (in usual play app this is not required)
	 */
	@Override
	public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
		return ctx.getBean(controllerClass);
	}
}
