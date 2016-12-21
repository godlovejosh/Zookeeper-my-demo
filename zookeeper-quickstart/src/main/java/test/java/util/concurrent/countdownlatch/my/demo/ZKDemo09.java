package test.java.util.concurrent.countdownlatch.my.demo;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;

/**
 * 使用同步API获取节点数据内容
 *
 * @author wuxing
 */
public class ZKDemo09 {
    public static void main(String[] args) throws Exception {
        final CountDownLatch connectedSignal = new CountDownLatch(1);
        final ZooKeeper zk = new ZooKeeper("127.0.0.1:2182", 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == KeeperState.SyncConnected) {
                    if (event.getType() == EventType.None
                            && event.getPath() == null) {
                        connectedSignal.countDown();
                    }
                }
            }
        });
        connectedSignal.await();
        zk.create("/zk-huey", "hello".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        byte[] nodeData = zk.getData("/zk-huey", false, null);
        System.out.println("NodeData: " + new String(nodeData));
        zk.close();
    }
}