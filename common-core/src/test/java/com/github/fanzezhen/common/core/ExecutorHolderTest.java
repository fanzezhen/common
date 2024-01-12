package com.github.fanzezhen.common.core;

import com.github.fanzezhen.common.core.thread.ExecutorHolder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author zezhen.fan
 */
@Slf4j
@Ignore
public class ExecutorHolderTest {
    @Test
    @Ignore
    public void test() throws InterruptedException {
        ExecutorHolder<Object> executorHolder = ExecutorHolder.create().addTask(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("1");
        }).addTask(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("2");
        }).addTask(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("3");
        }).addTask(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("4");
        }).addTask(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("5");
        });
        Thread.sleep(1000);
        System.out.println("6");
        executorHolder.get();
        executorHolder.addTask(() -> {
            System.out.println("7");
            return 7;
        });
        System.out.println(executorHolder.get());
        Assert.assertNotNull(executorHolder.get());
    }
}
