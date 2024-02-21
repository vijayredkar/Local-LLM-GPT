package com.credit.check.service;

import org.springframework.stereotype.Service;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

	@Service
	public class TranslatorService 
	{
		interface Translator 
		{
			@SystemMessage("You are a professional translator into {{language}}")
			@UserMessage("Translate the following text: {{text}}")
			String translate(@V("text") String text, @V("language") String language);
		}

		public String translate(ChatLanguageModel model, String text, String language) 
		{
			System.out.println("\n---- Translation GPT Model applied : "+model);
			Translator translator = AiServices.create(Translator.class, model);			
			return translator.translate(text, language);
		}
	}