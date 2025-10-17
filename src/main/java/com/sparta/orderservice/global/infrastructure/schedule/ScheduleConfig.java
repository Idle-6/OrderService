package com.sparta.orderservice.global.infrastructure.schedule;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
public class ScheduleConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 스케줄러 전용 쓰레드풀 생성
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5); // 동시에 5개 스케줄 실행 가능
        taskScheduler.setThreadNamePrefix("scheduler-thread-");
        taskScheduler.initialize();

        // 등록
        taskRegistrar.setTaskScheduler(taskScheduler);
    }
}
