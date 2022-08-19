package com.andreirookie.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

import static com.andreirookie.bot.PomodoroBot.TimerType.BREAK;
import static com.andreirookie.bot.PomodoroBot.TimerType.WORK;

public class PomodoroBot extends TelegramLongPollingBot {


    // second type(Long) - chatId
    private final ConcurrentHashMap<UserTimer, Long> userTimerRepository = new ConcurrentHashMap<>();

    enum TimerType {
        WORK,
        BREAK
    }

     record UserTimer(Instant userTimer, TimerType timerType) {}

    @Override
    public String getBotUsername() {
        return "PomodoroBot";
    }

    @Override
    public String getBotToken() {
        return Constants.pomodoroBotToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        if (update.getMessage().getText().equalsIgnoreCase("exit")) {
            sendMsg(update.getMessage().getChatId(),"Timer stopped");
            //TODO stop timer
            System.out.println("Timer stopped");
            return;
        }


        var args = update.getMessage().getText().split(" ");
        Instant workTime = Instant.now().plus(Long.parseLong(args[0]), ChronoUnit.MINUTES);

        Instant breakTime = workTime.plus(Long.parseLong(args[1]), ChronoUnit.MINUTES);

        userTimerRepository.put((new UserTimer(workTime, WORK)), update.getMessage().getChatId());
        System.out.printf("[%s] Collection size %d", Instant.now().toString(), userTimerRepository.size());
        userTimerRepository.put((new UserTimer(breakTime, TimerType.BREAK)), update.getMessage().getChatId());
        sendMsg(update.getMessage().getChatId(), "Timer started\nLet's work");
    }

    public void checkTimer() throws InterruptedException{

        while(true) {
            System.out.println(" User timers count: " + userTimerRepository.size());

            // TODO отображение оставшегося  времени
//            userTimerRepository.forEach((timer, userId) -> {
//                 if (Instant.now().isBefore(timer.userTimer) && timer.timerType == BREAK) {
//                     var dif = (timer.userTimer.minus(System.currentTimeMillis(),ChronoUnit.MINUTES));
//                     sendMsg(userId, ("Time left: " + dif));
//
//                 }
//                try {
//                    Thread.sleep(60000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            });

            userTimerRepository.forEach((timer, userId) -> {
                if (Instant.now().isAfter(timer.userTimer)) {
                    switch (timer.timerType) {
                        case WORK -> sendMsg(userId, "Time to take a break");
                        case BREAK -> sendMsg(userId,"Time to work");
                    }
                    userTimerRepository.remove(timer);
                }
            });
            Thread.sleep(1000);


        }

    }

    private void sendMsg(Long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}
