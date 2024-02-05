package com.github.fanzezhen.common.core.model.tree;

/**
 * @author zezhen.fan
 */
public class BinaryNode<T extends Comparable<T>> {
    protected T data;
    protected BinaryNode<T> left;
    protected BinaryNode<T> right;

    public BinaryNode(T data){
        this.data = data;
    }
}
