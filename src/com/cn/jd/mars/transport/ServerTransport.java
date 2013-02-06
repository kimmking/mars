package com.cn.jd.mars.transport;


public interface ServerTransport {
	/**
	 * 生成放到注册中心的通讯相关配置
	 * @return
	 */
	public String generateRegStr();
}
