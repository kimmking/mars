/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cn.jd.mars.rpc.proxy;

import com.cn.jd.mars.request.RequestInvocationHandler;
import com.cn.jd.mars.request.cluster.AbstrctClusterClientTransPort;
import com.cn.jd.mars.serviceexporter.ServiceExporterWrapper;
import com.cn.jd.mars.util.bytecode.Proxy;
import com.cn.jd.mars.util.bytecode.Wrapper;

/**
 * JavaassistRpcProxyFactory 

 * @author william.liangf
 */
public class JavassistProxyFactory implements ProxyFactory
{
    public <T> T getProxy(AbstrctClusterClientTransPort clusterHandlerParm, Class<?>[] interfaces, String serviceInterfaceNameParm)
    {
        return (T) Proxy.getProxy(interfaces).newInstance(
        	new RequestInvocationHandler(clusterHandlerParm, serviceInterfaceNameParm));
    }
    
	public <T> ServiceExporterWrapper getExport(T ref, Class<T> type) {
		// TODO Wrapper类不能正确处理带$的类名
        final Wrapper wrapper = Wrapper.getWrapper(ref.getClass().getName().indexOf('$') < 0 ? ref.getClass() : type);
        return new ServiceExporterWrapper(wrapper, ref);
	}

	@Override
	public <T> T getCallBackProxy(
			AbstrctClusterClientTransPort clusterHandlerParm,
			Class<?>[] interfaces, String serviceInterfaceNameParm)
	{
		// TODO Auto-generated method stub
		return null;
	}
}