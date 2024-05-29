import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class Bot extends TelegramLongPollingBot {
    public InlineKeyboardButton next = InlineKeyboardButton.builder()
            .text("Next").callbackData("next")
            .build();

    public InlineKeyboardButton back = InlineKeyboardButton.builder()
            .text("Back")
            .url("https://core.telegram.org/bots/api")
            .build();

    public InlineKeyboardButton url = InlineKeyboardButton.builder()
            .text("Tutorial").callbackData("url")
            .build();
    private boolean screaming = false;
    private InlineKeyboardMarkup keyboardM1 = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(next))
            .build();

    //Buttons are wrapped in lists each keyboard is a set button rows
    private InlineKeyboardMarkup keyboardM2 = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(back))
            .keyboardRow(List.of(url))
            .build();
    @Override
    public String getBotUsername() {
        return "Tutorial tg bot";
    }

    @Override
    public String getBotToken() {
        return "7232492112:AAGHKsTuP_VTDBlLBfAulfZ6O8ybk_gMGw0";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        User user = msg.getFrom();
        Long userId = user.getId();

        //sendText(id, msg.getText());
        System.out.println(user.getFirstName() + " wrote " + msg.getText() + " :)");

        var txt = msg.getText();
        if(msg.isCommand()) {
            if (txt.equals("/scream"))
                screaming = true;
            else if (txt.equals("/whisper"))
                screaming = false;
            else if (txt.equals("/menu"))
                sendMenu(userId, "<b>Menu 1</b>", keyboardM1);
            return;
        }

        if (msg.isCommand()) {
            if (msg.getText().equals("/scream")) {
                screaming = true; //If the command was "scream", we switch gears
            } else if (msg.getText().equals("/whisper")) {
                screaming = false; //Otherwise we return to normal
            }
            return; //We don't want to echo commands, so we exit
        }

        if (screaming) {
            scream(userId, msg); //Call a custom method
        } else {
            copyMessage(userId, msg.getMessageId()); //Else proceed normally
        }

    }

    public void sendMenu(Long who, String txt, InlineKeyboardMarkup km) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .parseMode("HTML")
                .text(txt)
                .replyMarkup(km)
                .build();

        try {
            execute(sm);
        } catch (TelegramApiException tae) {
            throw new RuntimeException(tae);
        }
    }

    private void scream(Long id, Message msg) {
        if (msg.hasText()) {
            sendText(id, msg.getText().toUpperCase());
        } else {
            copyMessage(id, msg.getMessageId()); //We can't really scream a sticker
        }
    }

    public void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //who are we sending a message to
                .text(what) //Content message
                .build();

        try {
            execute(sm); //Actually sending the message
        } catch (TelegramApiException tae) {
            throw new RuntimeException(); //Any error will be printed here
        }
    }

    public void copyMessage(Long who, Integer idMsg) {
        CopyMessage cm = CopyMessage.builder()
                .fromChatId(who.toString()) //We copy from the user
                .chatId(who.toString()) //And send it back to him
                .messageId(idMsg) //Specifying what message
                .build();

        try {
            execute(cm); //Actually sending the message
        } catch (TelegramApiException tae) {
            throw new RuntimeException(); //Any error will be printed here
        }
    }

    public void buttonTap(Long id, String queryId, String data, int msgId) {
        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(id.toString())
                .messageId(msgId).text("").build();

        EditMessageReplyMarkup editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                .chatId(id.toString())
                .messageId(msgId)
                .build();

        if (data.equals("next")) {
            editMessageText.setText("MENU 2");
            editMessageReplyMarkup.setReplyMarkup(keyboardM2);
        } else if (data.equals("back")) {
            editMessageText.setText("MENU 1");
            editMessageReplyMarkup.setReplyMarkup(keyboardM1);
        }

        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                .callbackQueryId(queryId)
                .build();

        try {
            execute(close);
            execute(editMessageText);
            execute(editMessageReplyMarkup);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
