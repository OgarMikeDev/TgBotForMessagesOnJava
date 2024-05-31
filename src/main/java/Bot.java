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
            .callbackData("back")
            .build();

    public InlineKeyboardButton url = InlineKeyboardButton.builder()
            .text("Tutorial")
            .url("https://web.telegram.org/a/#1957288286")
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
        return "ogar_mike_tg_30052024_bot";
    }

    @Override
    public String getBotToken() {
        return "7243120977:AAHBOr0z1JJi90m95pPZpohIEhh_218i55Y";
    }

    @Override
    public void onUpdateReceived(Update update) {
        buttonTab(update);

        Message msg = update.getMessage();
        User user = msg.getFrom();
        Long userId = user.getId();

        //sendText(id, msg.getText());
        System.out.println(user.getFirstName() + " wrote \"" + msg.getText() + "\" :)");

        String txt = msg.getText();
        if (msg.isCommand()) {
            if (txt.equals("/scream")) {
                screaming = true;
            } else if (txt.equals("/whisper")) {
                screaming = false;
            } else if (txt.equals("/menu")) {
                sendMenu(userId, "<b>Menu 1</b>", keyboardM1);
            }
            return;
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

    public void buttonTab(Update update) {
        if (update.hasCallbackQuery()) {
            String id = update.getCallbackQuery().getMessage().getChatId().toString();
            int msgId = update.getCallbackQuery().getMessage().getMessageId();
            String data = update.getCallbackQuery().getData();
            String queryId = update.getCallbackQuery().getId();

            System.out.println(
                    "Id: " + id +
                            "\nMessage id: " + msgId +
                            "\nData: " + data +
                            "\nQuery id:" + queryId);
            EditMessageText newTxt = EditMessageText.builder()
                    .chatId(id)
                    .messageId(msgId).text("").build();

            EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                    .chatId(id.toString()).messageId(msgId).build();

            if(data.equals("next")) {
                newTxt.setText("Menu 2");
                newKb.setReplyMarkup(keyboardM2);
            } else if(data.equals("back")) {
                newTxt.setText("Menu 1");
                newKb.setReplyMarkup(keyboardM1);
            }

            AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                    .callbackQueryId(queryId).build();

            try {
                execute(close);
                execute(newTxt);
                execute(newKb);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

            return;
        }
    }
}
