package Customer.com.customer;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

public class ConsumerMsgTask implements Runnable {
	private KafkaStream m_stream;
	private int m_threadNumber;
	private String my_prefer;
	private int count = 0;

	public ConsumerMsgTask(KafkaStream stream, int threadNumber,String prefer) {
		my_prefer = prefer;
		m_threadNumber = threadNumber;
		m_stream = stream;
	}

	public void run() {
		ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
		//首先我们要遍历kafka过来的消息
		while (it.hasNext()) {
			String msg = new String(it.next().message());
			if (my_prefer==null) {
				my_prefer = msg;
				CustomerDemo.prefer = my_prefer;
				count = 0;
			}
			if (!(msg.equals(my_prefer))) {
				count++;
			}
			//如果我们发现大量的消息（即）用户的行为和我们现在的推荐信息是不符合的，那么我们就对Zookeeper的/sgroup目录进行更改，来通知推荐系统更换推荐策略
			if (count>1000) {
				System.out.println("Customer 经过分析得知用户行为发生了变化");
				RecommandeServer as = new RecommandeServer();  
			    try {
					as.connectZookeeper();
					/*在sgroup里面创建recommande 节点，注意这里的节点必须是SEQUENTIAL的，因为只有样才能保证节点是有序的，然后在推荐系统可以很方便的进行过处理
					 * 并且强烈推荐把节点设置成EPHEMERAL的，因为网络可能发生各种问题，服务其也可能出问题，如果一个服务器掉线了，节点是EPHEMERAL的话就可以自动清除
					 * 掉这个信息，即我们不认为一个有问题的服务器传过来的数据是有价值大的
					 */
					as.getZk().create("/sgroup/recommande", msg.getBytes(),  Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
					//as.handle();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
			    //更新推荐的商品信息
				CustomerDemo.prefer = msg;
				my_prefer = msg;
				count = 0;
			}
		}
			
		System.out.println("Shutting down Thread: " + m_threadNumber);
	}
}
