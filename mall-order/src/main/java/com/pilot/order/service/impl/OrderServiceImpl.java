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
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor; // 【新增】Lombok 注解
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor //使用构造器注入，解决字段注入警告
public class OrderServiceImpl implements OrderService {

    private final PmsProductMapper productMapper;
    private final OmsOrderMapper orderMapper;
    private final StringRedisTemplate redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.order}")
    private String orderQueue;

    private DefaultRedisScript<Long> decreaseStockScript;

    @PostConstruct
    public void initDecreaseStockScript() {
        decreaseStockScript = new DefaultRedisScript<>();
        decreaseStockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/decrease_stock.lua")));
        decreaseStockScript.setResultType(Long.class);
    }

    @Override
    public OrderResult createOrder(OrderCreateParam param, Long userId) {
        Long productId = param.getProductId();

        if (!checkRepeatOrder(userId, productId)) {
            throw new RuntimeException("请勿重复下单");
        }

        log.info("用户{}开始秒杀商品{}", userId, productId);

        // 执行 Lua 脚本
        Long result = redisTemplate.execute(
                decreaseStockScript,
                Collections.singletonList(String.valueOf(productId))
        );
        // 先判断是否为 null (防御性编程)
        if (result == null) {
            log.error("Redis脚本执行返回NULL，用户{}，商品{}", userId, productId);
            throw new RuntimeException("系统异常");
        }

        // 使用 equals 比较对象，避免自动拆箱导致的隐患
        if (result == -1) {
            log.warn("用户{}秒杀商品{}库存不足或商品未预热", userId, productId);
            throw new RuntimeException("商品不存在或未预热");
        }

        if (result == 0) {
            log.warn("用户{}秒杀商品{}库存不足", userId, productId);
            throw new RuntimeException("已售罄");
        }

        // 剩下的情况就是成功了，继续业务逻辑
        PmsProduct product = productMapper.selectById(productId);
        if (product == null) {
            log.warn("用户{}秒杀商品{}不存在", userId, productId);
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

    @Override
    public boolean checkRepeatOrder(Long userId, Long productId) {
        String key = "seckill:order:unique:" + userId + ":" + productId;
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, "1", 5, TimeUnit.MINUTES);
        // flag 也有可能为 null (网络抖动等)，这种写法是安全的
        return Boolean.TRUE.equals(flag);
    }
}