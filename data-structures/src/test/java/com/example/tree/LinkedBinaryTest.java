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

    /**
     * 二叉树基本实现
     */
    @Test
    public void BinaryTest() {
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

    /**
     * 顺序查找
     * 存储结构：可以是顺序表，也可以是链表
     * 时间复杂度：T(n)=O(n)
     * 空间复杂度：S(n)=O(1)
     */
    @Test
    public void order2findTest() {
        int[] scoreArr = {22, 44, 33, 88, 66, 99, 100};
        int search = order2find(scoreArr, 33);
        if (search == -1) {
            System.out.println("分数不存在");
        } else {
            System.out.println("分数的索引：" + search);
        }
    }

    private int order2find(int[] arr, int score) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == score) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 前提：1、顺序存储结构；2、按照关键字有序
     * 折半查找【折半二分查找】
     * 时间复杂度：T(n)=O(log2n)
     */
    @Test
    public void binarySearchTest() {
        int[] scoreArr = {22, 33, 44, 55, 66, 77, 88};
        //不使用递归
        int search = binarySearch(scoreArr, 88);
        //使用递归
        int searchRecursive = binarySearchRecursive(scoreArr, 88);
        if (search == -1) {
            System.out.println("分数不存在");
        } else {
            System.out.println("分数的索引：" + search);
        }
    }

    /**
     * 使用递归
     * 时间复杂度：T(n)=O(log2n)
     * 空间复杂度：S(n)=O(log2n)
     *
     * @param arr
     * @param score
     * @return
     */
    private int binarySearchRecursive(int[] arr, int score) {
        //指定low、high
        int low = 0;
        int high = arr.length - 1;
        return binarySearchRecursive(arr, score, low, high);
    }

    private int binarySearchRecursive(int[] arr, int score, int low, int high) {
        //递归结束条件
        if (low > high) {
            return -1;
        }
        int mid = (low + high) / 2;
        if (score == arr[mid]) {
            return mid;
        } else if (score < arr[mid]) {
            return binarySearchRecursive(arr, score, low, mid - 1);
        } else {
            return binarySearchRecursive(arr, score, mid + 1, high);
        }
    }

    /**
     * 不使用递归
     * 时间复杂度：T(n)=O(log2n)
     * 空间复杂度：S(n)=O(1)
     *
     * @param arr
     * @param score
     * @return
     */
    private int binarySearch(int[] arr, int score) {
        //指定low、high
        int low = 0;
        int high = arr.length - 1;
        //折半查找
        while (low <= high) {
            //取中间位置
            int mid = (low + high) / 2;
            if (score == arr[mid]) {
                return mid;
            } else if (score < arr[mid]) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return -1;
    }
}
