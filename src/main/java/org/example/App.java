package org.example;

import org.example.bot.MyBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Scanner;

public class App 
{
    public static void main( String[] args ) {
        System.out.println( "App Start!" );

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new MyBot("YOUR_API_TELEGRAM_BOT"));
            System.out.println("Bot registered");

            System.out.println("For stop server working -> Enter stop");
            while (isRunning) {
                String message = scanner.nextLine();
                if(message.equals("stop")) {
                    isRunning = false;
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
