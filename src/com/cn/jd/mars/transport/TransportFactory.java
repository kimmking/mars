package com.cn.jd.mars.transport;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.cn.jd.mars.spring.ReferenceServiceBean;
import com.cn.jd.mars.spring.TransportServerBean;
import com.cn.jd.mars.transport.hessian.HessianServer;
import com.cn.jd.mars.transport.mars.MarsClientSocketTransport;
import com.cn.jd.mars.transport.mars.MarsServerSocket;

public class TransportFactory
{
	private static TransportFactory service = new TransportFactory();
	private HashMap theClientTransportHMap = new HashMap();
	private HashMap theServiceTransportHMap = new HashMap();
	public static String Transport_Type_Mars = "mars";
	public static String Transport_Type_Hessian = "hessian";
	private Vector<TransportServerBean> transportServerBeanVect = new Vector();
	
	private TransportFactory()
	{
		
	}
	
	public static TransportFactory getInstance()
	{
		return service;
	}
	
	/**
	 * 从spring配置文件的方式初始化连接(在消费端静态配置服务提供者信息)
	 * @param idParm
	 * @param typeParm
	 * @param ipParm
	 * @param portParm
	 * @param timeoutParm
	 * @param retriesParm
	 */
	public void initClientTransport(String idParm, String typeParm, String ipParm,
			String portParm, int timeoutParm, int retriesParm)
	{
		if (Transport_Type_Mars.equals(typeParm))
		{
			HashMap tmpParmHMap = new HashMap();
			tmpParmHMap.put(ReferenceServiceBean.TimeOut_ParmName, timeoutParm);
			tmpParmHMap.put(ReferenceServiceBean.Retries_ParmName, retriesParm);
			ClientTransPort tmpMarsClientTransPort =
				new MarsClientSocketTransport(ipParm, Integer.parseInt(portParm),
						tmpParmHMap);
			tmpMarsClientTransPort.start();
			theClientTransportHMap.put(idParm, tmpMarsClientTransPort);
		}
	}
	
	/**
	 * 从注册中心获得服务提供端信息,并建立连接
	 * @param typeParm
	 * @param parms
	 * @param parmHMapParm
	 * @return
	 */
	public ClientTransPort initClientTransportFromRegistry(
			String typeParm, List parms, HashMap parmHMapParm)
	{
		ClientTransPort retClientTransPort = null;
		if (Transport_Type_Mars.equals(typeParm))
		{
			retClientTransPort = new MarsClientSocketTransport(
					(String)parms.get(0), Integer.parseInt((String)parms.get(1)),
					parmHMapParm);
			
			retClientTransPort.start();
			theClientTransportHMap.put(
				"fromRegistry_"+System.nanoTime(), retClientTransPort);
		}
		
		return retClientTransPort;
	}
	
	public void addOneTransportServerBean(TransportServerBean oneTransportServerBeanParm)
	{
		transportServerBeanVect.add(oneTransportServerBeanParm);
	}
	
	public synchronized void initTransportServerBeans()
	{
		TransportServerBean tmpTransportServerBean = null;
		for (int i = 0; i < transportServerBeanVect.size(); i++)
		{
			tmpTransportServerBean = transportServerBeanVect.get(i);
			initServiceTransport(tmpTransportServerBean.getId(),
					tmpTransportServerBean.getType(),
					tmpTransportServerBean.getIp(),
					tmpTransportServerBean.getPort());
		}
		transportServerBeanVect.clear();
	}
	
	private void initServiceTransport(String idParm, String typeParm, String ipParm, String portParm)
	{
		if (Transport_Type_Mars.equals(typeParm))
		{
			MarsServerSocket tmpMarsServerSocket =
				new MarsServerSocket(ipParm, Integer.parseInt(portParm));
			tmpMarsServerSocket.start();
			theServiceTransportHMap.put(idParm, tmpMarsServerSocket);
		}
		
		if (Transport_Type_Hessian.equals(typeParm))
		{
			String tmpIpAddr = ipParm;
			HessianServer tmpHessianServer = new
					HessianServer(tmpIpAddr, portParm);
			theServiceTransportHMap.put(idParm, tmpHessianServer);
		}
	}
	
	public String generateRegStr(String transPortNameParm)
	{
		return ((ServerTransport)theServiceTransportHMap.get(transPortNameParm)).generateRegStr();
	}
	
	public ClientTransPort getClientTransPort(String transPortIdParm)
	{
		return (ClientTransPort)theClientTransportHMap.get(transPortIdParm);
	}
	
	public ServerTransport getServiceTransport(String transPortNameParm)
	{
		return (ServerTransport)theServiceTransportHMap.get(transPortNameParm);
	}
}
