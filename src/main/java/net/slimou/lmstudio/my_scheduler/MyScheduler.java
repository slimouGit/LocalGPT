package net.slimou.lmstudio.my_scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MyScheduler {

    private final MySchedulerService myService;

    @Value("${scheduler.enabled}")
    private boolean schedulerEnabled;

    @Value("${scheduler.fixedRate}")
    private long fixedRate;

    public MyScheduler(MySchedulerService myService) {
        this.myService = myService;
    }

    @Scheduled(fixedRateString = "${scheduler.fixedRate}")
    public void scheduleTask() {
        if (schedulerEnabled) {
            myService.doScheduledTask();
        } else {
            System.out.println("Scheduler ist deaktiviert.");
        }
    }
}
