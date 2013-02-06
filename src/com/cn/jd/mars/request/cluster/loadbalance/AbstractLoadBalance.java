package com.cn.jd.mars.request.cluster.loadbalance;

import java.util.ArrayList;

import com.cn.jd.mars.transport.ClientTransPort;


public abstract class AbstractLoadBalance implements LoadBalance
{
	protected ArrayList<ClientTransPort> clientTransPortList;
	
	public AbstractLoadBalance()
	{
		
	}
	
	public AbstractLoadBalance(ArrayList<ClientTransPort> clientTransPortListParm)
	{
		clientTransPortList = clientTransPortListParm;
	}
	
	public void setClientTransPortList(
			ArrayList<ClientTransPort> clientTransPortListParm)
	{
		clientTransPortList = clientTransPortListParm;
	}
}
