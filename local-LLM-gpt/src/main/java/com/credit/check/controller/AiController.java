package com.credit.check.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.credit.check.service.CalculatorService;
import com.credit.check.service.ChatLocalLLMLang4JOllamaService;
import com.credit.check.service.ChatLocalLLMOllamaService;
import com.credit.check.service.ChatLocalLLMService;
import com.credit.check.service.ChatService;
import com.credit.check.service.DocLoadChromaService;
import com.credit.check.service.DocLoadOscarsChromaOllamaService;
import com.credit.check.service.DocLoadOscarsChromaService;
import com.credit.check.service.EmbeddingLocalLLMService;
import com.credit.check.service.EmbeddingService;
import com.credit.check.service.ModelService;
import com.credit.check.service.PersistenceService;
import com.credit.check.service.RetrievalService;
import com.credit.check.service.SpellCheckerService;
import com.credit.check.service.TranslatorLocalLLMService;
import com.credit.check.service.TranslatorService;

@RestController
@RequestMapping(value = "/ai")
public class AiController  
{

	@Autowired
	private ModelService modelSvc;
	
	@Autowired
	private CalculatorService calculatorSvc;

	@Autowired
	private ChatService chatSvc;
	
	@Autowired
	private ChatLocalLLMService chatLocalSvc;
	
	/*
	@Autowired 
	private ChatLocalLLMOllamaService chatLocalOllamaSvc;
	*/
	@Autowired 
	 private ChatLocalLLMLang4JOllamaService chatLocalLLMLang4JOllamaSvc;
	 
	
	@Autowired
	private EmbeddingService embeddingSvc;
	
	@Autowired
	private EmbeddingLocalLLMService embeddingLocalSvc;
	
	@Autowired
	private TranslatorService translatorSvc;
	
	@Autowired
	private TranslatorLocalLLMService translatorLocalSvc;
	
	@Autowired
	private SpellCheckerService spellChkSvc;
	
	@Autowired
	private PersistenceService persistenceSvc;
	
	@Autowired
	private RetrievalService retrievalSvc;
	
	@Autowired
	private DocLoadChromaService docRetrievalSvc;
	
	@Autowired
	private DocLoadOscarsChromaService docRetrievalOscarsSvc;
	
	@Autowired
	private DocLoadOscarsChromaOllamaService docRetrievalOscarsChromaOllamaSvc;
	

	@GetMapping("/chat")
	public ResponseEntity<String> chat(@RequestParam("text") String text) 
	{
		String response = chatSvc.generate(modelSvc.getDemoModel(), text);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/chat-local-llm")
	public ResponseEntity<String> chatWithLocalLLM(@RequestParam("text") String text) 
	{
		String response = chatLocalSvc.generate(modelSvc.getLocalModel(), text);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	@GetMapping("/chat-local-llm-ollama")
	public ResponseEntity<String> chatWithLocalLLMOllama(@RequestParam("text") String text) 
	{
		//chatLocalOllamaSvc.generate(text);
		String response = chatLocalLLMLang4JOllamaSvc.generate(text);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	

	@GetMapping("/calculate")
	public ResponseEntity<String> calculate(@RequestParam("text") String text) 
	{
		String response = calculatorSvc.calculate(modelSvc.getDemoModel(), text);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}	

	@GetMapping("/embed")
	public ResponseEntity<String> embed(@RequestParam("text") String text) 
	{
		String response = embeddingSvc.embed(modelSvc.getEmbeddingModel(), text);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/embed-local-llm")
	public ResponseEntity<String> embedWithLocalLLMAndChroma(@RequestParam("text") String text) 
	{
		String response = embeddingLocalSvc.embed(modelSvc.getEmbeddingModel(), text);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/translate")
	public ResponseEntity<String> translate(@RequestParam("text") String text, @RequestParam(defaultValue = "chinese") String language) 
	{
		String response = translatorSvc.translate(modelSvc.getDemoModel(), text, language);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/*
	@GetMapping("/translate-local-llm-1")
	public ResponseEntity<String> translateLocalLLM_1(@RequestParam("text") String text, @RequestParam(defaultValue = "chinese") String language) 
	{
		String response = translatorLocalSvc.translate_1(modelSvc.getLocalModel(), text, language);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	*/
	
	@GetMapping("/translate-local-llm-2")
	public ResponseEntity<String> translateLocalLLM_2(@RequestParam("text") String text, @RequestParam(defaultValue = "chinese") String language) 
	{
		String response = translatorLocalSvc.translate_2(modelSvc.getLocalModel(), text, language);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/spellCheck")
	public ResponseEntity<String> spellCheck(@RequestParam("text") String text, @RequestParam(defaultValue = "english") String language) 
	{
		String response = spellChkSvc.spellCheck(modelSvc.getDemoModel(), text, language);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/spellCheck-local-llm")
	public ResponseEntity<String> spellCheckLocal(@RequestParam("text") String text, @RequestParam(defaultValue = "english") String language) 
	{
		String response = spellChkSvc.spellCheck(modelSvc.getLocalModel(), text, language);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
		
	@GetMapping("/persistDemo")
	public ResponseEntity<String> persistDemo(@RequestParam("showName") String showName) {
		String response = persistenceSvc.demo(modelSvc.getDemoModel(), Boolean.valueOf(showName));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/retrieve")
	public ResponseEntity<String> retrieve(@RequestParam("file") String file, @RequestParam("text") String text) {
		String response = retrievalSvc.retrieve(modelSvc.getLocalModel(), modelSvc.getEmbeddingModel(), file, text);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/retrieve-local-llm")
	public ResponseEntity<String> retrieveLocalLLM(@RequestParam(defaultValue = "no-file-to-load") String file, @RequestParam("text") String text, @RequestParam(defaultValue = "1") int maxLimit, @RequestParam(defaultValue = "false") Boolean refreshDb) 
	{
		String response = docRetrievalSvc.retrieveFromSingleRow(modelSvc.getLocalModel(), modelSvc.getEmbeddingModel(), file, text, maxLimit, refreshDb);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/retrieve-local-llm-ollama")
	public ResponseEntity<String> retrieveLocalLLMOllama(@RequestParam(defaultValue = "no-file-to-load") String file, @RequestParam("text") String text, @RequestParam(defaultValue = "1") int maxLimit, @RequestParam(defaultValue = "false") Boolean refreshDb, @RequestParam(defaultValue = "0.5") double minScore) 
	{
		String response = docRetrievalOscarsSvc.retrieveFromSingleRow(modelSvc.getLocalModel(), modelSvc.getEmbeddingModel(), file, text, maxLimit, refreshDb, minScore);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/retrieve-local-llm-ollama-chroma-oscars")
	public ResponseEntity<String> retrieveLocalLLMOllamaChromaOscars(@RequestParam(defaultValue = "no-file-to-load") String file, @RequestParam("text") String text, @RequestParam(defaultValue = "1") int maxLimit, @RequestParam(defaultValue = "false") Boolean refreshDb, @RequestParam(defaultValue = "0.5") double minScore) 
	{
		String response = docRetrievalOscarsChromaOllamaSvc.chromaOlamaRetrieveOrchestrate(modelSvc.getLocalModel(), modelSvc.getEmbeddingModel(), file, text, maxLimit, refreshDb, minScore);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
}
