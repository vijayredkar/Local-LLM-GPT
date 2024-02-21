package com.credit.check.service;

import static dev.langchain4j.internal.Utils.randomUUID;
import java.util.List;
import org.springframework.stereotype.Service;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;

@Service
public class EmbeddingLocalLLMService 
{
	private EmbeddingStore<TextSegment> embeddingStore;

	public String embed(EmbeddingModel embeddingModel, String text) 
	{
		StringBuilder sb = new StringBuilder();

		Embedding inProcessEmbedding = embeddingModel.embed(text).content();
		//sb.append(String.valueOf(inProcessEmbedding)).append(System.lineSeparator());

		//-- add embedded segments to Chroma
		//TextSegment segment1 = TextSegment.from("Names of regions in India \n Jharkhand\n Mahabaleshwar\n Coimbatore\n Kolkatta");
		TextSegment segment1 = TextSegment.from("Jharkhand");
		Embedding embedding1 = embeddingModel.embed(segment1).content();
		getEmbeddingStore().add(embedding1, segment1);

		TextSegment segment2 = TextSegment.from("Mahabaleshwar");
		Embedding embedding2 = embeddingModel.embed(segment2).content();
		getEmbeddingStore().add(embedding2, segment2);
		
		TextSegment segment3 = TextSegment.from("Coimbatore");
		Embedding embedding3 = embeddingModel.embed(segment3).content();
		getEmbeddingStore().add(embedding3, segment3);
		
		TextSegment segment4 = TextSegment.from("Kolkatta");
		Embedding embedding4 = embeddingModel.embed(segment4).content();
		getEmbeddingStore().add(embedding4, segment4);
		
		
		//-- search Chroma
		//Embedding queryEmbedding = embeddingModel.embed("Jhak is incorrectly spelled. What is the correct spelling of this Indian state? In the response please extract only the specific name of the state that is missplled.").content();
		//Embedding queryEmbedding = embeddingModel.embed("Kolkat is incorrectly spelled. What is the correct spelling of this Indian state? In the response please extract only the specific name of the state that is missplled.").content();
		//Embedding queryEmbedding = embeddingModel.embed("Kalcut is incorrectly spelled. What is the correct spelling of this Indian state? In the response please extract only the specific name of the state that is missplled.").content();
		Embedding queryEmbedding = embeddingModel.embed("Komabatore is incorrectly spelled. What is the correct spelling of this Indian state? In the response please extract only the specific name of the state that is missplled.").content();
		List<EmbeddingMatch<TextSegment>> relevant = getEmbeddingStore().findRelevant(queryEmbedding, 1);
		EmbeddingMatch<TextSegment> embeddingMatch = relevant.get(0);
		
		

		sb.append(String.valueOf(embeddingMatch.score())).append(System.lineSeparator()); // 0.8144288493114709
		sb.append(embeddingMatch.embedded().text()); // I like football.

		
		String response = sb.toString().toString();
		System.out.println("\n---- response "+response);
		
		return response;
	}

	private EmbeddingStore<TextSegment> getEmbeddingStore() 
	{
		if (embeddingStore == null) 
		{
			embeddingStore = ChromaEmbeddingStore.builder()
												 .baseUrl("http://127.0.0.1:8000")
												 .collectionName(randomUUID())
												 .build();
		}
		return embeddingStore;
	}
}