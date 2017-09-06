package dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import pojo.Order;
import pojo.OrderItem;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);
    
    List<OrderItem> getByOrderNoUserId(@Param("orderNo")Long orderNo,@Param("userId")Integer userId);

    Order selectByOrderNo(Long orderNo);
    
    void batchInsert(@Param("orderItemList")List<OrderItem> orderItemList);










}