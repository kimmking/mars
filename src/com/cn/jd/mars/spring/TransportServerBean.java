package com.cn.jd.mars.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.cn.jd.mars.serviceexporter.MarsServiceExportService;
import com.cn.jd.mars.transport.TransportFactory;

public class TransportServerBean implements InitializingBean, ApplicationContextAware, ApplicationListener, BeanNameAware{
	private String id;
	private String type;
	private String ip;
	private String port;
	
	public TransportServerBean()
	{
		
	}
	
	@Override
	public void setBeanName(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		// TODO Auto-generated method stub
		if(event instanceof ContextRefreshedEvent)
		{
			TransportFactory.getInstance().initTransportServerBeans();
			MarsServiceExportService.getInstance().initRegistryServiceVect();
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		TransportFactory.getInstance().addOneTransportServerBean(this);
	}
	
	public String getType() {
		return type;
	}

	public void setType(String typeParm) {
		type = typeParm;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
}
