package net.slimou.lmstudio.my_scheduler;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class MySchedulerChecker {

    @Value("${scheduler.enabled}")
    private boolean schedulerEnabled;

}
