package com.github.fanzezhen.common.core.model.tree;

public class Node<T extends Comparable<T>> {
    public T data;
    public Node<T> left;
    public Node<T> right;

    public Node (T data){
        this.data = data;
    }
}
