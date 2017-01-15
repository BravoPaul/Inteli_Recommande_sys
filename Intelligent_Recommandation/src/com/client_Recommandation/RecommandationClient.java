package com.client_Recommandation;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;


public class RecommandationClient {  
	//sgroup 节点也就是存放推荐列表的节点，如果有新的推荐，那么Kafka的Customer程序就会向这个节点写入文件
    private String groupNode = "sgroup";  
    //zookeeper实例
    private ZooKeeper zk;   
  
    //这里watch就是用来监视Zookeepr Znode 变化的，所以正是因为Watch这个回调函数，才保证了zookeeper能实时监控
    public class myWatch implements Watcher {
    	
    	@Override
    	public void process(WatchedEvent event) {  
            // 如果发生了"/sgroup"节点下的子节点变化事件, 更新server列表, 并重新注册监听  
            if (event.getType() == EventType.NodeChildrenChanged   
                && ("/" + groupNode).equals(event.getPath())) {  
                try {  
                    updateServerList();  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        } 

    }
    
    /** 
     * 连接zookeeper 
     */  
    public void connectZookeeper() throws Exception {  
    	zk = new ZooKeeper("localhost:2181,localhost:2182,localhost:2183", 5000, new myWatch());
        updateServerList();  
    }  
  
    /*
     * 当我们发现Sgroup节点发生变化后，那么我们需要找到刚刚加入的节点，那刚刚加入的节点肯定是数值最大的节点，因为Customer里面节点是Sequence（自增长）的
     * 所以我们通过比较节点的大小可以获得最大节点，即现在我们要推荐的商品
     */
    private void updateServerList() throws Exception {   
        // 获取并监听groupNode的子节点变化  
        // watch参数为true, 表示监听子节点变化事件.   
        // 每次都需要重新注册监听, 因为一次注册, 只能监听一次事件, 如果还想继续保持监听, 必须重新注册  
    	if (zk.exists("/" + groupNode, true) != null) {
    		List<String> subList = zk.getChildren("/" + groupNode, true);
            SortedSet<RecommandNode> recommandes = new TreeSet<RecommandNode>();
            for (String string : subList) {
    			recommandes.add(new RecommandNode(string));
    		}
            System.out.println("现在的推荐商品： " + new String(zk.getData("/"+groupNode+"/"+recommandes.last().id, false, null)));
		}
        else {
			System.out.println("默认情况下推荐的商品为：a ");
		}
    }  
  
    /** 
     * client的工作逻辑写在这个方法中 
     * 此处不做任何处理, 只让client sleep，因为如果不sleep的话，程序很快就结束了
     */  
    public void handle() throws InterruptedException {  
        Thread.sleep(Long.MAX_VALUE);  
    }  
  
    public static void main(String[] args) throws Exception {  
        RecommandationClient ac = new RecommandationClient();  
        ac.connectZookeeper();  
        ac.handle();  
    }  
}  
