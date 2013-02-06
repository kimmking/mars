package com.cn.jd.mars.request.cluster;

import java.util.ArrayList;
import java.util.HashMap;

import com.cn.jd.mars.request.cluster.loadbalance.AbstractLoadBalance;
import com.cn.netcomm.communication.util.Utilities;


/**
 * 集群策略的工厂类
 * 
 * @author netcomm(baiwenzhi@360buy.com)
 * @date 2013-2-5
 */
public class ClusterClientTransPortFactory {
	public static ClusterClientTransPortFactory service = new ClusterClientTransPortFactory();
	private HashMap serviceNameToClusterClientTransPortHMap = new HashMap();
	
	private ClusterClientTransPortFactory()
	{
		
	}
	
	public static ClusterClientTransPortFactory getInstance()
	{
		return service;
	}
	
	/**
	 * 创建集群策略,目前只支持failover方式
	 * @param serviceInterfaceNameParm
	 * @param typeParm
	 * @param theLoadBalanceParm
	 * @return
	 */
	public AbstrctClusterClientTransPort generateClusterClientTransPort(
			String serviceInterfaceNameParm, String typeParm,
			AbstractLoadBalance theLoadBalanceParm)
	{
		AbstrctClusterClientTransPort retClusterClientTransPort = null;
		if ("".equals(typeParm))
		{
			retClusterClientTransPort = new FailoverClusterClientTransPort(theLoadBalanceParm);
		}
		else if ("failover".equals(typeParm))
		{
			retClusterClientTransPort = new FailoverClusterClientTransPort(theLoadBalanceParm);
		}
		
		ArrayList tmpList = (ArrayList)serviceNameToClusterClientTransPortHMap
			.get(serviceInterfaceNameParm);
		if (tmpList == null)
		{
			tmpList = new ArrayList();
			serviceNameToClusterClientTransPortHMap.put(serviceInterfaceNameParm, tmpList);
		}
		
		tmpList.add(retClusterClientTransPort);
		return retClusterClientTransPort;
	}
	
	public void addOneClusterItem(String serviceInterfaceNameParm,
			int connectionsParm, String formatStrParm, HashMap parmHMapParm)
	{
		ArrayList<String> tmpStrs = Utilities.parseFormatStr(formatStrParm);
		ArrayList<AbstrctClusterClientTransPort> tmpList = (ArrayList)serviceNameToClusterClientTransPortHMap
				.get(serviceInterfaceNameParm);
		String tmpType = tmpStrs.get(0);
		String tmpkey = "";
		for (int i = 1; i < tmpStrs.size(); i++)
		{
			if (i == 1)
			{
				tmpkey = tmpStrs.get(i);
			}
			else
			{
				tmpkey = tmpkey +":"+ tmpStrs.get(i);
			}
		}
		tmpStrs.remove(0);
		
		int tmpListSz = tmpList.size();
		AbstrctClusterClientTransPort tmpClusterClientTransPort = null;
		for (int i = 0; i < tmpListSz; i++)
		{
			tmpClusterClientTransPort = tmpList.get(i);
			tmpClusterClientTransPort.addOneClientTransPort(tmpType, tmpkey,
					tmpStrs, connectionsParm, parmHMapParm);
		}
	}
	
	public void adjustClusterItems(
		String serviceInterfaceNameParm, ArrayList<String> formatStrListParm)
	{
		ArrayList<String[]> tmpExistTransPortList = new ArrayList();
		for (int i = 0; i < formatStrListParm.size(); i++)
		{
			ArrayList<String> tmpStrs =
				Utilities.parseFormatStr(formatStrListParm.get(i));
			
			String[] tmpOneItemStrs = new String[2];
			String tmpType = tmpStrs.get(0);
			String tmpkey = "";
			for (int j = 1; j < tmpStrs.size(); j++)
			{
				if (j == 1)
				{
					tmpkey = tmpStrs.get(j);
				}
				else
				{
					tmpkey = tmpkey +":"+ tmpStrs.get(j);
				}
			}
			tmpOneItemStrs[0] = tmpType;
			tmpOneItemStrs[1] = tmpkey;
			
			tmpExistTransPortList.add(tmpOneItemStrs);
		}
		
		ArrayList<AbstrctClusterClientTransPort> tmpList = (ArrayList)serviceNameToClusterClientTransPortHMap
				.get(serviceInterfaceNameParm);
		AbstrctClusterClientTransPort tmpAbstrctClusterClientTransPort = null;
		for (int i = 0; i < tmpList.size(); i++)
		{
			tmpAbstrctClusterClientTransPort = tmpList.get(i);
			tmpAbstrctClusterClientTransPort.deleteClientTransPort(tmpExistTransPortList);
		}
	}
}
