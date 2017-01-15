package Customer.com.customer;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.server.ServerCnxn.Stats;

public class RecommandeServer {  
    private String groupNode = "sgroup";  
    private String subNode = "sub";  
    private ZooKeeper zk;
    
    
    
    public ZooKeeper getZk() {
		return zk;
	}

	public void setZk(ZooKeeper zk) {
		this.zk = zk;
	}

	public String getSubNode() {
		return subNode;
	}

	public void setSubNode(String subNode) {
		this.subNode = subNode;
	}

	/** 
     * 连接zookeeper 
     * @param address server的地址 
     */  
    public class myWatch implements Watcher {
    	
		public void process(WatchedEvent arg0) {
			// TODO Auto-generated method stub
			
		} 

    }
    
    public void connectZookeeper() throws Exception {  
        zk = new ZooKeeper("localhost:2181,localhost:2182,localhost:2183", 5000, new myWatch());
        // 在"/sgroup"下创建子节点  
        // 子节点的类型设置为EPHEMERAL_SEQUENTIAL, 表明这是一个临时节点, 且在子节点的名称后面加上一串数字后缀  
        // 将server的地址数据关联到新创建的子节点上  
        if (zk.exists("/"+groupNode, true) != null) {
			
		}
        else {
            String createdPath = zk.create("/" + groupNode, "recommandatin".getBytes(),   
            Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
   
    }  
      
    /** 
     * server的工作逻辑写在这个方法中 
     * 此处不做任何处理, 只让server sleep 
     */  
    public void handle() throws InterruptedException {  
        Thread.sleep(Long.MAX_VALUE);  
    }  
   
    
    public static void main(String[] args) throws Exception {  
        // 在参数中指定server的地址  
        RecommandeServer as = new RecommandeServer();  
        as.connectZookeeper();  
        //as.getZk().create("/sgroup/sub", "05".getBytes(),  Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(as.getZk().getChildren("/sgroup", true));
        //as.getZk().delete("/sgroup",-1);
        as.getZk().close();
    }  
}  
