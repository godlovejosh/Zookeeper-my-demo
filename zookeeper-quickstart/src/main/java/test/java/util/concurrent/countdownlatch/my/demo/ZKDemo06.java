package test.java.util.concurrent.countdownlatch.my.demo;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * 使用异步API删除一个ZNode节点
 *
 * @author wuxing
 */
public class ZKDemo06 {
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
        zk.delete(
                "/zk-huey",
                -1,
                new AsyncCallback.VoidCallback() {
                    @Override
                    public void processResult(int rc, String path, Object ctx) {
                        System.out.println("ResultCode: " + rc);
                        System.out.println("Znode: " + path);
                        System.out.println("Context: " + (String) ctx);
                    }
                },
                "The Context"
        );
        zk.close();
    }
}