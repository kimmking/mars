package com.cn.jd.mars.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import com.cn.jd.mars.serviceexporter.MarsServiceExportService;

public class ServiceExporterBean implements InitializingBean, ApplicationContextAware, ApplicationListener, BeanNameAware{
	private String id;
	private String serviceInterface;
	private Object ref;
	private String transporServer;
	
	public ServiceExporterBean()
	{
		
	}
	
	@Override
	public void setBeanName(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onApplicationEvent(ApplicationEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		MarsServiceExportService.getInstance().registryService(serviceInterface, ref,
				transporServer);
	}

	public String getInterface() {
		return serviceInterface;
	}

	public void setInterface(String serviceInterface) {
		this.serviceInterface = serviceInterface;
	}
	
	public Object getRef() {
		return ref;
	}

	public void setRef(Object ref) {
		this.ref = ref;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTransporServer() {
		return transporServer;
	}

	public void setTransporServer(String transporServer) {
		this.transporServer = transporServer;
	}
}
