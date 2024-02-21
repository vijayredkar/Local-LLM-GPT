package com.credit.check.service;

import static dev.langchain4j.data.document.FileSystemDocumentLoader.loadDocument;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import java.util.List;
import java.util.ArrayList;

@Service
public class DocLoadChromaService {

	private EmbeddingStore<TextSegment> embeddingStore = getEmbeddingStore();
	
	private EmbeddingStore<TextSegment> getEmbeddingStore() 
	{
		if (embeddingStore == null) 
		{
			embeddingStore = ChromaEmbeddingStore.builder()
												 .baseUrl("http://127.0.0.1:8000")
												 .collectionName("indian-cities-5")
												 .build();
		}
		return embeddingStore;
	}
    
	private static Path toPath(String fileName) 
	{
		String basePath = "C:\\Vijay\\Java\\projects\\openapi-ai-trials\\LLM-gen-ai\\langchain4j\\local-LLM-gpt\\training-docs\\";
		String fileWithPath = basePath + fileName;
		System.out.println("---- Got file name With Path "+fileWithPath);
			
		Path path1 = Paths.get(fileWithPath);			
		return path1;
	}
    
	//-- Chroma operations on single row from DB
	public String retrieveFromSingleRow(ChatLanguageModel model, EmbeddingModel embeddingModel, String fileName, String text, int maxLimit, boolean refreshDb) 
	{	
		System.out.println("\nmodel : "+ model +" \n "+"embeddingModel : "+ embeddingModel +" \n "+"fileName : "+ fileName +" \n "+"text : "+ text);

		if(refreshDb)
		{
			saveSegmentToChroma(fileName, embeddingModel); //save single row as 1 segment
		}
		
		List<EmbeddingMatch<TextSegment>> result = fetchSingleRowFromChroma(text, maxLimit, embeddingModel);	
		
		StringBuilder responseBldr = new StringBuilder();
		for(EmbeddingMatch<TextSegment> segment : result)
		{
			responseBldr.append(segment.embedded().text());
			responseBldr.append("\n");
		}
		
		String response = responseBldr.toString();
		System.out.println("\n--- Got closest relevant record from Chroma : "+response);
		
		return response;
	}
	
	
	 //-- Chroma operations on full doc from DB
	public String retrieveFromDoc(ChatLanguageModel model, EmbeddingModel embeddingModel, String fileName, String text) 
	{	
		System.out.println("\nmodel : "+ model +" \n "+"embeddingModel : "+ embeddingModel +" \n "+"fileName : "+ fileName +" \n "+"text : "+ text);		 
		
		saveDocToChroma(embeddingModel, fileName); //save full doc as 1 segment    // load all data at one time. Avoid repeated loads
		String result = fetchDocTextFromChroma(model, embeddingModel, text); //read full doc and extract relevant lines from Chroma doc row
						
		return result;
	}
	
	
	//Chroma - single row fetch from DB
	public List<EmbeddingMatch<TextSegment>> fetchSingleRowFromChroma(String query, int maxResults, EmbeddingModel embeddingModel) 
	{
        Embedding queryEmbedding = embeddingModel.embed(query).content();
        return embeddingStore.findRelevant(queryEmbedding, maxResults);
    }
	
	public void saveSegmentToChroma(String fileName, EmbeddingModel embeddingModel) 
	{
		List<String> lines = parseFile(fileName);
		
		for(String text : lines)
		{
			TextSegment segment1 = TextSegment.from(text, new Metadata());
	        Embedding embedding1 = embeddingModel.embed(segment1).content();
	        embeddingStore.add(embedding1, segment1);
	        
	        System.out.println("----  Saved embedding segment to Local Chroma : "+text);
		}        
    }

	private List<String> parseFile(String fileName) {
		//read fileName line by line and save to chroma
		List<String> lines = new ArrayList<String>();
		Path path = toPath(fileName);
		try 
		{
			 lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return lines;
	}
	
	
	private String fetchDocTextFromChroma(ChatLanguageModel model, EmbeddingModel embeddingModel, String text) {
		ConversationalRetrievalChain chain = ConversationalRetrievalChain.builder()
																		 .chatLanguageModel(model)
																		 .retriever(EmbeddingStoreRetriever.from(embeddingStore, embeddingModel))
																		 .build();

		String result = chain.execute(text);
		System.out.println("\n ----  Got Local LLM response :\n    "+result);
		return result;
	}	

	private void saveDocToChroma(EmbeddingModel embeddingModel, String fileName) {
		EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
																.documentSplitter(DocumentSplitters.recursive(500, 0))
																.embeddingModel(embeddingModel)
																.embeddingStore(embeddingStore)
																.build();

		Document document = loadDocument(toPath(fileName));
		ingestor.ingest(document);
		System.out.println("\n ----  Saved to Local Chroma");
	}	

}