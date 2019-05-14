package com.thorough.core.modules;

import com.alibaba.fastjson.JSONObject;
import com.thorough.core.modules.pathology.model.vo.ImageVo;
import com.thorough.library.utils.DateUtils;
import com.thorough.library.utils.FileUtils;
import org.apache.tomcat.util.threads.LimitLatch;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by chenlinsong on 2018/11/11.
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {
        int  corePoolSize  =    2;
        int  maxPoolSize   =   5;
        long keepAliveTime = 1;
        LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue<Runnable>(10);
////        linkedBlockingQueue.put("aa1");
////        System.out.println(linkedBlockingQueue.take());
////        linkedBlockingQueue.put("aa2");
////        System.out.println(linkedBlockingQueue.take());
////        linkedBlockingQueue.put("aa3");
////        System.out.println(linkedBlockingQueue.take());
////        linkedBlockingQueue.put("aa4");
////        System.out.println(linkedBlockingQueue.take());
//
////        System.out.println(linkedBlockingQueue.poll());
////        System.out.println(linkedBlockingQueue.size());
        ExecutorService threadPool =
                new ThreadPoolExecutor(
                        corePoolSize,
                        maxPoolSize,
                        keepAliveTime,
                        TimeUnit.MILLISECONDS,
                        linkedBlockingQueue,new RejectedExecutionHandler(){
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        System.err.println("超过:"+Thread.currentThread().getName());
                    }
                }
                );
//
        ConcurrentLinkedDeque<Integer> linkedDeque = new ConcurrentLinkedDeque();
        for (int i=0;i < 10;i++){
            final int j = i;

            threadPool.execute(new Thread("name** "+j){
                public void run() {
                    System.out.println(Thread.currentThread().getName()+" &&"+j);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        threadPool.shutdown();


//
//        threadPool.shutdown();
//        while (!threadPool.isTerminated()){
//            Thread.sleep(1000);
//        }
//        Iterator iterator = linkedDeque.iterator();
//        while (iterator.hasNext()){
//            int value = (int) iterator.next();
//            linkedDeque.remove(value);
//        }
//        System.out.println(linkedDeque.size());


//        LimitLatch limitLatch = new LimitLatch(20);
//
//        new Thread(){
//            public void run(){
//                while (true){
//                    //System.out.println("linkedBlockingQueuelist:"+linkedBlockingQueue.size());
//                    //System.out.println("limitLatchsize: "+limitLatch.getQueuedThreads().size()+" "+ DateUtils.getDateTime());
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();
//        for(int i=0 ;i < 10000;i++){
//            final int k = i;
//            limitLatch.countUpOrAwait();
//            System.out.println("---:"+i);
//            threadPool.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        System.out.println("+++"+k);
//                        Thread.sleep(2000);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }finally {
//                        limitLatch.countDown();
//                    }
//                }
//            });
////            try {
////                limitLatch.countUpOrAwait();
////                System.out.println("a"+k);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }finally {
//////                limitLatch.countDown();
////            }
//        }

//

    }
}
