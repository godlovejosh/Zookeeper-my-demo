package test.java.util.concurrent.countdownlatch.my.demo;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

/**
 * 使用异步API获取子节点列表
 *
 * @author wuxing
 */
public class ZKDemo08 {
    public static void main(String[] args) throws Exception {
        final CountDownLatch connectedSignal = new CountDownLatch(1);
        final ZooKeeper zk = new ZooKeeper("127.0.0.1:2182", 5000, null);
        zk.register(new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == KeeperState.SyncConnected) {
                    if (event.getType() == EventType.None
                            && event.getPath() == null) {
                        connectedSignal.countDown();
                    } else if (event.getType() == EventType.NodeChildrenChanged) {
                        try {
                            System.out.println("NodeChildrenChanged.");
                            List<String> children = zk.getChildren(event.getPath(), true);
                            System.out.println("Children: " + children);
                        } catch (KeeperException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        connectedSignal.await();
        zk.create("/zk-huey", "root".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zk.create("/zk-huey/node1", "node1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zk.create("/zk-huey/node2", "node2".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zk.getChildren("/zk-huey",
                true,
                new AsyncCallback.Children2Callback() {
                    @Override
                    public void processResult(int rc, String path, Object ctx,
                                              List<String> children, Stat stat) {
                        System.out.println("ResultCode: " + rc);
                        System.out.println("ZNode: " + path);
                        System.out.println("Context: " + ctx);
                        System.out.println("Children: " + children);
                        System.out.println("Stat: " + stat);
                    }
                },
                "The Context"
        );
        zk.create("/zk-huey/node3", "node3".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        Thread.sleep(10 * 1000);
        zk.close();
    }
}