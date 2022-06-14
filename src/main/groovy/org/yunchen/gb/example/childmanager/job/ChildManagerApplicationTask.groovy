package org.yunchen.gb.example.childmanager.job;

import org.springframework.beans.factory.annotation.Configurable
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
@Configurable
@EnableScheduling
public class ChildManagerApplicationTask {

    @Scheduled(fixedRate = 10000L)
    public void run1() {
        // println ("Scheduling Tasks Examples: The time is now ${new Date().format('yyyy-MM-dd')}");
    }

    //每1分钟执行一次
    @Scheduled(cron = "0 */1 *  * * * ")
    public void run2() {
        // println ("Scheduling Tasks Examples By Cron: The time is now ${new Date().format('yyyy-MM-dd')}");
    }

}
