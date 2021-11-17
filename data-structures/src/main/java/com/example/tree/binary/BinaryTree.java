package com.example.tree.binary;

/**
 * @author ：Administrator
 * @description：TODO二叉树接口
 * @date ：2021/11/17 15:42
 */
public interface BinaryTree {
    /**
     * 是否为空树
     *
     * @return
     */
    boolean isEmpty();

    /**
     * 树节点数量
     *
     * @return
     */
    int size();

    /**
     * 获取二叉树的高度
     *
     * @return
     */
    int getHeight();

    /**
     * 查询指定值的节点
     *
     * @param value
     * @return
     */
    Node findKey(Object value);

    /**
     * 前序遍历递归操作 DLR 根 左子树 右子树
     */
    void preOrderTraverse();

    /**
     * 中序遍历递归操作 LDR 左子树 根 右子树
     */
    void inOrderTraverse();

    /**
     * 后续遍历递归操作 LRD 左子树 右子树 根
     */
    void postOrderTraverse();

    /**
     * 中序遍历非递归操作
     */
    void inOrderByStack();

    /**
     * 前序遍历非递归操作
     */
    void preOrderByStack();

    /**
     * 后序遍历非递归操作
     */
    void postOrderByStack();

    /**
     * 按照层次遍历二叉树
     */
    void levelOrderByStack();
}
