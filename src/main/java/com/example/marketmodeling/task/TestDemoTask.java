package com.example.marketmodeling.task;

import com.example.marketmodeling.dto.MarketModelingDto;
import com.example.marketmodeling.dto.YybDayDto;
import com.example.marketmodeling.dto.YybDto;
import com.example.marketmodeling.service.MarketModelingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TestDemoTask {

    @Autowired
    MarketModelingService marketModelingService;

    @PostConstruct
    public void startTask() {

        LocalDate localDate = LocalDate.of(2020, 8, 31);
        Boolean f =true;
        while (f) {
            //找出营业部分类
            List<String> comList = marketModelingService.getComList(localDate.toString().replaceAll("-", ""));

            //通过建模sql找出营业部历史数据
            for (String com : comList) {

                if (com.contains("机构专用") || com.contains("深股通专用")) {
                    continue;
                }

                //计算机构当天买入股票,涨跌幅不同对后面5天收益比例
//                buyProbability(com);


                List<YybDto> yybDtoList = marketModelingService.getYybData(com);


                List<Object[]> batchArgs = new LinkedList<>();

                List<MarketModelingDto> marketModelingDtoList = new ArrayList<>();

                //初始化
                for (YybDto yybDto : yybDtoList) {

                    int date = Integer.parseInt(yybDto.getOpendate());

                    if (date > 20210831) {
                        continue;
                    }

                    //找出大于发现日的股票日期后续五天数据
                    List<YybDayDto> yybDayDtoList = marketModelingService.getYybDayData(yybDto.getFoxxcode(), yybDto.getOpendate());

                    if (yybDayDtoList.size() < 6) {
                        continue;
                    }

                    //计算1 - 5 日内的胜率
                    batchArgs.add(new Object[]{yybDto.getFoxxcode(), yybDto.getOpendate(), com,yybDto.getRatio(), getRatio(yybDayDtoList, 1)
                            , getRatio(yybDayDtoList, 2), getRatio(yybDayDtoList, 3), getRatio(yybDayDtoList, 4),
                            getRatio(yybDayDtoList, 5)});


                    MarketModelingDto marketModelingDto = new MarketModelingDto();
                    marketModelingDto.setOpendate(yybDto.getOpendate());
                    marketModelingDto.setCom(com);
                    marketModelingDto.setRatio(yybDto.getRatio());
                    marketModelingDto.setT1(getRatio(yybDayDtoList, 1));
                    marketModelingDto.setT2(getRatio(yybDayDtoList, 2));
                    marketModelingDto.setT3(getRatio(yybDayDtoList, 3));
                    marketModelingDto.setT4(getRatio(yybDayDtoList, 4));
                    marketModelingDto.setT5(getRatio(yybDayDtoList, 5));


                    marketModelingDtoList.add(marketModelingDto);

                }

                marketModelingService.saveData("t_market_yyb_profit", batchArgs);

                double comratio = doComRatio(marketModelingDtoList, 1);

                batchArgs = new LinkedList<>();
                batchArgs.add(new Object[]{"", "", com, comratio, doComRatio(marketModelingDtoList, 2),
                        doComRatio(marketModelingDtoList, 3), doComRatio(marketModelingDtoList, 4),
                        doComRatio(marketModelingDtoList, 5)});

                marketModelingService.saveyybComData("t_market_yyb_com", batchArgs);


            }
            if(localDate.isEqual(LocalDate.now())){
                f = false;
            }
            localDate = localDate.plusDays(1);
        }

        //三到五
        int a = 0;
    }

//    private void buyProbability(String com){
//
//        List<YybDto> yybDtoList = marketModelingService.getYybDataNotRatio(com);
//
//        List<YybDto> yybDtoListLeft = yybDtoList.stream().filter(x->x.getRatio()>9.9).collect(Collectors.toList());
//        List<YybDto> yybDtos = yybDtoList.stream().filter(x->x.getRatio()>0.0&&x.getRatio()<9.9).collect(Collectors.toList());
//        List<YybDto> yybDtoListRight = yybDtoList.stream().filter(x->x.getRatio()<0.0).collect(Collectors.toList());
//        calculateIncome(yybDtoListLeft);
//        calculateIncome(yybDtos);
//        calculateIncome(yybDtoListRight);
//    }
//
//    private void calculateIncome(List<YybDto> yybDtoList){
//        if(yybDtoList!=null){
//            for (YybDto yybDto : yybDtoList) {
//                int date = Integer.parseInt(yybDto.getOpendate());
//
//                if (date > 20210831) {
//                    continue;
//                }
//
//                //找出大于发现日的股票日期后续五天数据
//                List<YybDayDto> yybDayDtoList = marketModelingService.getYybDayData(yybDto.getFoxxcode(), yybDto.getOpendate());
//
//                if (yybDayDtoList.size() < 6) {
//                    continue;
//                }
//                double price = yybDayDtoList.get(0).getPrice();
//
//
//            }
//        }
//    }

    private double doComRatio(List<MarketModelingDto> marketModelingDtoList, int i) {
        double ratio = 0.00;

        double countT1All = marketModelingDtoList.size();
        if (countT1All < 1) {
            return ratio;
        }

        if (i == 1) {
            double countT1Z = marketModelingDtoList.stream().filter(x -> x.getT1() > 0).count();
            ratio = countT1Z / countT1All;
        } else if (i == 2) {

            double countT1Z = marketModelingDtoList.stream().filter(x -> x.getT2() > 0).count();
            ratio = countT1Z / countT1All;
        } else if (i == 3) {

            double countT1Z = marketModelingDtoList.stream().filter(x -> x.getT3() > 0).count();
            ratio = countT1Z / countT1All;
        } else if (i == 4) {

            double countT1Z = marketModelingDtoList.stream().filter(x -> x.getT4() > 0).count();
            ratio = countT1Z / countT1All;
        } else if (i == 5) {

            double countT1Z = marketModelingDtoList.stream().filter(x -> x.getT5() > 0).count();
            ratio = countT1Z / countT1All;
        }

        return ratio;
    }

    private double getRatio(List<YybDayDto> yybDayDtoList, int i) {
        double t = yybDayDtoList.get(0).getPrice();

        double ratio = (yybDayDtoList.get(i).getPrice() - t) / t;

        return ratio;
    }
}
