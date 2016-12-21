package test.java.util.concurrent.countdownlatch.my.demo;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * 创建一个基本的ZooKeeper会话实例
 *
 * @author wuxing
 */
public class ZKDemo01 {
    public static void main(String[] args) throws Exception {
        final CountDownLatch connectedSignal = new CountDownLatch(1);
        /**
         * ZooKeeper客户端和服务器会话的建立是一个异步的过程
         * 构造函数在处理完客户端的初始化工作后立即返回，在大多数情况下，并没有真正地建立好会话
         * 当会话真正创建完毕后，Zookeeper服务器会向客户端发送一个事件通知
         */
        ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == KeeperState.SyncConnected) {
                    connectedSignal.countDown();
                }
            }
        });
        System.out.println("State1: " + zk.getState());    // CONNECTING

        connectedSignal.await();
        System.out.println("State2: " + zk.getState());    // CONNECTED

        zk.close();
        System.out.println("State3: " + zk.getState());    // CLOSED
    }
}
