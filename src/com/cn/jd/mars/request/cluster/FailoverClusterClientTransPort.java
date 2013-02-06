package com.cn.jd.mars.request.cluster;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import com.cn.jd.mars.request.Request;
import com.cn.jd.mars.request.cluster.loadbalance.AbstractLoadBalance;
import com.cn.jd.mars.transport.ClientTransPort;
import com.cn.netcomm.communication.exception.ReqTimeOutException;


/**
 * Failover的集群策略
 * 
 * @author netcomm(baiwenzhi@360buy.com)
 * @date 2013-2-5
 */
public class FailoverClusterClientTransPort extends AbstrctClusterClientTransPort
{
	private static Logger logger =
		Logger.getLogger(FailoverClusterClientTransPort.class.getName());
	private static int MaxExceptionTryCnt = 10;
	private HashMap<String, AtomicInteger> theExceptionTryCntHMap = new HashMap();
	
	public FailoverClusterClientTransPort(
			AbstractLoadBalance theLoadBalanceParm)
	{
		super(theLoadBalanceParm);
	}

	@Override
	public Object sendRequest(Request reqParm, Class rtParm)
			throws IOException
	{
		Object retObj = null;
		int tmpSz = clientTransPortList.size();
		ClientTransPort tmpClientTransPort = null;
		boolean isSendOk = false;
		
		for (int i = 0; i < tmpSz; i++)
		{
			tmpClientTransPort = theLoadBalance.select();
			if (tmpClientTransPort == null)
			{
				break;
			}
			
			try
			{
				if (rtParm != null)
				{
					retObj = tmpClientTransPort.sendRequest(reqParm, rtParm);
				}
				else
				{
					tmpClientTransPort.sendRequest(reqParm);
				}
				
				isSendOk = true;
				
				// 删除链路异常统计
				String tmpKey = tmpClientTransPort.getKey();
				theExceptionTryCntHMap.remove(tmpKey);
				break;
			}
			catch (SocketException se)
			{
				// 忽略该异常
				logger.warn("忽略该异常", se);
			}
			catch (ReqTimeOutException re)
			{
				throw re;
			}
			catch (Exception ex)
			{
				oneTimeClientTransPortError(tmpClientTransPort);
				logger.error("该条链路已经断开,请确认!!!", ex);
			}
		}
		
		if (isSendOk == false)
		{
			throw new IOException("没有可用的服务提供者或请求超时!!!");
		}
		
		return retObj;
	}

	private void oneTimeClientTransPortError(ClientTransPort clientTransPortParm)
	{
		String tmpKey = clientTransPortParm.getKey();
		AtomicInteger tmpCnt = theExceptionTryCntHMap.get(tmpKey);
		if (tmpCnt == null)
		{
			tmpCnt = new AtomicInteger(1);
			theExceptionTryCntHMap.put(tmpKey, tmpCnt);
		}
		else
		{
			tmpCnt.addAndGet(1);
		}
		
		logger.warn("通讯发送异常,重试次数 "+tmpCnt.get());
		if (tmpCnt.get() >= MaxExceptionTryCnt)
		{
			theExceptionTryCntHMap.remove(tmpKey);
			removeOneClientTransPort(clientTransPortParm);
			try
			{
				clientTransPortParm.stop();
			}
			catch(Exception ex)
			{
				logger.error("移除该条链路 "+tmpKey, ex);
			}
		}
	}
}
