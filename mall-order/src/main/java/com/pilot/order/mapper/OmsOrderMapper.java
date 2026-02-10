package com.pilot.order.mapper;

import com.pilot.order.domain.OmsOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OmsOrderMapper {
    int insert(OmsOrder order);

    OmsOrder selectByOrderSn(String orderSn);

    List<OmsOrder> selectByUserId(Long userId);
}
