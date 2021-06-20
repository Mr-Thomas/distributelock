package com.example.distributelock.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/6/20 11:55
 * zookeeper瞬时节点实现分布式锁
 */
@Slf4j
@Component
public class ZookeeperUtil implements AutoCloseable, Watcher {

    private ZooKeeper zooKeeper;
    private String zkNode;

    public ZookeeperUtil() throws Exception {
        this.zooKeeper = new ZooKeeper("127.0.0.1:2181", 10000, this);
    }

    public Boolean lock(String node) {
        try {
            createNode(node);
            log.info("zkNode:{}", zkNode);
            //获取所有子节点
            List<String> children = zooKeeper.getChildren("/" + node, false);
            //节点排序
            Collections.sort(children);
            //获取序号最小【第一个】的子节点
            String firstNode = children.get(0);
            //如果创建的节点是第一个子节点，则获得锁
            if (zkNode.endsWith(firstNode)) {
                log.info("获得锁线程：{} ,节点：{}", Thread.currentThread().getName(), firstNode);
                return true;
            }
            //如果不是第一个子节点，则监听前一个节点,监听到了，则跳出循环
            String lastNode = firstNode;
            for (String child : children) {
                if (zkNode.endsWith(child)) {
                    zooKeeper.exists("/" + node + "/" + lastNode, true);
                    log.info("获得锁线程：{} ,节点：{}", Thread.currentThread().getName(), child);
                    break;
                } else {
                    lastNode = child;
                }
            }
            //等待监听收到通知，唤起线程，唤起后，意味着当前线程拿到锁
            synchronized (this) {
                wait();
            }
            return true;
        } catch (Exception e) {
            log.error("lock error", e);
        }
        return false;
    }

    public void createNode(String node) {
        try {
            Stat exists = zooKeeper.exists("/" + node, false);
            if (ObjectUtils.isEmpty(exists)) {
                /**
                 * 创建业务根节点
                 * 1、节点路径
                 * 2、节点初始化内容
                 * 3、节点权限【不需要账号密码就可以连接zookeeper】
                 * 4、节点模式【瞬时节点、瞬时有序、持久、持久有序】
                 */
                zooKeeper.create("/" + node, node.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            //创建瞬时有序节点 【/order/order_0000000012】
            zkNode = zooKeeper.create("/" + node + "/" + node + "_", node.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (Exception e) {
            log.error("createNode error", e);
        }
    }

    /**
     * 实现Watcher【zk观察器】
     *
     * @param watchedEvent
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        //当节点被删除的时候，zk观察器监听到后，唤起等待线程
        if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
            synchronized (this) {
                notify();
            }
        }
    }

    /**
     * 关闭资源连接
     *
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        //-1:是所有的版本 获得锁的瞬时节点执行完逻辑后，删除该节点； zk观察器监听到后，唤起等待线程
        try {
            zooKeeper.delete(zkNode, -1);
            zooKeeper.close();
            log.info("释放zk锁:{}", zkNode);
        } catch (Exception e) {
            log.error("释放锁异常：", e);
        }
    }
}
