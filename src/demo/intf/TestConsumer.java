package demo.intf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import org.springframework.beans.factory.InitializingBean;

public class TestConsumer extends Thread implements InitializingBean
{
	private TestService theTestService = null;
	private long totalCnt = 0;
	private long tmpStartTime = System.currentTimeMillis();
	
	public TestConsumer()
	{
		
	}
	
	public void run()
	{
		new MyThread().start();
	}

	public synchronized void addTotalCnt()
	{
		totalCnt++;
		if (totalCnt % 1000 == 0)
		{
			System.out.println(totalCnt+" # "+Thread.currentThread().getName() + "耗时 "+(System.currentTimeMillis() - tmpStartTime));
			tmpStartTime = System.currentTimeMillis();
		}
	}
	
	public TestService getTheTestService() {
		return theTestService;
	}

	public void setTheTestService(TestService theTestService) {
		this.theTestService = theTestService;
		System.out.println("this.theTestService "+this.theTestService);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.start();
	}
	
	class MyThread extends Thread
	{
		protected MyThread()
		{
			
		}
		
		public void run()
		{
			System.out.println("开始");
			long tmpError = 0;
			long tmpStartTime = System.currentTimeMillis();
			ArrayList tmpList = new ArrayList();
			tmpList.add("123");
			tmpList.add("456");
			//tmpList.add(new BigDecimal("100.00"));
			HashMap tmpHMap = new HashMap();
			tmpHMap.put("1", "ok le man1");
			tmpList.add(tmpHMap);
			long tmpSTime = System.currentTimeMillis();
			for (int j = 0; j < 1; j++)
			{
				for (int i = 0; i < 10000; i++)
				{
					try
					{
						theTestService
							.sayHello(Thread.currentThread().getName()+"#"+i+"# ok 123 21312dfsfadsfdadddd" +
									"assasfdsafadsfads21312dfsfadsfdaddddassasfdsafadsfads21312dfsfadsfdaddd我们", tmpList);
					}
					catch(Exception ex)
					{
						tmpError++;
						System.out.println("发生一次异常 "+ex.getMessage());
						ex.printStackTrace();
					}
				}
				
				System.out.println(j+"#"+Thread.currentThread().getName() + "耗时 "+(System.currentTimeMillis() - tmpSTime));
				tmpSTime = System.currentTimeMillis();
			}
			System.out.println("发送完毕，失败消息数 "+tmpError);
		}
	}
}
