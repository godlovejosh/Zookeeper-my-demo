package test.java.util.concurrent.countdownlatch.my.demo;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * 创建一个复用sessionId和sessionPasswd的Zookeeper对象示例
 *
 * @author wuxing
 */
public class ZKDemo02 {
    public static void main(String[] args) throws Exception {
        final CountDownLatch connectedSignal = new CountDownLatch(1);
        ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == KeeperState.SyncConnected) {
                    connectedSignal.countDown();
                }
            }
        });
        connectedSignal.await();
        long sessionId = zk.getSessionId();
        byte[] passwd = zk.getSessionPasswd();
        zk.close();
        final CountDownLatch anotherConnectedSignal = new CountDownLatch(1);
        ZooKeeper newZk = new ZooKeeper(
                "127.0.0.1:2181",
                5000,
                new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        if (event.getState() == KeeperState.SyncConnected) {
                            anotherConnectedSignal.countDown();
                        }
                    }
                },
                sessionId,
                passwd);
        anotherConnectedSignal.await();
        newZk.close();
    }
}