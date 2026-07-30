package org.example.service;

import org.example.model.MessageDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatHistoryService {
    private final Map<Long, List<MessageDTO>> histories = new ConcurrentHashMap<>();
    private final String systemPrompt = "Ты полезный и дружилюбный асситент! Отвечай кратко и лаконично.";
    private static final int MAX_HISTORIES_SIZE = 20;

    public synchronized List<MessageDTO> addUserMessageAndGetHistories(Long chatId, String userText) {

        histories.putIfAbsent(chatId, new ArrayList<>());
        List<MessageDTO> history = histories.get(chatId);

        if(history.isEmpty()) {
            history.add(new MessageDTO("system", systemPrompt));
        }

        history.add(new MessageDTO("user", userText));

        trimHistory(history);

        return new ArrayList<>(history);
    }

    public synchronized void addAssistantMessage(Long chatId, String assistantText) {
        List<MessageDTO> history = histories.get(chatId);
        if(history != null) {
            history.add(new MessageDTO("assistant", assistantText));
            trimHistory(history);
        }
    }

    public synchronized void clearHistory(Long chatId) {
        histories.remove(chatId);
    }

    private void trimHistory(List<MessageDTO> history) {
        if(history.size() > MAX_HISTORIES_SIZE + 1) {
            List<MessageDTO> trimmed = new ArrayList<>();
            trimmed.add(history.get(0));
            trimmed.addAll(history.subList(history.size() - MAX_HISTORIES_SIZE, history.size()));
            history.clear();
            history.addAll(trimmed);
        }
    }
}
