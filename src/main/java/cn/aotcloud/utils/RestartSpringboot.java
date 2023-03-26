package cn.aotcloud.utils;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class RestartSpringboot {

	public static String[] args;
	
	public static ConfigurableApplicationContext context;
	
	public static void setContext(ConfigurableApplicationContext context) {
		RestartSpringboot.context = context;
	}
	
	public static void setArgs(String[] args) {
		RestartSpringboot.args = args != null ? Arrays.copyOf(args, args.length) : null;
	}

	public static String restart(Class<?> clz) {
		ExecutorService threadPool = new ThreadPoolExecutor(1,1,0, TimeUnit.SECONDS,new ArrayBlockingQueue<> (1),new ThreadPoolExecutor.DiscardOldestPolicy());
        threadPool.execute (()->{
        	RestartSpringboot.context.close ();
        	if(RestartSpringboot.args != null) {
        		RestartSpringboot.context = SpringApplication.run(clz, RestartSpringboot.args);
        	} else {
        		RestartSpringboot.context = SpringApplication.run(clz);
        	}
        } );
        threadPool.shutdown ();
        return "Sending signal completed at " + new Date().toString();
	}
}
