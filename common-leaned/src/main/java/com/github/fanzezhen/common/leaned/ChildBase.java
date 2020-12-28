package com.github.fanzezhen.common.leaned;

/**
 * @author zezhen.fan
 */
public class ChildBase extends Base {
    private final String c = Base.testS("c-child");
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
        new ChildBase("s");
    }
}
