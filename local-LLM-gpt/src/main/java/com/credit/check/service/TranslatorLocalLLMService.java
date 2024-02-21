package com.credit.check.service;

import org.springframework.stereotype.Service;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.localai.LocalAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.V;

import java.time.Duration;
import java.util.ArrayList;

	@Service
	public class TranslatorLocalLLMService 
	{
		/*
		interface Translator 
		{
			//@SystemMessage("You are a professional translator into {{language}}")
			@SystemMessage("You are a professional book/novel translator and you will be given the body of text. Your job is to translate the text into into {{language}}."
					+ "	You are only allowed to return the translated text and nothing else."
					+ "	IMPORTANT: ONLY RETURN TRANSLATED TEXT AND NOTHING ELSE.")
			@UserMessage("Translate the following text: {{text}}")
			String translate(@V("text") String text, @V("language") String language);
		}
		*/
		/*
		interface Translator 
		{
			//@SystemMessage("You are a professional translator into {{language}}")
			@SystemMessage("You are a text formatter. You will be given the body of text. Your job is to convert this text to uppercase in {{language}}.")
			@UserMessage("Format the following text: {{text}}")
			String translate(@V("text") String text, @V("language") String language);
		}
		
		public String translate_1(ChatLanguageModel model, String text, String language) 
		{
			System.out.println("\n---- translate_1 GPT Model applied : "+model);
			Translator translator = AiServices.create(Translator.class, model);			
			return translator.translate(text, language);
		}
		*/
		
		public String translate_2(ChatLanguageModel model, String text, String language) 
		{
			
			//-- hardcoded for tests for now
			 
			model  = LocalAiChatModel.builder()
												.baseUrl("http://127.0.0.1:8080")
												.timeout(Duration.ofMinutes(250))//keep this timeout shorter
												//.modelName("gpt4all-j")
												//.modelName("lunademo")
												.modelName("openllama")
												.temperature(0.0)
												.build();
			 
			 
			 System.out.println("\n**** hardcoded model config for tests : "+model.toString());
			 System.out.println("**** hardcoded model config for tests : "+model);
			 System.out.println("**** hardcoded model config for tests : "+model);
			 System.out.println("**** hardcoded model config for tests : "+model);
			//-- tests
			 
			System.out.println("\n---- translate_2 GPT Model applied : "+model);
			
			
			
			//SystemMessage responseInDutch = new SystemMessage("You are a helpful Dutch language specialist. Antwoord altijd in het Nederlands.");
			SystemMessage responseInDutch = new SystemMessage("You are a expert language translator.");
			//SystemMessage responseInDutch = new SystemMessage("You will respond to all questions in the Dutch language only.");
			//SystemMessage responseInDutch = new SystemMessage("You are a helpful assistant.");
			
			//UserMessage question = new UserMessage("What is the Spanish translation of \"What is your name?\"?");
			//UserMessage question = new UserMessage("What is the Spanish translation of \"Your creditcard bill payment is due on 20 February 2024.\"?");
			//UserMessage question = new UserMessage("What is the Arabic translation of \"Your credit card bill payment is due on 20 February 2024.\"?");
			UserMessage question = new UserMessage("What is the Arabic translation of \"Your need to pay the fees by 20 February 2024.\" ?");
			
			
			var chatMessages = new ArrayList<ChatMessage>();
			chatMessages.add(responseInDutch);
			chatMessages.add(question);
			 
			Response<AiMessage> response = model.generate(chatMessages);
			
			String responseData = response.content().toString();
			System.out.println("---- response : "+responseData);
			
			return responseData;
		}
		
	}
	
	
	