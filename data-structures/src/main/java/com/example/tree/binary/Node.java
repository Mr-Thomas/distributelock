package com.example.tree.binary;

import lombok.Data;

/**
 * @author ：Administrator
 * @description：Node 二叉链表的节点
 * @date ：2021/11/17 15:55
 */
@Data
public class Node {
    //节点值
    private Object value;
    //左子树引用
    private Node leftChild;
    //右子树引用
    private Node rightChild;

    public Node(Object value) {
        this.value = value;
    }

    public Node(Object value, Node leftChild, Node rightChild) {
        this.value = value;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }
}
