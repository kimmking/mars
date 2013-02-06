package com.cn.jd.mars.request.cluster.loadbalance;

import java.util.ArrayList;

import com.cn.jd.mars.transport.ClientTransPort;

public class RoundRobinLB extends AbstractLoadBalance
{
	public RoundRobinLB()
	{
		
	}
	
	public RoundRobinLB(ArrayList<ClientTransPort> clientTransPortListParm)
	{
		super(clientTransPortListParm);
	}

	@Override
	/**
	 * 轮循策略的负载均衡
	 */
	public ClientTransPort select()
	{
		ClientTransPort retCT = null;
		
		synchronized(clientTransPortList)
		{
			if (clientTransPortList.size() == 1)
			{
				retCT = clientTransPortList.get(0);
			}
			else
			{
				retCT = clientTransPortList.remove(0);
				clientTransPortList.add(retCT);
			}
		}
		
		return retCT;
	}
}
