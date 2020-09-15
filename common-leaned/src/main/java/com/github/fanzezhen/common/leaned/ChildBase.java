package com.github.fanzezhen.common.leaned;

public class ChildBase extends Base {
    private String c = Base.testS("c-child");
    public ChildBase() {
        System.out.println("ChildBase");
    }
    public ChildBase(String s) {
        super("a", "b");
        System.out.println("ChildBase");
    }

    {
        System.out.println("I am a ChildBase");
    }

    static {
        System.out.println("child static");
    }
    public static void main(String[] args) {
//        System.out.println();
        new ChildBase("s");
    }
}
