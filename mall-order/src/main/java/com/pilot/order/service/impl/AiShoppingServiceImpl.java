package com.pilot.order.service.impl;

import com.pilot.order.domain.PmsProduct;
import com.pilot.order.dto.AiChatParam;
import com.pilot.order.mapper.PmsProductMapper;
import com.pilot.order.service.AiShoppingService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AiShoppingServiceImpl implements AiShoppingService {

    @Autowired
    private PmsProductMapper productMapper;

    @Autowired
    private ChatLanguageModel chatModel;

    @Override
    public String chat(AiChatParam param) {
        Long productId = param.getProductId();
        String userMessage = param.getMessage();

        PmsProduct product = productMapper.selectById(productId);
        if (product == null) {
            return "商品不存在";
        }

        String context = buildContext(product);
        String prompt = buildPrompt(context, userMessage);

        log.info("AI 导购请求 - 商品ID: {}, 用户问题: {}", productId, userMessage);

        String response = chatModel.generate(prompt);

        log.info("AI 导购响应 - 商品ID: {}, 回复: {}", productId, response);

        return response;
    }

    private String buildContext(PmsProduct product) {
        StringBuilder sb = new StringBuilder();
        sb.append("商品信息：\n");
        sb.append("- 商品名称：").append(product.getName()).append("\n");
        sb.append("- 商品价格：").append(product.getPrice()).append("元\n");
        sb.append("- 商品描述：").append(product.getDescription()).append("\n");
        return sb.toString();
    }

    private String buildPrompt(String context, String userMessage) {
        return String.format(
                "你是一个专业的购物导购助理。请根据以下商品信息回答用户的问题。\n\n%s\n\n用户问题：%s\n\n请用友好、专业的语气回答，突出商品的优点和特色。",
                context, userMessage
        );
    }
}
