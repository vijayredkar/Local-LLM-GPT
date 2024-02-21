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
public class EmbeddingService 
{
	private EmbeddingStore<TextSegment> embeddingStore;

	public String embed(EmbeddingModel embeddingModel, String text) 
	{
		StringBuilder sb = new StringBuilder();

		Embedding inProcessEmbedding = embeddingModel.embed(text).content();
		sb.append(String.valueOf(inProcessEmbedding)).append(System.lineSeparator());

		TextSegment segment1 = TextSegment.from("I like football.");
		Embedding embedding1 = embeddingModel.embed(segment1).content();
		getEmbeddingStore().add(embedding1, segment1);

		TextSegment segment2 = TextSegment.from("The weather is good today.");
		Embedding embedding2 = embeddingModel.embed(segment2).content();
		getEmbeddingStore().add(embedding2, segment2);

		Embedding queryEmbedding = embeddingModel.embed("What is your favourite sport?").content();
		List<EmbeddingMatch<TextSegment>> relevant = getEmbeddingStore().findRelevant(queryEmbedding, 1);
		EmbeddingMatch<TextSegment> embeddingMatch = relevant.get(0);

		sb.append(String.valueOf(embeddingMatch.score())).append(System.lineSeparator()); // 0.8144288493114709
		sb.append(embeddingMatch.embedded().text()); // I like football.

		return sb.toString();
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