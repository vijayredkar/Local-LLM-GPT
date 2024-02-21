package com.credit.check.service;
	
	
import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;
import static org.mapdb.Serializer.INTEGER;
import static org.mapdb.Serializer.STRING;

import java.util.List;
import java.util.Map;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.stereotype.Service;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

@Service
public class PersistenceService {

	private PersistentChatMemoryStore store = new PersistentChatMemoryStore();

	interface Assistant {
		String chat(@MemoryId int memoryId, @UserMessage String userMessage);
	}

	static class PersistentChatMemoryStore implements ChatMemoryStore {
		private final DB db = DBMaker.fileDB("multi-user-chat-memory.db").closeOnJvmShutdown().transactionEnable()
				.make();
		private final Map<Integer, String> map = db.hashMap("messages", INTEGER, STRING).createOrOpen();

		@Override
		public List<ChatMessage> getMessages(Object memoryId) {
			String json = map.get((int) memoryId);
			return messagesFromJson(json);
		}

		@Override
		public void updateMessages(Object memoryId, List<ChatMessage> messages) {
			String json = messagesToJson(messages);
			map.put((int) memoryId, json);
			db.commit();
		}

		@Override
		public void deleteMessages(Object memoryId) {
			map.remove((int) memoryId);
			db.commit();
		}
	}

	public String demo(ChatLanguageModel model, boolean showName) {
		StringBuilder sb = new StringBuilder();

		ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder().id(memoryId)
				.maxMessages(10).chatMemoryStore(store).build();

		Assistant assistant = AiServices.builder(Assistant.class).chatLanguageModel(model)
				.chatMemoryProvider(chatMemoryProvider).build();

		if (showName) {
			sb.append(assistant.chat(1, "Hello, my name is Klaus")).append(System.lineSeparator());
			sb.append(assistant.chat(2, "Hi, my name is Francine"));

		} else {
			sb.append(assistant.chat(1, "What is my name?")).append(System.lineSeparator());
			sb.append(assistant.chat(2, "What is my name?"));
		}
		return sb.toString();
	}
}	
