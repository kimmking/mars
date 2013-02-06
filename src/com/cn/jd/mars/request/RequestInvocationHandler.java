package com.cn.jd.mars.request;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import com.cn.jd.mars.request.cluster.AbstrctClusterClientTransPort;

/**
 * RequestInvocationHandler
 * 
 * @author netcomm(baiwenzhi@360buy.com)
 * @date 2013-2-5
 */
public class RequestInvocationHandler implements InvocationHandler
{
    private final AbstrctClusterClientTransPort clusterClientTransPort;
    private String serviceInterfaceName;
    
    public RequestInvocationHandler(AbstrctClusterClientTransPort clusterHandlerParm,
    		String serviceInterfaceNameParm)
    {
    	clusterClientTransPort = clusterHandlerParm;
    	serviceInterfaceName = serviceInterfaceNameParm;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        // Object的基本方法不用走rpc
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(clusterClientTransPort, args);
        }
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return clusterClientTransPort.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return clusterClientTransPort.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return clusterClientTransPort.equals(args[0]);
        }
        
        Request tmpRequest = new Request(serviceInterfaceName, method.getName(), args);
        
        Class<?> tmpRt = method.getReturnType();
        // 判断是否需要返回值
        if(Void.TYPE.equals(tmpRt))
        {
        	clusterClientTransPort.sendRequest(tmpRequest, null);
        	return null;
        }
        else
        {
        	return clusterClientTransPort.sendRequest(tmpRequest, tmpRt);
        }
    }
}
