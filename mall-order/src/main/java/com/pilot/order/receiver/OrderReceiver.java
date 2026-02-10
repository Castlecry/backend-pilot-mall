package com.pilot.order.receiver;

import com.pilot.order.config.RabbitMQConfig;
import com.pilot.order.domain.OmsOrder;
import com.pilot.order.dto.OrderMessage;
import com.pilot.order.mapper.OmsOrderMapper;
import com.pilot.order.mapper.PmsProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class OrderReceiver {

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private PmsProductMapper productMapper;

    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void processOrder(OrderMessage message) {
        try {
            log.info("收到订单消息，订单号: {}, 用户ID: {}, 商品ID: {}", 
                    message.getOrderSn(), message.getUserId(), message.getProductId());

            Integer currentStock = productMapper.selectById(message.getProductId()).getStock() - 1;
            productMapper.updateStock(message.getProductId(), currentStock);

            OmsOrder order = new OmsOrder();
            order.setOrderSn(message.getOrderSn());
            order.setUserId(message.getUserId());
            order.setProductId(message.getProductId());
            order.setTotalAmount(message.getTotalAmount());
            order.setStatus(message.getStatus());
            order.setCreateTime(java.sql.Timestamp.valueOf(LocalDateTime.now()));

            orderMapper.insert(order);

            log.info("订单入库成功，订单号: {}", message.getOrderSn());
        } catch (Exception e) {
            log.error("订单处理失败，订单号: {}", message.getOrderSn(), e);
        }
    }
}
