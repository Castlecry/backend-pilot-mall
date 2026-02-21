package com.pilot.order.controller;

import com.pilot.common.api.CommonResult;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order/ai")//demo 实际未使用
public class OrderAiController {

    private final ChatLanguageModel chatModel;

    public OrderAiController(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/simple-chat")
    public CommonResult<String> chat(@RequestParam String message) {
        // 调用 Ollama 里的模型
        String response = chatModel.generate(message);
        return CommonResult.success(response);
    }
}