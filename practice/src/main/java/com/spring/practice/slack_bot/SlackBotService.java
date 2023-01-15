package com.spring.practice.slack_bot;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class SlackBotService {

    // Get token from application.properties
    @Value("${slack.bot.token}")
    private String botToken;

    // Get channel id from Slack
    public String getConversationList() {
        String url = "https://slack.com/api/conversations.list?pretty=1";
        System.out.println(botToken);

        ResponseEntity<String> responseEntity = httpResponse(url, botToken, HttpMethod.GET);
        System.out.println(responseEntity.getBody());
        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        System.out.println(jsonObject);
        JSONArray items = jsonObject.getJSONArray("channels");

        String id = null;

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String name = item.getString("name");

            if (name.equals("일정-공유")) {
                id = item.getString("id");
            }
        }

        System.out.println(id);
        return id;
    }

    // Send message to Slack
    public void publishMessage(String id, String text) {
        String url = "https://slack.com/api/chat.postMessage?channel=" + id + "&text=" + text + "&pretty=1";

        httpResponse(url, botToken, HttpMethod.POST);
    }

    // get time and set text for slack message
    public String timeCheckAndSetText() {
        String prefix = getPrefix();

        LocalTime localTime = LocalTime.now();
        String hour = String.valueOf(localTime.getHour());
        String minute = String.valueOf(localTime.getMinute());

        if (hour.equals("21") && minute.equals("00")) {
            return prefix + "계획 공유";
        } else {
            return prefix + "공부한 내용 정리";
        }
    }

    // Send Http Request and get response
    private ResponseEntity<String> httpResponse(String url, String token, HttpMethod method) {
        String requestUrl = url;
        HttpMethod requestMethod = method;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                requestUrl,
                requestMethod,
                requestEntity,
                String.class
        );

        return responseEntity;
    }

    // get prefix for slack message
    private String getPrefix() {
        LocalDate today = LocalDate.now();
        System.out.println(today);

        String month = String.valueOf(today.getMonthValue());
        String day = String.valueOf(today.getDayOfMonth());

        String prefix = month + "/" + day + " ";
        return prefix;
    }
}
