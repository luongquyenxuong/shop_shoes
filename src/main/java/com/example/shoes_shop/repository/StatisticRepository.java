package com.example.shoes_shop.repository;

import com.example.shoes_shop.dto.ChartDTO;
import com.example.shoes_shop.dto.StatisticDTO;
import com.example.shoes_shop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<Order, Long> {

    @Query(name = "getStatistic30Day",nativeQuery = true)
    List<StatisticDTO> getStatistic30Day();

    @Query(name = "getStatisticDayByDay",nativeQuery = true)
    List<StatisticDTO> getStatisticDayByDay(String toDate, String formDate);

    @Query(name = "getStatisticOrderCancel",nativeQuery = true)
    List<ChartDTO> getStatisticOrderCancel();
    @Query(name = "getStatisticOrderRefund",nativeQuery = true)
    List<ChartDTO> getStatisticOrderRefund();

    @Query(name = "getViewPurchaseRatio",nativeQuery = true)
    List<ChartDTO> getViewPurchaseRatio();

}
