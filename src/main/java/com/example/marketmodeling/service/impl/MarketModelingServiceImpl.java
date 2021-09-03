package com.example.marketmodeling.service.impl;

import com.example.marketmodeling.config.CreatedSqlConfig;
import com.example.marketmodeling.dto.MarketModelingDto;
import com.example.marketmodeling.dto.MarketYybComProfitDto;
import com.example.marketmodeling.dto.YybDayDto;
import com.example.marketmodeling.dto.YybDto;
import com.example.marketmodeling.service.MarketModelingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MarketModelingServiceImpl implements MarketModelingService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<String> getComList(String date) {

        String sql = String.format("SELECT abc.com from (SELECT\n" +
                "        sum(buyamount) AS buyamount,\n" +
                "        SUM(sellamount) AS sellamount,\n" +
                "        SUM(buyamount - sellamount) AS jmeamount,\n" +
                "        GROUP_CONCAT(DISTINCT(`foxxcode`)) AS foxxcode,\n" +
                "        GROUP_CONCAT(DISTINCT(`name`)) AS NAMES,\n" +
                "        com\n" +
                "FROM\n" +
                "        t_market_yyb\n" +
//                "WHERE\n" +
//                "        com = '%s'\n" +
                "GROUP BY\n" +
                "        com\n" +
                "ORDER BY\n" +
                "        jmeamount DESC\n" +
                ") as abc",date);

        List<String> parameterMode = new ArrayList<>();

        try {
            parameterMode = jdbcTemplate.queryForList(sql, String.class);

        } catch (DataAccessException e) {
            System.out.println("查询数据错误");
        }

        return parameterMode;
    }

    @Override
    public List<YybDto> getYybData(String com) {

        String sql = String.format("SELECT DISTINCT(foxxcode) as foxxcode,opendate,ratio FROM t_market_yyb WHERE com ='%s'  AND buyamount > 3000 "
                , com);

        List<YybDto> parameterMode = new ArrayList<>();

        try {

            parameterMode = jdbcTemplate.query(sql,
                    new Object[]{}, new BeanPropertyRowMapper<YybDto>(YybDto.class));

        } catch (DataAccessException e) {
            System.out.println(e);
        }

        return parameterMode;

    }

    @Override
    public List<YybDayDto> getYybDayData(String foxxcode, String opendate) {

        String sql = String.format("SELECT * FROM t_market_yyb_day WHERE foxxcode = '%s' and opendate > %s  limit 6"
                , foxxcode,opendate);

        List<YybDayDto> parameterMode = new ArrayList<>();

        try {

            parameterMode = jdbcTemplate.query(sql,
                    new Object[]{}, new BeanPropertyRowMapper<YybDayDto>(YybDayDto.class));

        } catch (DataAccessException e) {
            System.out.println(e);
        }

        return parameterMode;

    }

    @Override
    public void saveData(String tableName, List<Object[]> list) {

        String sql = CreatedSqlConfig.initCreatedTable(MarketModelingDto.class, tableName);

        jdbcTemplate.batchUpdate(sql,list);

    }

    @Override
    public List<YybDto> getYybDataAndRatio(String com,Double left,Double right) {

        StringBuffer stringBuffer =new StringBuffer();
        stringBuffer.append("SELECT DISTINCT(foxxcode) as foxxcode,opendate FROM t_market_yyb WHERE com ='"+com+"' AND buyamount > 3000 ");
        if (left!=null){
            stringBuffer.append("and ratio>"+left);
        }
        if (right!=null){
            stringBuffer.append("and ratio<"+right);
        }
//        String sql = String.format("SELECT DISTINCT(foxxcode) as foxxcode,opendate FROM t_market_yyb WHERE com ='%s' and ratio>%s AND buyamount > 3000 "
//                , com,r);
        String sql = stringBuffer.toString();
        List<YybDto> parameterMode = new ArrayList<>();

        try {

            parameterMode = jdbcTemplate.query(sql,
                    new Object[]{}, new BeanPropertyRowMapper<YybDto>(YybDto.class));

        } catch (DataAccessException e) {
            System.out.println(e);
        }

        return parameterMode;
    }

    @Override
    public List<YybDto> getYybDataNotRatio(String com) {
        String sql = String.format("SELECT DISTINCT(foxxcode) as foxxcode,opendate,ratio FROM t_market_yyb WHERE com ='%s' AND buyamount > 3000 "
                , com);

        List<YybDto> parameterMode = new ArrayList<>();

        try {

            parameterMode = jdbcTemplate.query(sql,
                    new Object[]{}, new BeanPropertyRowMapper<YybDto>(YybDto.class));

        } catch (DataAccessException e) {
            System.out.println(e);
        }

        return parameterMode;
    }

    @Override
    public void saveyybComData(String tableName, List<Object[]> list) {
        String sql = CreatedSqlConfig.initCreatedTable(MarketYybComProfitDto.class, tableName);

        jdbcTemplate.batchUpdate(sql,list);
    }
}
