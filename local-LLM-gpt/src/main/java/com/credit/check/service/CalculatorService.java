package com.credit.check.service;

import org.springframework.stereotype.Service;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;

@Service
public class CalculatorService {

	static class Calculator 
	{
		@Tool("Calculates the length of a string")
		int stringLength(String s) 
		{
			return s.length();
		}

		@Tool("Calculates the sum of two numbers")
		int add(int a, int b) 
		{
			return a + b;
		}

		@Tool("Calculates the square root of a number")
		double sqrt(int x) 
		{
			return Math.sqrt(x);
		}
	}

	interface Assistant 
	{
		String chat(String userMessage);
	}

	public String calculate(ChatLanguageModel model, String text) 
	{
		Assistant assistant = AiServices.builder(Assistant.class)
										.chatLanguageModel(model)
										.tools(new Calculator())
										.chatMemory(MessageWindowChatMemory.withMaxMessages(10))
										.build();
		return assistant.chat(text);
	}
}