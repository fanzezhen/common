package com.github.fanzezhen.common.core.thread;

import cn.hutool.core.map.CaseInsensitiveMap;
import com.github.fanzezhen.common.core.context.SysContextHolder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author zezhen.fan
 * @date 2023/8/14
 */
public class CommonThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    public CommonThreadPoolTaskExecutor() {
    }

    public void execute(final Runnable task) {
        Map<String, String> contextMap = SysContextHolder.getContextMap();
        RunProxy wrapper = new RunProxy(contextMap, task);
        super.execute(wrapper);
    }

    public Future<?> submit(final Runnable task) {
        Map<String, String> contextMap = SysContextHolder.getContextMap();
        RunProxy wrapper = new RunProxy(contextMap, task);
        return super.submit(wrapper);
    }

    public <T> Future<T> submit(final Callable<T> task) {
        Map<String, String> contextMap = SysContextHolder.getContextMap();
        CallProxy<T> wrapper = new CallProxy<>(contextMap, task);
        return super.submit(wrapper);
    }

    private static class CallProxy<T> implements Callable<T> {
        private Map<String, String> contextMap;
        private Callable<T> caller;

        public CallProxy(Map<String, String> contextMap, Callable<T> caller) {
            if (contextMap != null) {
                this.contextMap = new CaseInsensitiveMap<>();
                this.contextMap.putAll(contextMap);
            }

            this.caller = caller;
        }

        public T call() throws Exception {
            T var1;
            try {
                SysContextHolder.setContextMap(this.contextMap);
                var1 = this.caller.call();
            } finally {
                SysContextHolder.clean();
            }

            return var1;
        }
    }

    private static class RunProxy implements Runnable {
        private Map<String, String> contextMap;
        private Runnable runner;

        public RunProxy(Map<String, String> contextMap, Runnable runner) {
            if (contextMap != null) {
                this.contextMap = new CaseInsensitiveMap<>();
                this.contextMap.putAll(contextMap);
            }

            this.runner = runner;
        }

        public void run() {
            try {
                SysContextHolder.setContextMap(this.contextMap);
                this.runner.run();
            } finally {
                SysContextHolder.clean();
            }

        }
    }
}
