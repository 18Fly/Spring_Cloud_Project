package org.project.feign.common;

//获取当前线程，可以进行一些信息保存操作，以便该线程后续操作
public class BaseContext {
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }
}
