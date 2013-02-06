package com.cn.jd.mars.serviceexporter;

import java.util.HashMap;
import java.util.Vector;

import com.cn.jd.mars.registry.RegistryFactory;
import com.cn.jd.mars.request.Request;
import com.cn.jd.mars.rpc.proxy.JavassistProxyFactory;
import com.cn.jd.mars.rpc.proxy.ProxyFactory;
import com.cn.jd.mars.transport.TransportFactory;
import com.cn.jd.mars.util.ReflectUtils;


public class MarsServiceExportService {
	private static MarsServiceExportService service = new MarsServiceExportService();
	private HashMap serviceProxyHMap = new HashMap();
	private ProxyFactory theProxyFactory = new JavassistProxyFactory();
	private HashMap theServiceExporterWrapperHMap = new HashMap();
	private Vector<String[]> initRegistryServiceVect = new Vector();
	
	private MarsServiceExportService()
	{
		
	}
	
	public static MarsServiceExportService getInstance()
	{
		return service;
	}
	
	/**
	 * 注册服务
	 */
	public synchronized void initRegistryServiceVect()
	{
		String[] tmpStrs = null;
		for (int i = 0; i < initRegistryServiceVect.size(); i++)
		{
			tmpStrs = initRegistryServiceVect.get(i);
			String tmpDetailParm = TransportFactory.getInstance().generateRegStr(tmpStrs[0]);
			RegistryFactory.getInstance().registerOneServiceExporter(
					tmpStrs[1], tmpDetailParm);
		}
		
		initRegistryServiceVect.clear();
	}
	
	public void registryService(String interfaceNameParm,
			Object serviceImplParm, String transporServerParm) throws Exception
	{
		String[] tmpStrs = new String[2];
		tmpStrs[0] = transporServerParm;
		tmpStrs[1] = interfaceNameParm;
		initRegistryServiceVect.add(tmpStrs);
		
		Class tmpServiceIntf = ReflectUtils.forName(interfaceNameParm);
		ServiceExporterWrapper tmpServiceExporterWrapper =
				theProxyFactory.getExport(serviceImplParm, (Class)tmpServiceIntf);
		theServiceExporterWrapperHMap.put(interfaceNameParm, tmpServiceExporterWrapper);
	}
	
	public Object onRequestHandle(Request theRequestParm) throws Throwable
	{
		Object ret = null;
		ServiceExporterWrapper tmpServiceExporterWrapper =
				(ServiceExporterWrapper)theServiceExporterWrapperHMap
					.get(theRequestParm.getServiceInterfaceName());
		
		Class<?>[] tmpParameterTypes = null;
		Object[] tmpArguments = theRequestParm.getArguments();
		if (tmpArguments != null)
		{
			tmpParameterTypes = new Class<?>[tmpArguments.length];
			for (int i = 0; i < tmpArguments.length; i++)
			{
				tmpParameterTypes[i] = tmpArguments.getClass();
			}
		}
		
		ret = tmpServiceExporterWrapper.doInvoke(
				tmpServiceExporterWrapper.getRef(), theRequestParm.getMethodName(),
				tmpParameterTypes, theRequestParm.getArguments());
		
		return ret;
	}

	public ProxyFactory getTheProxyFactory() {
		return theProxyFactory;
	}
}
