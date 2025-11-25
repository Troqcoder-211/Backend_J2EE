package j2ee.ourteam.services.bot;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath:/prompts/rag-prompt.st")
    private Resource ragPromptTemplate;

    public RagService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    public String retrieveInfo(String message) {
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder().query(message).topK(4).build()
        );

        return docs.stream().map(Document::getText).reduce("", (a, b) -> a + "\n" + b);
    }

    public String generateReply(String systemInfo, String userMessage) {
        SystemPromptTemplate system = new SystemPromptTemplate(ragPromptTemplate);
        var systemMsg = system.createMessage(Map.of("information", systemInfo));

        Prompt prompt = new Prompt(List.of(
                systemMsg,
                new UserMessage(userMessage)
        ));

        return chatClient.prompt(prompt).call().content();
    }

    public String retrieveAndGenerate(String message) {

        // 🔍 1. Retrieve documents
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .topK(4)
                        .build()
        );

        String info = docs.stream()
                .map(Document::getText)
                .reduce("", (a, b) -> a + "\n" + b);

        System.out.println("🔍 Retrieved documents: \n" + info);

        // 🧠 2. Build system prompt
        SystemPromptTemplate system = new SystemPromptTemplate(ragPromptTemplate);
        var systemMsg = system.createMessage(Map.of("information", info));

        // 📝 3. Create prompt
        Prompt prompt = new Prompt(List.of(
                systemMsg,
                new UserMessage(message)
        ));

        // 🤖 4. Ask model
        String result = chatClient.prompt(prompt).call().content();
        System.out.println("🤖 AI Response: " + result);

        return result;
    }
}
