package test.java.util.concurrent.countdownlatch.my.demo;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

/**
 * ZooKeeper权限控制
 *
 * @author wuxing
 */
public class ZKDemo15 {
    public static void main(String[] args) throws Exception {
        /**
         * 使用含有权限信息的zookeeper会话创建数据节点
         */
        final CountDownLatch connectedSignal = new CountDownLatch(1);
        ZooKeeper zk = new ZooKeeper("127.0.0.1:2182", 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                connectedSignal.countDown();
            }
        });
        connectedSignal.await();
        zk.addAuthInfo("digest", "huey:123".getBytes());
        zk.create("/zk-huey", "hello".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
        zk.close();
        /**
         * 使用无权限信息的zookeeper会话访问含有权限信息的数据节点
         */
        try {
            final CountDownLatch signal = new CountDownLatch(1);
            ZooKeeper zk1 = new ZooKeeper("127.0.0.1:2182", 5000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    signal.countDown();
                }
            });
            signal.await();
            zk1.getData("/zk-huey", false, null);
            System.out.println("NodeData: " + new String(zk1.getData("/zk-huey", false, null)));
            zk1.close();
        } catch (Exception e) {
            System.out.println("Failed to delete Znode: " + e.getMessage());
        }
        /**
         * 使用错误权限信息的zookeeper会话访问含有权限信息的数据节点
         */
        try {
            final CountDownLatch signal = new CountDownLatch(1);
            ZooKeeper zk2 = new ZooKeeper("127.0.0.1:2182", 5000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    signal.countDown();
                }
            });
            signal.await();
            zk2.addAuthInfo("digest", "huey:abc".getBytes());
            System.out.println("NodeData: " + new String(zk2.getData("/zk-huey", false, null)));
            zk2.close();
        } catch (Exception e) {
            System.out.println("Failed to delete Znode: " + e.getMessage());
        }
        /**
         * 使用正确权限信息的zookeeper会话访问含有权限信息的数据节点
         */
        try {
            final CountDownLatch signal = new CountDownLatch(1);
            ZooKeeper zk3 = new ZooKeeper("127.0.0.1:2182", 5000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    signal.countDown();
                }
            });
            signal.await();
            zk3.addAuthInfo("digest", "huey:123".getBytes());
            System.out.println("NodeData: " + new String(zk3.getData("/zk-huey", false, null)));
            zk3.close();
        } catch (Exception e) {
            System.out.println("Failed to delete Znode: " + e.getMessage());
        }
    }
}