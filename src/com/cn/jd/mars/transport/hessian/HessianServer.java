package com.cn.jd.mars.transport.hessian;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.server.HessianSkeleton;
import com.cn.jd.mars.transport.ServerTransport;
import com.cn.jd.mars.util.JettyHttpServer;
import com.cn.jd.mars.util.ReflectUtils;

public class HessianServer implements ServerTransport
{
	private JettyHttpServer theHttpServer;
	private String serverIp;
	private String port;
	private HashMap theServiceExportHMap = new HashMap();
	
	public HessianServer(String ipAddrParm, String portParm)
	{
		serverIp = ipAddrParm;
		port = portParm;
		theHttpServer = new JettyHttpServer(serverIp, Integer.parseInt(portParm), new theHttpHandler());
	}
	
	class theHttpHandler implements HttpHandler
	{
		@Override
		public void handle(HttpServletRequest request,
				HttpServletResponse response) throws IOException,
				ServletException {
			String uri = request.getRequestURI();
			HessianSkeleton exporter = (HessianSkeleton)theServiceExportHMap.get(uri);
			try
			{
				exporter.invoke(request.getInputStream(), response.getOutputStream());
			}
			catch (Throwable e)
			{
                throw new ServletException(e);
            }
		}
	}
	
	public void addOneServiceExporter(String serviceNameParm,
			String interfaceNameParm,
			Object serviceImplParm)
	{
		HessianSkeleton skeleton = new HessianSkeleton(serviceImplParm,
				ReflectUtils.forName(interfaceNameParm));
		//"http://localhost:8080/hessiantest/hessian/math";
		String tmpURL = "/" + serviceNameParm;
		theServiceExportHMap.put(tmpURL, skeleton);
	}

	@Override
	public String generateRegStr() {
		// TODO Auto-generated method stub
		return null;
	}
}
