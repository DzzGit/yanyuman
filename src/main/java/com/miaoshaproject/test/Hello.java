package com.miaoshaproject.test;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
//@EnableScheduling
public class Hello {

    @Scheduled(cron = "0/1 * * * * ?")
    public void aa(){
        System.out.print("开始启动啦~");
    }

}
