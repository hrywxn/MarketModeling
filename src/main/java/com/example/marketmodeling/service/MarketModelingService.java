package com.example.marketmodeling.service;

import com.example.marketmodeling.dto.YybDayDto;
import com.example.marketmodeling.dto.YybDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MarketModelingService {

    List<String> getComList(String date);

    List<YybDto> getYybData(String com);

    List<YybDayDto> getYybDayData(String foxxcode, String opendate);

    void saveData(String tableName, List<Object[]> list);

    List<YybDto> getYybDataAndRatio(String com,Double left,Double right);

    List<YybDto> getYybDataNotRatio(String com);

    void saveyybComData(String t_market_yyb_com, List<Object[]> batchArgs);
}
