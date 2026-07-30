package org.example.bot;

import org.example.model.MessageDTO;
import org.example.service.ChatHistoryService;
import org.example.service.MistralClient;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class MyBot extends TelegramLongPollingBot {
    private final MistralClient mistralClient;
    private final ChatHistoryService historyManager;

    public MyBot(String botToken) {
        super(botToken);
        mistralClient = new MistralClient();
        historyManager = new ChatHistoryService();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(!update.hasMessage() && !update.getMessage().hasText()) {
            return;
        }

        Long chatId = update.getMessage().getChatId();
        String userText = update.getMessage().getText();

        try {

            if("/start".equals(userText)) {
                historyManager.clearHistory(chatId);
                execute(new SendMessage(chatId.toString(), "✅ Я готов ответь на любые твои вопросы."));
                return;
            }

            if("/reset".equals(userText)) {
                historyManager.clearHistory(chatId);
                execute(new SendMessage(chatId.toString(), "✅ История диалога очищена."));
                return;
            }

            List<MessageDTO> historyForApi = historyManager.addUserMessageAndGetHistories(chatId, userText);
            String aiResponse = mistralClient.sendMessageWithHistory(historyForApi);
            historyManager.addAssistantMessage(chatId, aiResponse);
            sendMessageInChunks(chatId.toString(), aiResponse);
        } catch (Exception e) {
            try {
                execute(new SendMessage(chatId.toString(), "❌ Ошибка: " + e.getMessage()));
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void sendMessageInChunks(String chatId, String text) throws TelegramApiException, InterruptedException {
        int maxLength = 4000;

        if (text.length() <= maxLength) {
            SendMessage msg = new SendMessage(chatId, text);
            msg.enableMarkdown(true);
            execute(msg);
            return;
        }

        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + maxLength, text.length());

            int breakPoint = text.lastIndexOf('\n', end);
            if (breakPoint <= start) {
                breakPoint = text.lastIndexOf(' ', end);
            }
            if (breakPoint <= start) {
                breakPoint = end;
            }

            chunks.add(text.substring(start, breakPoint).trim());
            start = breakPoint;
        }

        for (int i = 0; i < chunks.size(); i++) {
            SendMessage msg = new SendMessage(chatId, chunks.get(i));
            msg.enableMarkdown(true);

            if (chunks.size() > 1) {
                msg.setText(chunks.get(i) + "\n\n_*(часть " + (i + 1)
                        + " из " + chunks.size() + ")*_");
            }

            execute(msg);

            if (i < chunks.size() - 1) {
                Thread.sleep(600);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "YOUR_TELEGRAM_USERNAME_BOT";
    }
}
