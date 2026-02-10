package com.pilot.order.controller;

import com.pilot.common.api.CommonResult;
import com.pilot.order.dto.AiChatParam;
import com.pilot.order.service.AiShoppingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiShoppingController {

    @Autowired
    private AiShoppingService aiShoppingService;

    @PostMapping("/chat")
    public CommonResult<String> chat(@RequestBody AiChatParam param) {
        String response = aiShoppingService.chat(param);
        return CommonResult.success(response);
    }
}
