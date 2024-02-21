package com.credit.check.service;

import java.time.Duration;

import org.springframework.stereotype.Service;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.localai.LocalAiChatModel;

@Service
public class ChatLocalLLMOllamaService {

	/*
	public void generate(String text) {
		
		System.out.println("\n**** hardcoded with Ollama LLM ");
		
		String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        ollamaAPI.setVerbose(true);

        boolean isOllamaServerReachable = ollamaAPI.ping();

        System.out.println("Is Ollama server alive: " + isOllamaServerReachable);		 
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
	*/
}