package com.credit.check.service;

import static dev.langchain4j.data.document.FileSystemDocumentLoader.loadDocument;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.shaded.org.awaitility.Durations;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import java.util.List;
import java.util.ArrayList;

@Service
public class DocLoadOscarsChromaOllamaService {

	private EmbeddingStore<TextSegment> embeddingStore = getEmbeddingStore();
	
	private EmbeddingStore<TextSegment> getEmbeddingStore() 
	{
		if (embeddingStore == null) 
		{
			embeddingStore = ChromaEmbeddingStore.builder()
												 .baseUrl("http://127.0.0.1:8000")
												 .collectionName("oscars-awards-6")
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
    
	
	//-- Oscars Chroma+Ollama orchestrator
	public String chromaOlamaRetrieveOrchestrate(ChatLanguageModel model, EmbeddingModel embeddingModel, String fileName, String userPrompt, int maxLimit, boolean refreshDb, double minScore) 
	{	
		String systemMessage = "You are a expert in films and awards. Your task is to answer this question from the user - ";
		String contextFromChroma = retrieveFromSingleRow(model,  embeddingModel, fileName, userPrompt, maxLimit, refreshDb, minScore);
		String contextMessage = "You will respond based on this provided context only - " + contextFromChroma;
		String promptWithFullContext = systemMessage + "\n" + userPrompt + "\n"+ contextMessage;
		
		String response = generate(promptWithFullContext);
		return response;
	}
	
	
	
	//-- Chroma operations section
	public String retrieveFromSingleRow(ChatLanguageModel model, EmbeddingModel embeddingModel, String fileName, String text, int maxLimit, boolean refreshDb, double minScore) 
	{	
		System.out.println("\nmodel : "+ model +" \n "+"embeddingModel : "+ embeddingModel +" \n "+"fileName : "+ fileName +" \n "+"text : "+ text +" \n "+"minScore : "+ minScore);

		if(refreshDb)
		{
			saveSegmentToChroma(fileName, embeddingModel); //save single row as 1 segment
			
			//--for testing only
			saveSegmentToChromaForTestingOnly(embeddingModel);
			//--for testing only
		}
		
		List<EmbeddingMatch<TextSegment>> result = fetchSingleRowFromChroma(text, maxLimit, embeddingModel, minScore);	
		
		StringBuilder responseBldr = new StringBuilder();
		StringBuilder tempResponseBldr = new StringBuilder();
		for(EmbeddingMatch<TextSegment> segment : result)
		{
			responseBldr.append(segment.embedded().text());
			
			tempResponseBldr.append(segment.embedded().text());
			tempResponseBldr.append("- with embedding score : ");
			tempResponseBldr.append(segment.score());
			tempResponseBldr.append("\n");
			
			responseBldr.append("\n");
		}
		
		String response = responseBldr.toString();
		System.out.println("\n--- Got closest relevant record from Chroma : \n"+tempResponseBldr.toString());
		
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
	
	/*
	//Chroma - single row fetch from DB
	public List<EmbeddingMatch<TextSegment>> fetchSingleRowFromChroma(String query, int maxResults, EmbeddingModel embeddingModel) 
	{
        Embedding queryEmbedding = embeddingModel.embed(query).content();
        //return embeddingStore.findRelevant(queryEmbedding, maxResults);
        //return embeddingStore.findRelevant(queryEmbedding, maxResults,0.9);  //very strict - no results
        //return embeddingStore.findRelevant(queryEmbedding, maxResults,0.5);  //average strict - sorted correct + mixed results
        //return embeddingStore.findRelevant(queryEmbedding, maxResults,0.6);  
        return embeddingStore.findRelevant(queryEmbedding, maxResults,0.7);  //average strict - sorted correct + better subset results
        //return embeddingStore.findRelevant(queryEmbedding, maxResults,0.6899);  //average strict - sorted correct + better subset results
    }
	*/
	
	//Chroma - single row fetch from DB
	public List<EmbeddingMatch<TextSegment>> fetchSingleRowFromChroma(String query, int maxResults, EmbeddingModel embeddingModel, double minScore) 
	{
        Embedding queryEmbedding = embeddingModel.embed(query).content();
        return embeddingStore.findRelevant(queryEmbedding, maxResults,minScore);
    }
	
	public void saveSegmentToChroma(String fileName, EmbeddingModel embeddingModel) 
	{
		List<String> lines = parseFile(fileName);
		
		
		lines.remove(0); // remove header row
		
		for(String text : lines)
		{	
			text = fineTuneDataSet(text);
			TextSegment segment1 = TextSegment.from(text, new Metadata());
	        Embedding embedding1 = embeddingModel.embed(segment1).content();
	        embeddingStore.add(embedding1, segment1);
	        
	        System.out.println("----  Saved embedding segment to Local Chroma : "+text);
		}        
    }
	
	//-- for testing only
	public void saveSegmentToChromaForTestingOnly(EmbeddingModel embeddingModel) 
	{
		List<String> lines = new ArrayList<String>();
		
		lines.add("The ocean is massive volume of water");
		lines.add("The sea contains fish");
		lines.add("The water in the river is not salty");
		lines.add("H2O is important");
		
		for(String text : lines)
		{	
			TextSegment segment1 = TextSegment.from(text, new Metadata());
	        Embedding embedding1 = embeddingModel.embed(segment1).content();
	        embeddingStore.add(embedding1, segment1);
	        
	        System.out.println("****  Saved TEST embedding segment to Local Chroma : "+text);
		}        
    }

	private String fineTuneDataSet(String text)
	{ 
		//year_film	 year_ceremony	ceremony	category	              name	            film	 winner
		//2022,	     2023,	        95,	        ACTOR IN A LEADING ROLE,  Austin Butler,	Elvis,	 FALSE
		String[] tokens = text.split(",");
		String yearOfTheFilm = tokens[0];
		String yearOfTheCeremony = tokens[1];
		String ceremonyId = tokens[2];
		String awardCategory = tokens[3];
		String artistName = tokens[4];
		String filmName = tokens[5];
		String awardStatus = tokens[6];
		awardStatus =  "TRUE".equals(awardStatus)?" won the award for " : " was nominated for ";
		
		
		//austin butler DID NOT WIN THE AWARD FOR actor in a leading role FOR THE FILM elvis IN THE 2023 OSCAR CEREMONY.
		String fineTunedText = artistName +" " + awardStatus +" " +  awardCategory + " for the film " + filmName +" " + " in the Oscars ceremony of "+ yearOfTheCeremony;
		fineTunedText = fineTunedText.toLowerCase();
		return fineTunedText;
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
	
	//-------------------- ollama chat section
	  //public static void main(String[] args) {
		public String generate(String text) {
	    // The model name to use (e.g., "orca-mini", "mistral", "llama2", "codellama", "phi", or
	    // "tinyllama")
			
	    //String modelName = "orca-mini";
		//String modelName = "mistral";
		String modelName = "llama2";		
		//String modelName = "codellama";
		

	    // Create and start the Ollama container
	    GenericContainer<?> ollama =
	        new GenericContainer<>("langchain4j/ollama-" + modelName + ":latest")
	            .withExposedPorts(11434);
	    ollama.start();

	    // Build the ChatLanguageModel
	    ChatLanguageModel model =
	        OllamaChatModel.builder()
	        			   .baseUrl(baseUrl(ollama))
	        			   .modelName(modelName)
	        			   .temperature(0.8)
	        			   .timeout(Durations.TEN_MINUTES)
	        			   .build();

	    // Example usage
	    //String answer = model.generate("Provide 3 short bullet points explaining why Java is awesome");
	    String answer = model.generate(text);
	    System.out.println(answer);

	    // Stop the Ollama container
	    ollama.stop();
	    return answer;
	  }

	  private String baseUrl(GenericContainer<?> ollama) {
	    return String.format("http://%s:%d", ollama.getHost(), ollama.getFirstMappedPort());
	  }
	  


}