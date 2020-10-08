package com.github.fanzezhen.common.leaned;

/**
 * @author fanzezhen
 */
public class Base {
    String a = testS("a-base");
    String b;
    public Base() {
        System.out.println("Base");
    }

    public Base(String a, String b) {
        this.a = a;
        this.b = b;
        System.out.println("Base");
    }

    public static String testS(String s){
        System.out.println(s);
        return s;
    }

    {
        System.out.println("I am a Base");
    }

    static {
        System.out.println("base static");
    }
}
