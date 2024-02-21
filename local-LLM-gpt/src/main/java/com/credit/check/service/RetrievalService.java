package com.credit.check.service;

import static dev.langchain4j.data.document.FileSystemDocumentLoader.loadDocument;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Service;

import com.credit.check.ClientViewApplication;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

@Service
public class RetrievalService {

	private EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

	/*
	private static Path toPath(String fileName) {
		try {
			String fileWithPath = "C:\\Vijay\\Java\\projects\\openapi-ai-trials\\LLM-gen-ai\\langchain4j\\local-LLM-gpt\\src\\main\\java\\com\\credit\\check\\"+ fileName;
			System.out.println("---- fileWithPath"+fileWithPath);
			
			URL fileUrl = ClientViewApplication.class.getResource("C:\\Vijay\\Java\\projects\\openapi-ai-trials\\LLM-gen-ai\\langchain4j\\local-LLM-gpt\\src\\main\\java\\com\\credit\\check\\"+ fileName);
	
			//URL fileUrl=null;
			//fileUrl = new URL(fileWithPath);
			//fileUrl = ClientViewApplication.class.getResource(fileWithPath);
	
			return Paths.get(fileUrl.toURI());
			
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
    */
    
	private static Path toPath(String fileName) 
	{
		
		//String fileWithPath = "C:\\Vijay\\Java\\projects\\openapi-ai-trials\\LLM-gen-ai\\langchain4j\\local-LLM-gpt\\src\\main\\java\\com\\credit\\check\\"+ fileName;
		String fileWithPath = "C:\\Vijay\\Java\\projects\\openapi-ai-trials\\LLM-gen-ai\\langchain4j\\local-LLM-gpt\\src\\main\\java\\com\\credit\\check\\"+ fileName;
		System.out.println("---- fileWithPath "+fileWithPath);
			
		Path path1 = Paths.get(fileWithPath);			
		return path1;
	}
    
    
	public String retrieve(ChatLanguageModel model, EmbeddingModel embeddingModel, String fileName, String question) {
		EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
				.documentSplitter(DocumentSplitters.recursive(500, 0)).embeddingModel(embeddingModel)
				.embeddingStore(embeddingStore).build();

		Document document = loadDocument(toPath(fileName));
		ingestor.ingest(document);

		ConversationalRetrievalChain chain = ConversationalRetrievalChain.builder().chatLanguageModel(model)
				.retriever(EmbeddingStoreRetriever.from(embeddingStore, embeddingModel)).build();

		String result = chain.execute(question);
		System.out.println("\n\n ----  result is    "+result);
		//return chain.execute(question);
		return result;
	}
}