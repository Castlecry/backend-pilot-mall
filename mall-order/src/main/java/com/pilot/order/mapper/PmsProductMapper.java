package com.pilot.order.mapper;

import com.pilot.order.domain.PmsProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PmsProductMapper {
    PmsProduct selectById(@Param("id") Long id);

    List<PmsProduct> selectAll();

    int updateStock(@Param("id") Long id, @Param("stock") Integer stock);
}
