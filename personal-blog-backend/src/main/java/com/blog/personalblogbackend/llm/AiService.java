package com.blog.personalblogbackend.llm;

import com.blog.personalblogbackend.exception.ServiceException;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class AiService {

    private final ChatModel chatModel;
    private final String apiKey;

    public AiService(ChatModel chatModel, @Value("${spring.ai.openai.api-key:}") String apiKey) {
        this.chatModel = chatModel;
        this.apiKey = apiKey;
    }

    /**
     * 调用 LLM 进行对话
     *
     * @param systemPrompt
     * @param userPrompt
     * @return
     */
    public String chat(String systemPrompt, String userPrompt) {
        if (!StringUtils.hasText(apiKey)) {
            throw new ServiceException(503, "未配置 LLM API Key（spring.ai.openai.api-key）");
        }
        try {
            ChatResponse response = chatModel.call(new Prompt(List.of(
                    new SystemMessage(systemPrompt),
                    new UserMessage(userPrompt)
            )));
            if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
                throw new ServiceException(502, "LLM 返回空响应");
            }
            String content = response.getResult().getOutput().getText();
            if (!StringUtils.hasText(content)) {
                throw new ServiceException(502, "LLM 返回空响应");
            }
            return content.trim();
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(502, "LLM 请求失败: " + e.getMessage());
        }
    }
}
