package com.spring.practice.slack_bot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class Scheduler {

    @Autowired
    SlackBotService slackBotService;

    // 일정 시작 스케줄러
    @Scheduled(cron = "0 0 21 * * MON-FRI")
    public void startSchedule() {
        String text = slackBotService.timeCheckAndSetText();
        String id = slackBotService.getConversationList();
        slackBotService.publishMessage(id, text);
    }

    // 일정 종료 스케줄러
    @Scheduled(cron = "0 0 22 * * MON-FRI")
    public void finishSchedule() {
        String text = slackBotService.timeCheckAndSetText();
        String id = slackBotService.getConversationList();
        slackBotService.publishMessage(id, text);
    }
}
