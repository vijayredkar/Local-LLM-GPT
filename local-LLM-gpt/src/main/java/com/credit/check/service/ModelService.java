package com.credit.check.service;

import java.time.Duration;

import org.springframework.stereotype.Service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.localai.LocalAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

@Service
public class ModelService {

	private ChatLanguageModel demoModel;
	private static final String MODEL_NAME = "gpt4all-j";
	private static final String LOCAL_AI_URL = "http://127.0.0.1:8080";
	private ChatLanguageModel localModel;
	private AllMiniLmL6V2EmbeddingModel embeddingModel;

	public ChatLanguageModel getDemoModel() {
		if (demoModel == null) {
			demoModel = OpenAiChatModel.withApiKey("sk-dummy");
		}
		return demoModel;
	}	

	public ChatLanguageModel getLocalModel() 
	{
		if (localModel == null) {
			localModel = LocalAiChatModel.builder()
										.baseUrl(LOCAL_AI_URL)
										.timeout(Duration.ofMinutes(250))//keep this timeout shorter
										.modelName(MODEL_NAME)
										.build();
		}
		return localModel;
	}

	public EmbeddingModel getEmbeddingModel() 
	{
		if (embeddingModel == null) {
			embeddingModel = new AllMiniLmL6V2EmbeddingModel();
		}
		return embeddingModel;
	}
}