package com.example.tree.binary;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author ：Administrator
 * @description：TODO
 * @date ：2021/11/17 16:00
 */
public class LinkedBinaryTree implements BinaryTree {

    private Node root; //根节点

    public LinkedBinaryTree() {
    }

    public LinkedBinaryTree(Node root) {
        this.root = root;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public int size() {
        return size(root);
    }

    private int size(Node node) {
        if (ObjectUtils.isEmpty(node)) {
            return 0;
        }
        //左子树size
        int left = size(node.getLeftChild());
        //右子树size
        int right = size(node.getRightChild());
        //返回左子树、右子树size之和 并+1(根节点)
        return left + right + 1;
    }

    @Override
    public int getHeight() {
        return getHeight(root);
    }

    private int getHeight(Node node) {
        if (ObjectUtils.isEmpty(node)) {
            return 0;
        }
        //左子树高度
        int left = getHeight(node.getLeftChild());
        //右子树高度
        int right = getHeight(node.getRightChild());
        //返回左子树、右子树较大 并+1(根节点)
        return left > right ? left + 1 : right + 1;
    }

    @Override
    public Node findKey(Object value) {
        return findKey(value, root);
    }

    public Node findKey(Object value, Node node) {
        if (ObjectUtils.isEmpty(node)) {
            return null;
        }
        if (ObjectUtils.isNotEmpty(node) && node.getValue() == value) {
            return node;
        } else {
            Node left = findKey(value, node.getLeftChild());
            Node right = findKey(value, node.getRightChild());
            if (ObjectUtils.isNotEmpty(left) && left.getValue() == value) {
                return left;
            } else if (ObjectUtils.isNotEmpty(right) && right.getValue() == value) {
                return right;
            } else {
                return null;
            }
        }
    }

    @Override
    public void preOrderTraverse() {
        if (ObjectUtils.isEmpty(root)) {
            return;
        }
        //输出根节点
        System.out.print(root.getValue() + " ");
        //对左子树先序遍历
        //构建一个二叉树，根是左子树得根
        BinaryTree leftTree = new LinkedBinaryTree(root.getLeftChild());
        leftTree.preOrderTraverse();
        //对右子树先序遍历
        //构建一个二叉树，根是右子树得根
        BinaryTree rightTree = new LinkedBinaryTree(root.getRightChild());
        rightTree.preOrderTraverse();
    }

    @Override
    public void inOrderTraverse() {
        inOrderTraverse(root);
    }

    private void inOrderTraverse(Node node) {
        if (ObjectUtils.isEmpty(node)) {
            return;
        }
        //遍历左子树
        inOrderTraverse(node.getLeftChild());
        //输出根值
        System.out.print(node.getValue() + " ");
        //遍历右子树
        inOrderTraverse(node.getRightChild());
    }

    @Override
    public void postOrderTraverse() {
        postOrderTraverse(root);
    }

    private void postOrderTraverse(Node node) {
        if (ObjectUtils.isEmpty(node)) {
            return;
        }
        //遍历左子树
        postOrderTraverse(node.getLeftChild());
        //遍历右子树
        postOrderTraverse(node.getRightChild());
        //输出根值
        System.out.print(node.getValue() + " ");
    }

    @Override
    public void inOrderByStack() {
        //创建栈
        Deque<Node> stack = new LinkedList<>();
        Node current = root;
        while (ObjectUtils.isNotEmpty(current) || !stack.isEmpty()) {
            while (ObjectUtils.isNotEmpty(current)) {
                //进栈
                stack.push(current);
                current = current.getLeftChild();
            }
            if (!stack.isEmpty()) {
                //弹栈
                current = stack.pop();
                System.out.print(current.getValue() + " ");
                current = current.getRightChild();
            }
        }
    }

    @Override
    public void preOrderByStack() {

    }

    @Override
    public void postOrderByStack() {

    }

    @Override
    public void levelOrderByStack() {
        if (ObjectUtils.isEmpty(root)) return;
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        while (queue.size() != 0) {
            for (int i = 0; i < queue.size(); i++) {
                //获取并移除该队列头部元素，如果该队列为空返回null
                Node temp = queue.poll();
                System.out.print(temp.getValue() + " ");
                if (ObjectUtils.isNotEmpty(temp.getLeftChild())) queue.add(temp.getLeftChild());
                if (ObjectUtils.isNotEmpty(temp.getRightChild())) queue.add(temp.getRightChild());
            }
        }
    }
}
