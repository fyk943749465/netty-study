package com.feng.netty.future;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class TestJdkFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("模拟执行计算");
                Thread.sleep(1000);
                return 50;
            }
        });

        log.debug("等待结果");
        // get 方法是阻塞方法，等待线程执行结果的返回
        log.debug("结果是{}", future.get());
    }
}
