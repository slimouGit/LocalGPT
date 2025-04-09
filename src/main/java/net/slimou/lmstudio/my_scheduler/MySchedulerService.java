package net.slimou.lmstudio.my_scheduler;

import org.springframework.stereotype.Service;

@Service
public class MySchedulerService {
    public void doScheduledTask() {
        System.out.println("Service-Task wird ausgeführt: " + System.currentTimeMillis());
    }
}
