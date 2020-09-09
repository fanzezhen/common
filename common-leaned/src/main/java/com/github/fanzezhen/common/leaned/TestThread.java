package com.github.fanzezhen.common.leaned;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestThread implements Runnable {
    private Integer num;

    @Override
    public void run() {
        System.out.println(num);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SynchronousQueue<Runnable> queue = new SynchronousQueue<Runnable>();
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, queue);
        for (int i = 0; i < 10; i++) {
            threadPool.execute(
                    new Thread(new TestThread(i), "Thread".concat(i + "")));
            System.out.println("线程池中活跃的线程数： " + threadPool.getPoolSize());
            if (queue.size() > 0) {
                System.out.println("队列中阻塞的线程数" + queue.size());
            }
        }
        threadPool.shutdown();
    }
}
