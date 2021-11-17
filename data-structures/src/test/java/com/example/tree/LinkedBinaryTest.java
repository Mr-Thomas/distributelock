package com.example.tree;

import com.example.tree.binary.BinaryTree;
import com.example.tree.binary.LinkedBinaryTree;
import com.example.tree.binary.Node;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author ：Administrator
 * @description：二叉树的基本实现demo
 * @date ：2021/11/17 16:01
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DateStructuresApplication.class)
public class LinkedBinaryTest {

    @Test
    public void test() {
        //创建二叉树
        Node node5 = new Node(5, null, null);
        Node node4 = new Node(4, null, node5);
        Node node3 = new Node(3, null, null);
        Node node7 = new Node(7, null, null);
        Node node6 = new Node(6, null, node7);
        Node node2 = new Node(2, node3, node6);
        Node node1 = new Node(1, node4, node2);
        BinaryTree btree = new LinkedBinaryTree(node1);
        //判断二叉树是否为空
        System.out.println(btree.isEmpty());

        //先序递归遍历 1452367
        System.out.println("先序递归遍历");
        btree.preOrderTraverse();

        //中序递归遍历 4513267
        System.out.println("\n中序递归遍历");
        btree.inOrderTraverse();

        //后序递归遍历 5437621
        System.out.println("\n后序递归遍历");
        btree.postOrderTraverse();

        //中序非递归遍历【借助栈】
        System.out.println("\n中序非递归遍历");
        btree.inOrderByStack();

        //按照层次遍历【借助队列】
        System.out.println("\n按照层次遍历");
        btree.levelOrderByStack();

        //二叉树中查找某个值的节点
        System.out.println("\n二叉树中查找某个值的节点:" + btree.findKey(1));

        //二叉树的高度
        System.out.println("\n二叉树的高度:" + btree.getHeight());

        //二叉树的节点数据量
        System.out.println("\n二叉树的节点数据量:" + btree.size());
    }
}
