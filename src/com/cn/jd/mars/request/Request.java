package com.cn.jd.mars.request;

import java.io.Serializable;

/**
 * 请求的封装类
 * 
 * @author netcomm(baiwenzhi@360buy.com)
 * @date 2013-2-5
 */
public class Request implements Serializable
{
	// 服务名(接口名)
	private String serviceInterfaceName;
	// 方法名
	private String methodName;
	// 方法调用参数
	private Object[] arguments;
	
	public Request()
	{
		
	}
	
	public Request(String serviceInterfaceNameParm, String methodNameParm,
			Object[] argumentsParm)
	{
		serviceInterfaceName = serviceInterfaceNameParm;
		methodName = methodNameParm;
		arguments = argumentsParm;
	}

	public String getServiceInterfaceName() {
		return serviceInterfaceName;
	}

	public void setServiceInterfaceName(String serviceInterfaceName) {
		this.serviceInterfaceName = serviceInterfaceName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}
}
