package com.cn.jd.mars.util;

import java.util.Vector;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import com.cn.netcomm.communication.util.Utilities;

public class ZookeeperUtilities
{
	private static Logger logger = Logger.getLogger(ZookeeperUtilities.class.getName());
	private static ZookeeperUtilities service = new ZookeeperUtilities();
	private ZooKeeper zk;
	private Vector<String[]> serviceExporterVect = new Vector();
	private String address;
	private String rootPath;

	private ZookeeperUtilities()
	{

	}

	public static ZookeeperUtilities getInstance()
	{
		return service;
	}

	public void init(String addressParm, String rootPathParm) throws Exception
	{
		address = addressParm;
		rootPath = rootPathParm;
	}

	public void addServiceExporter(String rootPathParm,
			String serviceNamePathParm, String detailParm) throws Exception
	{
		String[] tmpServiceExporterStrs = new String[3];
		tmpServiceExporterStrs[0] = rootPathParm;
		tmpServiceExporterStrs[1] = serviceNamePathParm;
		tmpServiceExporterStrs[2] = detailParm;
		serviceExporterVect.add(tmpServiceExporterStrs);
		
		doAddServiceExporter(rootPathParm,
				serviceNamePathParm, detailParm);
	}

	private void doAddServiceExporter(String rootPathParm,
			String serviceNamePathParm, String detailParm) throws Exception
	{
		System.out.println(serviceNamePathParm + "#detailParm " + detailParm);
		initRootPath();
		
		String[] tmpPaths = Utilities.strSplit(serviceNamePathParm, "/");
		String tmpPath = rootPathParm;
		for (int i = 0; i < tmpPaths.length; i++)
		{
			tmpPath = tmpPath + "/" + tmpPaths[i];
			System.out.println(tmpPath);
			Stat s = zk.exists(tmpPath, false);
			if (s == null)
			{
				if (i == (tmpPaths.length - 1))
				{
					zk.create(tmpPath, detailParm.getBytes(),
							Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				} else
				{
					zk.create(tmpPath, "".getBytes(), Ids.OPEN_ACL_UNSAFE,
							CreateMode.PERSISTENT);
				}
			}
		}
	}
	
	private void initRootPath() throws Exception
	{
		if (zk == null)
		{
			int tmpTimeOut = 10000;
			zk = new ZooKeeper(address, tmpTimeOut,
					new Init_Watcher(address, tmpTimeOut));
			
			String[] tmpPaths = Utilities.strSplit(rootPath, "/");
			String tmpPath = "";
			for (int i = 1; i < tmpPaths.length; i++)
			{
				tmpPath = tmpPath + "/" + tmpPaths[i];
				Stat s = zk.exists(tmpPath, false);
				if (s == null)
				{
					zk.create(tmpPath, "".getBytes(), Ids.OPEN_ACL_UNSAFE,
							CreateMode.PERSISTENT);
				}
			}
		}
	}
	
	/*
	 * 和zk重新建立连接后，把需要注册的服务重新上传。
	 */
	private void reSendServiceExporter()
	{
		try
		{
			for (int i = 0; i < serviceExporterVect.size(); i++)
			{
				String[] tmpOneItem = serviceExporterVect.get(i);
				doAddServiceExporter(tmpOneItem[0], tmpOneItem[1], tmpOneItem[2]);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	class Init_Watcher implements Watcher
	{
		private String address;
		private int timeout;
		
		protected Init_Watcher(String addressParm, int timeoutParm)
		{
			address = addressParm;
			timeout = timeoutParm;
		}
		
		// 监控所有被触发的事件
		public void process(WatchedEvent event)
		{
			logger.info("zooKeeper 发生事件 " + event);
			boolean tmpIsReConnectOk = false;
			int tmpIsReConnectCnt = 0;
			
			switch (event.getState())
			{
				case Expired:
					logger.info("State expired! 重新建立和zk的连接");
					while ( ! tmpIsReConnectOk)
					{
						tmpIsReConnectCnt++;
						try
						{
							Thread.sleep(100);
							zk.close();
							zk = null;
							zk = new ZooKeeper(address, timeout, this);
							tmpIsReConnectOk = true;
							tmpIsReConnectCnt = 0;
							logger.info("成功重连zk服务器,重试次数 "+tmpIsReConnectCnt);
							break;
						}
						catch (Exception e)
						{
							logger.info("重试连接zk服务器,重试次数 "+tmpIsReConnectCnt);
							//e.printStackTrace();
						}
					}
					
					reSendServiceExporter();
					break;
				default:
					break;
			}
		}
	}
}
