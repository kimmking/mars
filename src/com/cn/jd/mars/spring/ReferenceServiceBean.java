package com.cn.jd.mars.spring;

import java.util.HashMap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import com.cn.jd.mars.registry.RegistryFactory;
import com.cn.jd.mars.request.cluster.AbstrctClusterClientTransPort;
import com.cn.jd.mars.request.cluster.ClusterClientTransPortFactory;
import com.cn.jd.mars.request.cluster.loadbalance.AbstractLoadBalance;
import com.cn.jd.mars.request.cluster.loadbalance.LoadBalanceFactory;
import com.cn.jd.mars.rpc.proxy.JavassistProxyFactory;
import com.cn.jd.mars.rpc.proxy.ProxyFactory;
import com.cn.jd.mars.transport.ClientTransPort;
import com.cn.jd.mars.transport.TransportFactory;
import com.cn.jd.mars.util.ReflectUtils;
import com.cn.netcomm.communication.util.Utilities;

public class ReferenceServiceBean implements InitializingBean, FactoryBean, ApplicationContextAware, ApplicationListener, BeanNameAware
{
	public static String TimeOut_ParmName = "timeout";
	public static String Retries_ParmName = "retries";
	private Class theInterface;
	private String id;
	private String clustertype = "failover";   // 默认failover集群方式
	private String loadbalance = "roundrobin"; // 默认roundrobin负载均衡策略
	private String clusterItems; 	           // 在服务调用端配置的静态服务提供者id列表，以","分割
	private String serviceInterfaceName;       // 服务接口名
	private String connections = "1";          // 对每个提供者建立的长连接个数,默认为1
	private String timeout = "3000";           // 服务方法调用超时时间(毫秒)
	private String retries = "0";              // 远程服务调用的重试次数，不包括第一次调用，不需要重试请设为0
	private ProxyFactory theProxyFactory = new JavassistProxyFactory();
	private AbstrctClusterClientTransPort clusterClientTransPort;
	private Object theServiceProxy;
	
	public ReferenceServiceBean()
	{
		
	}
	
	public ReferenceServiceBean(String serviceInterfaceParm, Class interfaceParm,
			AbstrctClusterClientTransPort clusterClientTransPortParm)
	{
		serviceInterfaceName = serviceInterfaceParm;
		theInterface = interfaceParm;
		clusterClientTransPort = clusterClientTransPortParm;
	}
	
	public Object createProxy()
	{
		Class<?>[] tmpInterfaces = new Class<?>[1];
        tmpInterfaces[0] = theInterface;
        
        // 创建服务代理
        return theProxyFactory.getProxy(clusterClientTransPort, tmpInterfaces, serviceInterfaceName);
    }

	@Override
	public void setBeanName(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onApplicationEvent(ApplicationEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
    	AbstractLoadBalance tmpLoadBalance = LoadBalanceFactory.getInstance()
    		.generateOneLoadBalance(loadbalance);
    	AbstrctClusterClientTransPort tmpNewClusterClientTransPort =
    		ClusterClientTransPortFactory.getInstance()
    			.generateClusterClientTransPort(serviceInterfaceName, clustertype, tmpLoadBalance);
    	clusterClientTransPort = tmpNewClusterClientTransPort;
    	
    	// 在服务调用端配置的静态服务提供者id列表，以","分割。
    	String[] tmpClusterItemsStrs = Utilities.strSplit(clusterItems, ",");
    	for (int i = 0; i < tmpClusterItemsStrs.length; i++)
    	{
    		ClientTransPort tmpClientTransPortIns = TransportFactory.getInstance()
				.getClientTransPort(tmpClusterItemsStrs[i]);
    		tmpNewClusterClientTransPort.addOneClientTransPort(tmpClientTransPortIns);
    	}
    	
    	theInterface = ReflectUtils.forName(serviceInterfaceName);
    	theServiceProxy = createProxy();
    	
    	HashMap tmpParmHMap = new HashMap();
    	tmpParmHMap.put(TimeOut_ParmName, Integer.parseInt(timeout));
    	tmpParmHMap.put(Retries_ParmName, Integer.parseInt(retries));
    	RegistryFactory.getInstance().subscribeChildChanges(serviceInterfaceName, connections,
    			tmpParmHMap);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClustertype() {
		return clustertype;
	}

	public void setClustertype(String clustertype) {
		this.clustertype = clustertype;
	}

	public String getLoadbalance() {
		return loadbalance;
	}

	public void setLoadbalance(String loadbalance) {
		this.loadbalance = loadbalance;
	}

	public String getClusterItems() {
		return clusterItems;
	}

	public void setClusterItems(String clusterItems) {
		this.clusterItems = clusterItems;
	}

	public String getInterface() {
		return serviceInterfaceName;
	}

	public void setInterface(String serviceInterfaceName) {
		this.serviceInterfaceName = serviceInterfaceName;
	}

	@Override
	public Object getObject() throws Exception {
		return theServiceProxy;
	}

	@Override
	public Class getObjectType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getConnections()
	{
		return connections;
	}

	public void setConnections(String connections)
	{
		this.connections = connections;
	}

	public String getTimeout()
	{
		return timeout;
	}

	public void setTimeout(String timeout)
	{
		this.timeout = timeout;
	}

	public String getRetries()
	{
		return retries;
	}

	public void setRetries(String retries)
	{
		this.retries = retries;
	}
}
