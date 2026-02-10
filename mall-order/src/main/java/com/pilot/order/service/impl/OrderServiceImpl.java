package com.pilot.order.service.impl;

import com.pilot.order.domain.OmsOrder;
import com.pilot.order.domain.PmsProduct;
import com.pilot.order.dto.OrderCreateParam;
import com.pilot.order.dto.OrderListResult;
import com.pilot.order.dto.OrderMessage;
import com.pilot.order.dto.OrderResult;
import com.pilot.order.mapper.OmsOrderMapper;
import com.pilot.order.mapper.PmsProductMapper;
import com.pilot.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private PmsProductMapper productMapper;

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.order}")
    private String orderQueue;

    private DefaultRedisScript<Long> decreaseStockScript;

    @Autowired
    public void initDecreaseStockScript() {
        decreaseStockScript = new DefaultRedisScript<>();
        decreaseStockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/decrease_stock.lua")));
        decreaseStockScript.setResultType(Long.class);
    }

    @Override
    public OrderResult createOrder(OrderCreateParam param, Long userId) {
        Long productId = param.getProductId();

        Long result = redisTemplate.execute(
                decreaseStockScript,
                Collections.singletonList(String.valueOf(productId))
        );

        if (result == null || result == -1) {
            throw new RuntimeException("商品不存在");
        }

        if (result == 0) {
            throw new RuntimeException("已售罄");
        }

        PmsProduct product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        String orderSn = generateOrderSn();

        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setOrderSn(orderSn);
        orderMessage.setUserId(userId);
        orderMessage.setProductId(productId);
        orderMessage.setTotalAmount(product.getPrice());
        orderMessage.setStatus(0);

        rabbitTemplate.convertAndSend(orderQueue, orderMessage);

        log.info("订单消息已发送到队列，订单号: {}, 用户ID: {}, 商品ID: {}", orderSn, userId, productId);

        OrderResult orderResult = new OrderResult();
        orderResult.setOrderSn(orderSn);
        orderResult.setProductId(productId);
        orderResult.setTotalAmount(product.getPrice());
        orderResult.setStatus(0);

        return orderResult;
    }

    private String generateOrderSn() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public List<OrderListResult> listOrders(Long userId) {
        List<OmsOrder> orders = orderMapper.selectByUserId(userId);
        return orders.stream()
                .map(order -> {
                    OrderListResult result = new OrderListResult();
                    BeanUtils.copyProperties(order, result);
                    return result;
                })
                .collect(Collectors.toList());
    }
}
