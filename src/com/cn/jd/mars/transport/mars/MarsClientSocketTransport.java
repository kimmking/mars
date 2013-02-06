package com.cn.jd.mars.transport.mars;

import java.io.IOException;
import java.util.HashMap;
import org.apache.log4j.Logger;
import com.cn.jd.mars.request.Request;
import com.cn.jd.mars.spring.ReferenceServiceBean;
import com.cn.jd.mars.transport.ClientTransPort;
import com.cn.jd.mars.transport.TransportFactory;
import com.cn.netcomm.communication.TransportConnection;
import com.cn.netcomm.communication.message.Message;
import com.cn.netcomm.communication.message.MsgMarshallerFactory;
import com.cn.netcomm.communication.transport.AutoReconnectDataSocket;
import com.cn.netcomm.communication.transport.InactiveConnectionMonitor;
import com.cn.netcomm.communication.transport.Transport;
import com.jd.glowworm.PB;

public class MarsClientSocketTransport implements ClientTransPort
{
	private static Logger logger =
		Logger.getLogger(MarsClientSocketTransport.class.getName());
	private String ipAddr;
	private int port;
	private MarsDataTransportConnection theConnection;
	private int timeOut;
	private int retries = 0;
	
	public MarsClientSocketTransport(String ipAddrParm, int portParm,
			HashMap parmHMapParm)
	{
		ipAddr = ipAddrParm;
		port = portParm;
		timeOut = (Integer)parmHMapParm.get
			(ReferenceServiceBean.TimeOut_ParmName);
		retries = (Integer)parmHMapParm.get
				(ReferenceServiceBean.Retries_ParmName);
	}

	@Override
	public void start()
	{
		AutoReconnectDataSocket tmpMonitorDataSocket =
				new AutoReconnectDataSocket(ipAddr, port, 15 * 1000);
		theConnection =
				new MarsDataTransportConnection(tmpMonitorDataSocket,
						InactiveConnectionMonitor.Only_Write_InactiveMonitor);
		Thread threadMe = new Thread(tmpMonitorDataSocket);
		threadMe.start();
	}
	
	@Override
	public Object sendRequest(Request reqParm, Class rtParm)
			throws IOException {
		/*String tmpJStr = JSON.toJSONString(reqParm, SerializerFeature.WriteClassName);
		Message tmpMsg = new Message(MsgMarshallerFactory.Request_MsgType,
				tmpJStr.getBytes(), true);*/
		byte[] tmpRequestBytes = PB.toPBBytes(reqParm);
		Message tmpMsg = new Message(MsgMarshallerFactory.Request_MsgType,
				tmpRequestBytes, true);
		
		Message tmpRespMsg = null;
		int tmpSendCnt = 1 + retries;
		for (int i = 0; i < tmpSendCnt; i++)
		{
			try
			{
				tmpRespMsg = theConnection.sendMsg(tmpMsg, timeOut);
				if (tmpRespMsg.getMsgType() == MsgMarshallerFactory.TransportExceptionResponse_MsgType
						|| tmpRespMsg.getMsgType() == MsgMarshallerFactory.BusinExceptionResponse_MsgType)
				{
					throw new IOException(new String(tmpRespMsg.getContent()));
				}
				else
				{
					break;
				}
			}
			catch (IOException ex)
			{
				if (i == (tmpSendCnt - 1))
				{
					logger.error("通讯发送异常,尝试 "+tmpSendCnt + "次后还是失败!!!");
					throw new IOException("通讯发送异常,尝试 "+tmpSendCnt + "次后还是失败!!!");
				}
				logger.error("[通讯异常], 发送次数 " + (i+1), ex);
			}
		}
		
		//Object tmpRet = JSON.parse(new String(tmpRespMsg.getContent()));
		Object tmpRet = PB.parsePBBytes(tmpRespMsg.getContent(), rtParm);
		
		return tmpRet;
	}

	@Override
	public void sendRequest(Request reqParm) throws IOException {
		/*String tmpJStr = JSON.toJSONString(reqParm, SerializerFeature.WriteClassName);
		Message tmpMsg = new Message(MsgMarshallerFactory.Request_MsgType,
				tmpJStr.getBytes(), false);*/
		byte[] tmpRequestBytes = PB.toPBBytes(reqParm);
		Message tmpMsg = new Message(MsgMarshallerFactory.Request_MsgType,
				tmpRequestBytes, false);
		
		int tmpSendCnt = 1 + retries;
		for (int i = 0; i < tmpSendCnt; i++)
		{
			try
			{
				theConnection.sendMsg(tmpMsg);
				break;
			}
			catch (IOException ex)
			{
				if (i == (tmpSendCnt - 1))
				{
					throw new IOException("通讯发送异常,尝试 "+tmpSendCnt + "次后还是失败!!!");
				}
			}
		}
	}
	
	class MarsDataTransportConnection extends TransportConnection
	{
		public MarsDataTransportConnection(Transport transportParm,
				int inactiveMonitorTypeParm)
		{
			super(transportParm, inactiveMonitorTypeParm, null);
		}
		
		public Message doMsgHandler(Message reqMsgParm)
		{
			System.out.println("发送socket接收一个从服务器端的请求回应或主动请求: "
					+reqMsgParm.getMsgType());
			Message response = new Message(MsgMarshallerFactory.Response_MsgType,
	        		new String("").getBytes(), false);
			return response;
		}
		
		public void transportOnFirstConnect()
		{
			
		}
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String getKey() {
		return ipAddr+":"+port;
	}

	@Override
	public String getType() {
		return TransportFactory.Transport_Type_Mars;
	}

	@Override
	public void stop() throws Exception
	{
		if (theConnection != null)
		{
			theConnection.stop();
			theConnection = null;
		}
	}

	@Override
	public int getRetries() throws Exception
	{
		return retries;
	}
}
