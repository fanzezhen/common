package com.github.fanzezhen.common.core.thread;

import cn.hutool.core.map.CaseInsensitiveMap;
import com.alibaba.fastjson.JSONObject;
import com.github.fanzezhen.common.core.context.SysContextHolder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author zezhen.fan
 */
public class CommonThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    public CommonThreadPoolTaskExecutor() {
        super();
    }

    @Override
    public void execute(final Runnable task) {
        JSONObject contextMap = SysContextHolder.getContextMap();
        RunProxy wrapper = new RunProxy(contextMap, task);
        super.execute(wrapper);
    }

    @Override
    public Future<?> submit(final Runnable task) {
        JSONObject contextMap = SysContextHolder.getContextMap();
        RunProxy wrapper = new RunProxy(contextMap, task);
        return super.submit(wrapper);
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        JSONObject contextMap = SysContextHolder.getContextMap();
        CallProxy<T> wrapper = new CallProxy<>(contextMap, task);
        return super.submit(wrapper);
    }

    private static class CallProxy<T> implements Callable<T> {
        private JSONObject contextMap;
        private final Callable<T> caller;

        public CallProxy(JSONObject contextMap, Callable<T> caller) {
            if (contextMap != null) {
                this.contextMap = new JSONObject(new CaseInsensitiveMap<>());
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
        private JSONObject contextMap;
        private final Runnable runner;

        public RunProxy(JSONObject contextMap, Runnable runner) {
            if (contextMap != null) {
                this.contextMap = new JSONObject(new CaseInsensitiveMap<>());
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
