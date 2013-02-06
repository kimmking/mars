package com.cn.jd.mars.serviceexporter;

import com.cn.jd.mars.util.bytecode.Wrapper;

public class ServiceExporterWrapper
{
	private Wrapper theWrapper;
	private Object ref;
	
	public ServiceExporterWrapper(Wrapper wrapperParm, Object refInsParm)
	{
		theWrapper = wrapperParm;
		ref = refInsParm;
	}
	
	public Object doInvoke(Object proxy, String methodName,
            Class<?>[] parameterTypes, 
            Object[] arguments) throws Throwable
    {
		return theWrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
	}

	public Object getRef() {
		return ref;
	}
}
