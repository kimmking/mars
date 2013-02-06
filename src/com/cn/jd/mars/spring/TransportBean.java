package com.cn.jd.mars.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import com.cn.jd.mars.transport.TransportFactory;

public class TransportBean implements InitializingBean, ApplicationContextAware, ApplicationListener, BeanNameAware{
	private String id;
	private String type;
	private String ip;
	private String port;
	private String timeout;
	private String retries;
	
	public TransportBean()
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
		TransportFactory.getInstance().initClientTransport(id, type, ip, port,
				Integer.parseInt(timeout), Integer.parseInt(retries));
	}
	
	public String getType() {
		return type;
	}

	public void setType(String typeParm) {
		type = typeParm;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
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

	public String getTimeout()
	{
		return timeout;
	}

	public void setTimeout(String timeout)
	{
		this.timeout = timeout;
	}

	public String getRetries()
	{
		return retries;
	}

	public void setRetries(String retries)
	{
		this.retries = retries;
	}
	
}
