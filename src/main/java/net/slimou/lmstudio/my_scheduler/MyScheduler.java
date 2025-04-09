package net.slimou.lmstudio.my_scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MyScheduler {

    private final MySchedulerService myService;
    private final MySchedulerChecker mySchedulerChecker;

    public MyScheduler(MySchedulerService myService, MySchedulerChecker mySchedulerChecker) {
        this.myService = myService;
        this.mySchedulerChecker = mySchedulerChecker;
    }

    private boolean shouldExecuteScheduledTask() {
        return mySchedulerChecker.isSchedulerEnabled();
    }


    @Scheduled(fixedRateString = "${scheduler.fixedRate}")
    public void scheduleTask() {
        if (shouldExecuteScheduledTask()) {
            myService.doScheduledTask();
        } else {
            System.out.println("Scheduler ist deaktiviert.");
        }
    }
}
