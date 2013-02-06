package com.cn.jd.mars.request.cluster.loadbalance;


/**
 * 负载均衡策略的工厂类
 * 目前只支持最常用的roundrobin(轮循)策略
 * @author netcomm(baiwenzhi@360buy.com)
 * @date 2013-2-5
 */
public class LoadBalanceFactory {
	private static LoadBalanceFactory service = new LoadBalanceFactory();
	
	private LoadBalanceFactory()
	{
		
	}
	
	public static LoadBalanceFactory getInstance()
	{
		return service;
	}
	
	public AbstractLoadBalance generateOneLoadBalance(String typeParm)
	{
		AbstractLoadBalance retLB = null;
		if ("roundrobin".equals(typeParm))
		{
			retLB = new RoundRobinLB();
		}
		
		return retLB;
	}
}
