package com.credit.check.service;

import org.springframework.stereotype.Service;
import dev.langchain4j.model.chat.ChatLanguageModel;

@Service
public class ChatService {

	public String generate(ChatLanguageModel model, String text) {
		return model.generate(text);
	}
}