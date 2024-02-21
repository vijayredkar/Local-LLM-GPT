package com.credit.check.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

import org.springframework.stereotype.Service;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.shaded.org.awaitility.Durations;

import java.time.Duration;

@Service
public class ChatLocalLLMLang4JOllamaService {

	
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
