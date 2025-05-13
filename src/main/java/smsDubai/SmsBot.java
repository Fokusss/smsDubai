package smsDubai;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class SmsBot extends TelegramLongPollingBot {

  private final String botToken = "7799926385:AAGptgE3qz3yXCGOA2tKUe-CXRmpX9R_WKE";
  private final String botUsername = "smsDubai_bot";
  private final Map<Long, List<String[]>> userData = new HashMap<>();

  @Override
  public String getBotUsername() {
    return botUsername;
  }

  @Override
  public String getBotToken() {
    return botToken;
  }

  @Override
  public void onUpdateReceived(Update update) {
    Message message = update.getMessage();
    Long chatId = message.getChatId();

    if (message.hasDocument()) {
      Document doc = message.getDocument();
      if (doc.getFileName().endsWith(".csv")) {
        try {
          GetFile getFile = new GetFile(doc.getFileId());
          File telegramFile = execute(getFile);
          String fileUrl = telegramFile.getFileUrl(getBotToken());

          InputStream input = new URL(fileUrl).openStream();
          BufferedReader reader = new BufferedReader(new InputStreamReader(input));
          StringBuilder response = new StringBuilder();
          List<String[]> data = new ArrayList<>();
          String line;
          while ((line = reader.readLine()) != null) {
            data.add(line.split(","));
          }

          userData.put(chatId, data);
          sendMessage(message.getChatId(), "День?");
        } catch (Exception e) {
          sendMessage(chatId, "Ошибка при обработке файла.");
          e.printStackTrace();
        }
      }
    } else if (message.hasText() && userData.get(message.getChatId()) != null) {
      String day = message.getText();
      int dayInt;
      try {
        dayInt = Integer.parseInt(day);
        sendMessage(message.getChatId(), "Обработка файла");
        Processing processing = new Processing(userData.get(message.getChatId()), dayInt);
        sendMessage(message.getChatId(), processing.printUpdate());
      } catch (Exception e) {
        sendMessage(message.getChatId(), "1-31");
        e.printStackTrace();
      }

    } else {
      sendMessage(message.getChatId(), "Сначала .csv");
    }


  }

  private void sendMessage(Long chatId, String str) {
    SendMessage msg = new SendMessage();
    msg.setChatId(chatId.toString());
    msg.setText(str);
    try {
      execute(msg);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

}
