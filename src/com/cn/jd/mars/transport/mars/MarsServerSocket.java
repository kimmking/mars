package com.cn.jd.mars.transport.mars;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Logger;
import com.cn.jd.mars.request.Request;
import com.cn.jd.mars.serviceexporter.MarsServiceExportService;
import com.cn.jd.mars.transport.ServerTransport;
import com.cn.jd.mars.transport.TransportFactory;
import com.cn.netcomm.communication.TransportConnection;
import com.cn.netcomm.communication.message.Message;
import com.cn.netcomm.communication.message.MsgMarshallerFactory;
import com.cn.netcomm.communication.transport.InactiveConnectionMonitor;
import com.cn.netcomm.communication.transport.Transport;
import com.cn.netcomm.communication.transport.TransportListener;
import com.cn.netcomm.communication.util.Utilities;
import com.cn.netcomm.communication.util.WorkerHandlerThreadPool;
import com.jd.glowworm.PB;

public class MarsServerSocket extends Thread implements ServerTransport
{
	private static Logger logger = Logger.getLogger(MarsServerSocket.class.getName());
	
	public static final int Socket_Close = 1;
	private String ip;
	private int port = 10000;
	private ServerSocket theServerSocket;
	
	public MarsServerSocket(String ipParm, int portParm)
	{
		ip = ipParm;
		port = portParm;
	}
	
	public void run()
	{
		try
		{
			theServerSocket = new ServerSocket(port, 0, InetAddress.getByName(ip));
			theServerSocket.setReceiveBufferSize(1024 * 32); // 将接收缓冲区设为32K
			
			logger.info("启动侦听服务成功:端口 "+port);
			while (true)
			{
				Socket socket = theServerSocket.accept();
				System.out.println("接受一个新连接 " + socket+"#"
						+socket.getReceiveBufferSize());
				new SocketHandler(socket);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("服务器接受连接请求发生异常。请重启系统");
		}
	}
	
	public void setPort(int portParm)
	{
		this.port = portParm;
	}
	
	public String generateRegStr()
	{
		String retStr =
				Utilities.generateFormatStr(TransportFactory.Transport_Type_Mars)
				+Utilities.generateFormatStr(ip)
				+Utilities.generateFormatStr(Integer.toString(port));
		return retStr;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}

class MarsTranspConnection extends TransportConnection
{
	private int count = 0;
	
	public MarsTranspConnection(Transport transportParm,
			int inactiveMonitorTypeParm)
	{
		super(transportParm, inactiveMonitorTypeParm, new WorkerHandlerThreadPool(100));
	}
	
	public Message doMsgHandler(Message reqMsgParm) throws Throwable
	{
		Message retMsg = null;
		//Request tmpResp = (Request)JSON.parse(new String(reqMsgParm.getContent()));
		Request tmpRequest = (Request)PB.parsePBBytes(reqMsgParm.getContent(), Request.class);
		
		Object retObj = MarsServiceExportService.getInstance().onRequestHandle(tmpRequest);
		
		if (retObj != null)
		{
			/*String tmpJStr = JSON.toJSONString(retObj, SerializerFeature.WriteClassName);
			retMsg = new Message(MsgMarshallerFactory.Response_MsgType,
				tmpJStr.getBytes(), false);*/
			byte[] tmpRequestBytes = PB.toPBBytes(retObj);
			retMsg = new Message(MsgMarshallerFactory.Response_MsgType,
					tmpRequestBytes, false);
		}
		
		return retMsg;
	}
	
	public void transportOnException(Exception exception)
	{
		System.out.println("通讯接收连接断开,共消费消息数 "+count);
	}
}

class SocketHandler implements Runnable, Transport
{
	private Socket openedSocket = null;
	private TransportListener transportListener;
	private MarsTranspConnection theCustTransportConnection;

	public SocketHandler(Socket openedSocketParm)
	{
		openedSocket = openedSocketParm;
		theCustTransportConnection =
			new MarsTranspConnection(this,
					InactiveConnectionMonitor.Only_Read_InactiveMonitor);
		
		Thread theThread = new Thread(this);
		theThread.start();
	}

	public void run()
	{
		try
		{
			InputStream inputStream = openedSocket.getInputStream();
			while (true)
			{
				Message tmpOneReqMsg = Utilities.getInstance().readMsg(inputStream);
				/*System.out.println(
						openedSocket.getRemoteSocketAddress()+"接收一个消息请求,类型"+tmpOneReqMsg.getMsgType());*/
				if (tmpOneReqMsg != null)
				{
					transportListener.onCommand(tmpOneReqMsg);
				}
				else
				{
					break;
				}
			}
		}
		catch (Exception ioe)
		{
			ioe.printStackTrace();
			System.out.println(openedSocket + "连接关闭");
			onException(ioe);
		}
	}
	
	private void onException(Exception ex)
	{
		transportListener.onException(ex);
		transportListener = null;
		openedSocket = null;
		theCustTransportConnection = null;
	}

	public String getRemoteAddress()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public void oneway(Message sendMsgParm) throws IOException
	{
		Utilities.getInstance().writeMsgThroughTcp(
				sendMsgParm, openedSocket.getOutputStream());
	}
	
	public TransportListener getTransportListener()
	{
		return transportListener;
	}
	
	public void setTransportListener(TransportListener commandListener)
	{
		transportListener = commandListener;
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
