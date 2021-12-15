package com.github.fanzezhen.common.leaned;

import cn.hutool.core.thread.ThreadUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author fanzezhen
 */
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

    static class NameTreadFactory implements ThreadFactory {

        private final AtomicInteger mThreadNum = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread t = new Thread(r, "my-thread-" + mThreadNum.getAndIncrement());
            System.out.println(t.getName() + " has been created");
            return t;
        }
    }

    public static class MyIgnorePolicy implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            doLog(r, e);
        }

        private void doLog(Runnable r, ThreadPoolExecutor e) {
            // 可做日志记录等
            System.err.println(r.toString() + " rejected");
//          System.out.println("completedTaskCount: " + e.getCompletedTaskCount());
        }
    }

    public static void main(String[] args) {
        SynchronousQueue<Runnable> queue = new SynchronousQueue<Runnable>();
        ThreadFactory threadFactory = new NameTreadFactory();
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, queue, threadFactory);
        int l = 10;
        for (int i = 0; i < l; i++) {
            TestThread t = new TestThread(i);
            threadPool.execute(t);
            System.out.println("线程池中活跃的线程数： " + threadPool.getPoolSize());
            if (queue.size() > 0) {
                System.out.println("队列中阻塞的线程数" + queue.size());
            }
        }
        threadPool.shutdown();
        ThreadPoolExecutor threadPoolExecutor = ThreadUtil.newExecutor(5, 10);
        for (int i = 0; i < l; i++) {
            int finalI = i;
            threadPoolExecutor.execute(()-> {
                ThreadUtil.sleep(1000);
                System.out.println("test-" + finalI);
            });
            System.out.println("getQueueSize:" + threadPoolExecutor.getQueue().size());
            System.out.println("getActiveCount:" + threadPoolExecutor.getActiveCount());
        }
        threadPoolExecutor.shutdown();
    }
}
