# How work REST client
A bot for communicating with neural networks from the Mistral provider.
A Telegram bot created at rubenlagus/TelegramBots. okHTTP was used to send requests and GSON was used to convert responses into objects.

<h3>What is needed to send Http requests</h3>
 # API_KEY - is a unique key for authorization, passed in the Authorization header. 
 To get it, you need to register on the official <a href="https://mistral.ai/">website</a>
 # MODEL - you can view the model you want to use on the <a href="https://docs.mistral.ai/models/overview">website.</a>

All requests related to the transmission of AI messages are sent to: https://api.mistral.ai/v1/chat/completions

# How work Telegram Bot
The telegram bot is implemented using Prince Polling, we send a telegram request for a certain period of time and check if the message has arrived.
The received answer is compared for the following logic.
