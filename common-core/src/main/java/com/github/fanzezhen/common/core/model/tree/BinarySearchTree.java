package com.github.fanzezhen.common.core.model.tree;

public class BinarySearchTree<T extends Comparable<T>> {
    public Node<T> root;

    public boolean insert(T data) {
        if (data == null) {
            return false;
        }
        if (root == null) {
            root = new Node<>(data);
        }
        Node<T> cur = root;
        while (true) {
            if (data.compareTo(cur.data) < 0) {
                if (cur.left == null) {
                    cur.left = new Node<>(data);
                    return true;
                }
                cur = cur.left;
            } else if (data.compareTo(cur.data) > 0) {
                if (cur.right == null) {
                    cur.right = new Node<>(data);
                    return true;
                }
                cur = cur.right;
            } else {
                return false;
            }
        }
    }

    public boolean contains(T data) {
        if (root == null || data == null) {
            return false;
        }
        Node<T> cur = root;
        while (true) {
            if (data.compareTo(cur.data) < 0) {
                cur = cur.left;
            } else if (data.compareTo(cur.data) > 0) {
                cur = cur.right;
            } else {
                return true;
            }
            if (cur == null) {
                return false;
            }
        }
    }
}
