package com.credit.check.service;

import java.time.Duration;

import org.springframework.stereotype.Service;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.localai.LocalAiChatModel;

@Service
public class ChatLocalLLMService {

	public String generate(ChatLanguageModel model, String text) {
		
		
		//-- hardcoded for tests for now
		 model  = LocalAiChatModel.builder()
											.baseUrl("http://127.0.0.1:8080")
											.timeout(Duration.ofMinutes(250))//keep this timeout shorter
											//.modelName("gpt4all-j")
											//.modelName("mistral")
											.modelName("lunademo")
											.temperature(0.7)
											.build();
		 
		 System.out.println("\n**** hardcoded model config for tests : "+model);
		 System.out.println("**** hardcoded model config for tests : "+model);
		 System.out.println("**** hardcoded model config for tests : "+model);
		 System.out.println("**** hardcoded model config for tests : "+model);
		//-- tests
		
		 
		System.out.println("\n---- Local LLM chat response for \ntext : "+text + " \nmodel : " +model);
		String response = model.generate(text);		
		System.out.println(response);
		
		
		return model.generate(text);
	}
}