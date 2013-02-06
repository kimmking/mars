package com.cn.jd.mars.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import com.cn.jd.mars.registry.RegistryFactory;

public class RegistryBean implements InitializingBean, ApplicationContextAware, ApplicationListener, BeanNameAware{
	private String id;
	private String protocol;
	private String address;
	
	public RegistryBean()
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
		RegistryFactory.getInstance().initRegistry(protocol, address);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String addressParm) {
		address = addressParm;
	}
}
