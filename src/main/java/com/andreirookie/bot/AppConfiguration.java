package com.andreirookie.bot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class AppConfiguration {

    @Bean
    public PomodoroBot pomodoroBot() {
        return new PomodoroBot();
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(PomodoroBot pomodoroBot) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
//        EchoBot echoBot = new EchoBot();
        telegramBotsApi.registerBot(pomodoroBot);

        new Thread(() -> {
            try {
                pomodoroBot.checkTimer();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).run();
        return telegramBotsApi;
    }

}
