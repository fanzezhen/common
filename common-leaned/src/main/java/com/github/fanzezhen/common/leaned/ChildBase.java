package com.github.fanzezhen.common.leaned;

public class ChildBase extends Base {
    public ChildBase() {
        System.out.println("ChildBase");
    }
    public ChildBase(String s) {
        super("a", "b");
        System.out.println(s);
    }

    public static void main(String[] args) {
        new ChildBase("a");
    }
}
