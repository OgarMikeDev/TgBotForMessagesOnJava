import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
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
        copyMessage(userId, msg.getMessageId());
    }

//    public void sendText(Long who, String what) {
//        SendMessage sm = SendMessage.builder()
//                .chatId(who.toString()) //who are we sending a message to
//                .text(what) //Content message
//                .build();
//
//        try {
//            execute(sm); //Actually sending the message
//        } catch (TelegramApiException tae) {
//            throw new RuntimeException(); //Any error will be printed here
//        }
//    }

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
}
