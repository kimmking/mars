package com.cn.jd.mars.request.cluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.cn.jd.mars.request.Request;
import com.cn.jd.mars.request.cluster.loadbalance.AbstractLoadBalance;
import com.cn.jd.mars.request.cluster.loadbalance.RoundRobinLB;
import com.cn.jd.mars.transport.ClientTransPort;
import com.cn.jd.mars.transport.TransportFactory;


/**
 * 集群策略的基类
 * 
 * @author netcomm(baiwenzhi@360buy.com)
 * @date 2013-2-5
 */
public abstract class AbstrctClusterClientTransPort {
	protected ArrayList<ClientTransPort> clientTransPortList = new ArrayList();
	protected AbstractLoadBalance theLoadBalance;
	
	public AbstrctClusterClientTransPort(AbstractLoadBalance theLoadBalanceParm)
	{
		theLoadBalance = theLoadBalanceParm;
		if (theLoadBalance == null)
		{
			theLoadBalance = new RoundRobinLB(clientTransPortList);
		}
		
		theLoadBalance.setClientTransPortList(clientTransPortList);
	}
	
	public void addOneClientTransPort(ClientTransPort theClientTransPortParm)
	{
		synchronized(clientTransPortList)
		{
			clientTransPortList.add(theClientTransPortParm);
		}
	}
	
	public void removeOneClientTransPort(ClientTransPort theClientTransPortParm)
	{
		synchronized(clientTransPortList)
		{
			String tmpKey = theClientTransPortParm.getKey();
			int tmpListSz = clientTransPortList.size();
			for (int i = 0; i < tmpListSz; i++)
			{
				if (clientTransPortList.get(i).getKey().equals(tmpKey))
				{
					clientTransPortList.remove(i);
					break;
				}
			}
		}
	}
	
	public void deleteClientTransPort(ArrayList<String[]> existTransPortListParm)
	{
		ArrayList<ClientTransPort> tmpWantToDeleteList = new ArrayList();
		
		int tmpListSz = clientTransPortList.size();
		ClientTransPort tmpClientTransPort = null;
		for (int i = 0; i < tmpListSz; i++)
		{
			boolean tmpIsWantToDelete = true;
			tmpClientTransPort = clientTransPortList.get(i);
			for (int j = 0; j < existTransPortListParm.size(); j++)
			{
				String tmpType = existTransPortListParm.get(j)[0];
				String tmpkey = existTransPortListParm.get(j)[1];
				if (tmpClientTransPort.getType().equals(tmpType))
				{
					if (tmpClientTransPort.getKey().equals(tmpkey))
					{
						tmpIsWantToDelete = false;
						break;
					}
				}
			}
			
			if (tmpIsWantToDelete == true)
			{
				tmpWantToDeleteList.add(tmpClientTransPort);
			}
		}
		
		for (int i = 0; i < tmpWantToDeleteList.size(); i++)
		{
			tmpClientTransPort = tmpWantToDeleteList.get(i);
			try
			{
				tmpClientTransPort.stop();
				removeOneClientTransPort(tmpClientTransPort);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	public void addOneClientTransPort(String typeParm, String keyParm, List parms,
			int connectionsParm, HashMap parmHMapParm)
	{
		boolean tmpCanAdd = true;
		int tmpListSz = clientTransPortList.size();
		ClientTransPort tmpClientTransPort = null;
		for (int i = 0; i < tmpListSz; i++)
		{
			tmpClientTransPort = clientTransPortList.get(i);
			if (tmpClientTransPort.getType().equals(typeParm))
			{
				if (tmpClientTransPort.getKey().equals(keyParm))
				{
					tmpCanAdd = false;
					break;
				}
			}
		}
		
		if (tmpCanAdd == true)
		{
			System.out.println("从registry获得一个服务提供者, 实例化链接数 " + connectionsParm);
			for (int i = 0; i < connectionsParm; i++)
			{
				tmpClientTransPort = TransportFactory.getInstance()
					.initClientTransportFromRegistry(typeParm, parms, parmHMapParm);
				addOneClientTransPort(tmpClientTransPort);
			}
		}
	}
	
	public abstract Object sendRequest(Request reqParm, Class rtParm) throws IOException;
}
