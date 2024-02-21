package com.credit.check.service;

import org.springframework.stereotype.Service;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

	@Service
	public class SpellCheckerService 
	{
		interface Translator 
		{
			@SystemMessage("You are a professional spelling checker in {{language}}")
			
			@UserMessage("Fix typos in these names of regions of India and keep only the name {{text}}")
			//@UserMessage("Please fix the spelling in the following text:  {{text}}")
			//@UserMessage("Cioimbtore is misspelled. This is a region in India. Please spell check and provide the correct region name.")
			//@UserMessage("What is the correctly spelled name of Cioimbtore?")
			//@UserMessage("Correct region names in India are Coimbatore, Delhi and Mumbai. Please generate your response fom this data only. What is the correct name of Cioimbtore?")
			
			
			String spellCheck(@V("text") String text, @V("language") String language);
			
			//@UserMessage("Translate the following text: {{text}}")
		}

		public String spellCheck(ChatLanguageModel model, String text, String language) 
		{
			System.out.println("\n----Prompt: \ntext,  language and model \n: "+text +  "   " +language+ "   " +model);
			Translator translator = AiServices.create(Translator.class, model);

			String response = translator.spellCheck(text, language);
			System.out.println("---- Response: \nresponse");
			return translator.spellCheck(text, language);
		}
	}