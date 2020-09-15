package com.github.fanzezhen.common.leaned;

/**
 * 单例模式
 * 懒加载
 * 支持高并发
 */
public class Singleton {
    private Singleton() {
    }

    private static class SingletonHolder {
        private static final Singleton INSTANCE = new Singleton();
    }

    public static Singleton getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
