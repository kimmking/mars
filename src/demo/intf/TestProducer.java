package demo.intf;

import java.util.ArrayList;

public class TestProducer implements TestService{
	private int i = 0;
	private long startTime = 0;
	private int thrdCnt = 1000;
	
	public TestProducer()
	{
		System.out.println("启动服务");
	}
	
	@Override
	public String sayHello(String hiStrParm, ArrayList detailListParm) {
		//System.out.println("haha sayHello " + hiStrParm);
		if (i == 0)
		{
			startTime = System.currentTimeMillis();
		}
		
		synchronized (this)
		{
			i++;
			if (i % thrdCnt == 0)
			{
				System.out.println(i + " 处理 "+thrdCnt+"耗时"+(System.currentTimeMillis() - startTime));
				startTime = System.currentTimeMillis();
			}
		}
		
		System.out.println("处理第 " + i + " 条消息");
		return "new world "+hiStrParm;
	}
}
