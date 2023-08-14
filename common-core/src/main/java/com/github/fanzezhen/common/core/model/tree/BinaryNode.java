package com.github.fanzezhen.common.core.model.tree;

/**
 * @author zezhen.fan
 */
public class BinaryNode<T extends Comparable<T>> {
    public T data;
    public BinaryNode<T> left;
    public BinaryNode<T> right;

    public BinaryNode(T data){
        this.data = data;
    }
}
