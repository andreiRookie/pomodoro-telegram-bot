package com.andreirookie.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class EchoBot extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "Popoguy";
    }

    @Override
    public String getBotToken() {
        return Constants.echoBotToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if ( update.getMessage().getText().equals("/start")) {

                sendMsg(update.getMessage().getChatId(),
                        ("Hello there, " + update.getMessage().getChat().getFirstName() +
                                "! I'm bot " + getBotUsername()),
                        update.getMessage().getChat().getFirstName());

                return;
            }

            sendMsg(update.getMessage().getChatId(),
                    update.getMessage().getText(),
                    update.getMessage().getChat().getFirstName());

        }

    }

    private void sendMsg(Long chatId, String text, String userName) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        System.out.println("Username = " + userName +
                " message = " + text);

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
