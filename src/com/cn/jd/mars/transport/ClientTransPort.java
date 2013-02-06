package com.cn.jd.mars.transport;

import java.io.IOException;

import com.cn.jd.mars.request.Request;


/**
 * 封装Client的通讯接口
 * 
 * @author netcomm(baiwenzhi@360buy.com)
 * @date 2013-2-5
 */
public interface ClientTransPort
{
	/**
	 * 启动一个通讯链路
	 */
	public void start();
	
	/**
	 * 发送一个需要响应的请求
	 * @param reqParm
	 * @param rtParm
	 * @return
	 * @throws IOException
	 */
	public Object sendRequest(Request reqParm, Class rtParm) throws IOException;
	
	/**
	 * 发送一个请求,不需要响应
	 * @param reqParm
	 * @throws IOException
	 */
	public void sendRequest(Request reqParm) throws IOException;
	public String getKey();
	public String getType();
	public void stop() throws Exception;
	
	/**
	 * 发送失败的重试次数
	 * @return
	 * @throws Exception
	 */
	public int getRetries() throws Exception;
}
