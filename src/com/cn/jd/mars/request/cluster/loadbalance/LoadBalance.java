package com.cn.jd.mars.request.cluster.loadbalance;

import com.cn.jd.mars.transport.ClientTransPort;

public interface LoadBalance {
	/**
	 * 按照不同负载均衡算法选择一个合适的服务提供者
	 * @return
	 */
	public ClientTransPort select();
}
