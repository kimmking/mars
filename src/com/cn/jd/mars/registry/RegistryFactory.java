package com.cn.jd.mars.registry;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import com.cn.jd.mars.request.cluster.ClusterClientTransPortFactory;
import com.cn.jd.mars.util.ZookeeperUtilities;

public class RegistryFactory {
	private static Logger logger =
		Logger.getLogger(RegistryFactory.class.getName());
	private static RegistryFactory service = new RegistryFactory();
	private boolean isRegistryOk = false;
	private String marsRootPath = "/mars";
	private String address;
	private ArrayList serviceNameList = new ArrayList();
	
	private RegistryFactory()
	{
		
	}
	
	public static RegistryFactory getInstance()
	{
		return service;
	}
	
	/**
	 * 初始化和注册中心的交互
	 * @param protocolParm
	 * @param addressParm
	 */
	public void initRegistry(String protocolParm, String addressParm)
	{
		address = addressParm;
		// 目前只支持zookeeper的注册中心
		if ("zookeeper".equals(protocolParm.toLowerCase()))
		{
			try
			{
				ZookeeperUtilities.getInstance().init(addressParm, marsRootPath);
				isRegistryOk = true;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * 注册一个服务到注册中心
	 * @param serviceNamePathParm
	 * @param detailParm
	 */
	public void registerOneServiceExporter(String serviceNamePathParm, String detailParm)
	{
		try
		{
			if (isRegistryOk == true)
			{
				String tmpIpAddr = InetAddress.getLocalHost()
					.getHostAddress().toString() +"_"+ System.nanoTime();
				String tmpPath = serviceNamePathParm + "/providers/"+tmpIpAddr;
				ZookeeperUtilities.getInstance().addServiceExporter(
					marsRootPath, tmpPath, detailParm);
			}
		}
		catch (ConnectionLossException ce)
		{
			logger.error("和zookeeper的连接断开,无法注册服务!!!", ce);
			ce.printStackTrace();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void subscribeChildChanges(String serviceNameParm, String connectionsParm,
			HashMap parmHMapParm)
	{
		if (isRegistryOk == true)
		{
			int tmpConnections = 1;
			try
			{
				tmpConnections = Integer.parseInt(connectionsParm);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
			serviceNameList.add(serviceNameParm);
			String tmpServicePath = marsRootPath + "/" + serviceNameParm + "/providers";
			ZkClient zkClient4subChild = new ZkClient(address, 10000, 10000,
	                new StringSerializer());
			zkClient4subChild.subscribeChildChanges(
				tmpServicePath, new CustIZkChildListener(
					serviceNameParm, tmpConnections, zkClient4subChild, parmHMapParm));
			
			List<String> tmpChildrenDatas = getZKChildrenValues(zkClient4subChild, tmpServicePath);
			for (int i = 0; i < tmpChildrenDatas.size(); i++)
			{
				ClusterClientTransPortFactory.getInstance()
					.addOneClusterItem(serviceNameParm, tmpConnections, tmpChildrenDatas.get(i),
							parmHMapParm);
			}
		}
	}
	
	private ArrayList getZKChildrenValues(ZkClient zkParm, String pathParm)
	{
		ArrayList retList = new ArrayList();
		
		List tmpChildren = zkParm.getChildren(pathParm);
		for (int i = 0; i < tmpChildren.size(); i++)
		{
			String tmpFullPath = pathParm +"/"+ tmpChildren.get(i);
			String tmpData = zkParm.readData(tmpFullPath);
			retList.add(tmpData);
		}
		
		return retList;
	}
	
	class CustIZkChildListener implements IZkChildListener
	{
		private String serviceName;
		private int connections;
		private ZkClient zk;
		private HashMap parmHMap;
		protected CustIZkChildListener(String serviceNameParm,
			int connectionsParm, ZkClient zkParm,
			HashMap parmHMapParm)
		{
			serviceName = serviceNameParm;
			connections = connectionsParm;
			zk = zkParm;
			parmHMap = parmHMapParm;
		}

		@Override
		public void handleChildChange(String parentPath, List<String> currentChilds)
				throws Exception {
			System.out.println("ZkClient clildren of path " + parentPath + ":" + currentChilds);
			ArrayList tmpFormatStrList = new ArrayList();
            int tmpListSz = currentChilds.size();
            for (int i = 0; i < tmpListSz; i++)
            {
            	String tmpFullPath = parentPath +"/"+ currentChilds.get(i);
    			String tmpData = zk.readData(tmpFullPath);
    			// 新增服务提供者
            	ClusterClientTransPortFactory.getInstance()
					.addOneClusterItem(serviceName, connections, tmpData,
							parmHMap);
            	tmpFormatStrList.add(tmpData);
            }
            
            // 如果因为zk网络等原因导致服务提供者对应的临时节点被删除,处于对业务的保护，这种情况不删除已有的通讯链路。
            /*ClusterClientTransPortFactory.getInstance()
				.adjustClusterItems(serviceName, tmpFormatStrList);*/
		}
		
	}
	
	public static class StringSerializer implements ZkSerializer {
        @Override
        public Object deserialize(final byte[] bytes) throws ZkMarshallingError {
            try {
                return new String(bytes, "utf-8");
            }
            catch (final UnsupportedEncodingException e) {
                throw new ZkMarshallingError(e);
            }
        }


        @Override
        public byte[] serialize(final Object data) throws ZkMarshallingError {
            try {
                return ((String) data).getBytes("utf-8");
            }
            catch (final UnsupportedEncodingException e) {
                throw new ZkMarshallingError(e);
            }
        }

    }
}
