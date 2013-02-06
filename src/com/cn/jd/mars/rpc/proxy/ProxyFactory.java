package com.cn.jd.mars.rpc.proxy;

import com.cn.jd.mars.request.cluster.AbstrctClusterClientTransPort;
import com.cn.jd.mars.serviceexporter.ServiceExporterWrapper;

public interface ProxyFactory {
	public <T> T getProxy(AbstrctClusterClientTransPort clusterHandlerParm, Class<?>[] interfaces, String serviceInterfaceNameParm);
	public <T> T getCallBackProxy(AbstrctClusterClientTransPort clusterHandlerParm, Class<?>[] interfaces, String serviceInterfaceNameParm);
	public <T> ServiceExporterWrapper getExport(T ref, Class<T> type);
}
