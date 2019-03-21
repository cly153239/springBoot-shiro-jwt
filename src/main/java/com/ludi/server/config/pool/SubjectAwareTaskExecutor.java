package com.ludi.server.config.pool;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author 陆迪
 * @date 2018-12-05 20:46
 * 线程池复用，传递Subject，以便随处获取当前线程用户
 **/
public class SubjectAwareTaskExecutor extends ThreadPoolTaskExecutor {

    @Override
    public void execute(final Runnable aTask) {
        logger.debug("execute(final Runnable aTask)");
        final Subject currentSubject = ThreadContext.getSubject();
        if (currentSubject != null) {
            super.execute(currentSubject.associateWith(aTask));
        } else {
            super.execute(aTask);
        }
    }


    @Override
    public void execute(Runnable task, long startTimeout) {
        logger.debug("execute(Runnable task, long startTimeout)");
        final Subject currentSubject = ThreadContext.getSubject();
        if (currentSubject != null) {
            super.execute(currentSubject.associateWith(task), startTimeout);
        } else {
            super.execute(task, startTimeout);
        }

    }

    @Override
    public Future<?> submit(Runnable task) {
        logger.debug("Future<?> submit(Runnable task)");
        final Subject currentSubject = ThreadContext.getSubject();
        if (currentSubject != null) {
            return super.submit(currentSubject.associateWith(task));
        } else {
            return super.submit(task);
        }
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        logger.debug(" <T> Future<T> submit(Callable<T> task)");
        final Subject currentSubject = ThreadContext.getSubject();
        if (currentSubject != null) {
            //返回一个封装过的Runnable，然后用户可以选择开启一个新线程执行
            return super.submit(currentSubject.associateWith(task));
        } else {
            return super.submit(task);
        }
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        logger.debug("ListenableFuture<?> submitListenable(Runnable task)");
        final Subject currentSubject = ThreadContext.getSubject();
        if (currentSubject != null) {
            return super.submitListenable(task);
        } else {
            return super.submitListenable(task);
        }
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        logger.debug("<T> ListenableFuture<T> submitListenable(Callable<T> task)");
        final Subject currentSubject = ThreadContext.getSubject();
        if (currentSubject != null) {
            return super.submitListenable(currentSubject.associateWith(task));
        } else {
            return super.submitListenable(task);
        }
    }
}
