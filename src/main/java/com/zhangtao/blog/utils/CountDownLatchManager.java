package com.zhangtao.blog.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 管理CountDownLatch
 * 获取
 * 删除
 */
@Component
public class CountDownLatchManager {
    Map<String, CountDownLatch> latchMap = new HashMap<>();


    public void onLoginStateChange(String loginId){
        CountDownLatch countDownLatch = latchMap.get(loginId);
        if (countDownLatch!= null) {
            countDownLatch.countDown();
        }
    }


    public CountDownLatch getLatch(String loginId){
        CountDownLatch countDownLatch = latchMap.get(loginId);
        if (countDownLatch == null) {
            countDownLatch = new CountDownLatch(1);
            latchMap.put(loginId, countDownLatch);
        }
        return countDownLatch;
    }

    public void deleteLatch(String loginId){
        latchMap.remove(loginId);
    }
}
