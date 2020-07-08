package com.yishang.yspay.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yishang.yspay.bean.PayOrder;
import com.yishang.yspay.bean.SysUser;
import com.yishang.yspay.bean.vo.CollectVo;
import com.yishang.yspay.bean.vo.PayOrderVo;
import com.yishang.yspay.mapper.PayOrderMapper;
import com.yishang.yspay.service.PayOrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrder> implements PayOrderService {

    @Autowired
    PayOrderMapper payOrderMapper;

    @Override
    public IPage<PayOrder> streamPage(Page<PayOrder> orderPage, PayOrderVo search, Map<String, String> params) {
        //查询条件
        String startTimeStr = params.get("startTime");
        if (startTimeStr != null && StringUtils.isNotBlank(startTimeStr)) {
            search.setStartTime(startTimeStr);
        }
        String endTimeStr = params.get("endTime");
        if (endTimeStr != null && StringUtils.isNotBlank(endTimeStr)) {
            search.setEndTime(endTimeStr);
        }
        String paytype = params.get("paytype");
        if (paytype != null && StringUtils.isNotBlank(paytype)) {
            search.setPaytype(paytype);
        }
        String merchantName = params.get("merchantName");
        if (merchantName != null && StringUtils.isNotBlank(merchantName)) {
            search.setMerchantName(merchantName);
        }
        String swiffpassmerchno = params.get("swiffpassmerchno");
        if (swiffpassmerchno != null && StringUtils.isNotBlank(swiffpassmerchno)) {
            search.setSwiffpassmerchno(swiffpassmerchno);
        }
        String paychannel = params.get("paychannel");
        if (paychannel != null && StringUtils.isNotBlank(paychannel)) {
            search.setPaychannel(Integer.parseInt(paychannel));
        }
        String operauser = params.get("operauser");
        if (operauser != null && StringUtils.isNotBlank(operauser)) {
            search.setOperauser(operauser);
        }
        return payOrderMapper.streamPage(orderPage, search);
    }

    @Override
    public Map<String, Object> statisticsOrder(PayOrderVo search, Map<String, String> params) {
        //查询条件
        String startTimeStr = params.get("startTime");
        if (startTimeStr != null && StringUtils.isNotBlank(startTimeStr)) {
            search.setStartTime(startTimeStr);
        }
        String endTimeStr = params.get("endTime");
        if (endTimeStr != null && StringUtils.isNotBlank(endTimeStr)) {
            search.setEndTime(endTimeStr);
        }
        String paytype = params.get("paytype");
        if (paytype != null && StringUtils.isNotBlank(paytype)) {
            search.setPaytype(paytype);
        }
        String merchantName = params.get("merchantName");
        if (merchantName != null && StringUtils.isNotBlank(merchantName)) {
            search.setMerchantName(merchantName);
        }
        String swiffpassmerchno = params.get("swiffpassmerchno");
        if (swiffpassmerchno != null && StringUtils.isNotBlank(swiffpassmerchno)) {
            search.setSwiffpassmerchno(swiffpassmerchno);
        }
        String paychannel = params.get("paychannel");
        if (paychannel != null && StringUtils.isNotBlank(paychannel)) {
            search.setPaychannel(Integer.parseInt(paychannel));
        }
        String operauser = params.get("operauser");
        if (operauser != null && StringUtils.isNotBlank(operauser)) {
            search.setOperauser(operauser);
        }
        Integer havePaid = payOrderMapper.countByPaystate(search, 1);
        Integer notPaid = payOrderMapper.countByPaystate(search, 0);
        BigDecimal moneyNum = payOrderMapper.sumOrderMoney(search);
        Map<String, Object> map = new HashMap<>();
        map.put("havePaid", havePaid);
        map.put("notPaid", notPaid);
        map.put("moneyNum", moneyNum);
        return map;
    }

    @Override
    public IPage<CollectVo> collectlist(Page<CollectVo> page, Map<String, String> params, SysUser user) {
        return payOrderMapper.collectlist(page, params, user);
    }

    @Override
    public Map<String, Object> homePageCollect(SysUser user) {
        Map<String, Object> map = new HashMap<>();
        //获取今日
        Calendar calNow = Calendar.getInstance();
        calNow.set(Calendar.HOUR_OF_DAY, 0);
        calNow.set(Calendar.SECOND, 0);
        calNow.set(Calendar.MINUTE, 0);
        calNow.set(Calendar.MILLISECOND, 0);
        String startTime = calNow.getTime().toLocaleString();
        calNow.set(Calendar.HOUR_OF_DAY, 23);
        calNow.set(Calendar.SECOND, 59);
        calNow.set(Calendar.MINUTE, 59);
        calNow.set(Calendar.MILLISECOND, 59);
        String endTime = calNow.getTime().toLocaleString();
        CollectVo todayCollect = payOrderMapper.homePageTodayCollect(user, startTime, endTime);
        map.put("todayMoney", todayCollect.getMoney());
        map.put("todayCount", todayCollect.getOrderCount());
        //累计
        CollectVo totalCollect = payOrderMapper.homePageTodayCollect(user, null, null);
        map.put("totalMoney", totalCollect.getMoney());
        map.put("totalCount", totalCollect.getOrderCount());
        return map;
    }

    @Override
    public List<String> findLatelyDate() {
        List<String> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 7; i > 0; i--) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -i);
            Date time = c.getTime();
            String preDay = sdf.format(time);
            list.add(preDay);
        }
        return list;
    }

    @Override
    public Map<String, Object> findLatelyLine(SysUser user, List<String> list) {
        Map<String, Object> map = new HashMap<>();
        List<String> dealList = new ArrayList<>();
        List<String> timesList = new ArrayList<>();
        for (String s : list) {
            String startTime = s + " " + "00:00:00";
            String endTime = s + " " + "23:59:59";
            CollectVo collectVo = payOrderMapper.homePageTodayCollect(user, startTime, endTime);
            String money = collectVo.getMoney() + "";
            if ("".equals(money.trim()) || StringUtils.isBlank(money.trim()) || "null".equals(money.trim())) {
                money = "0";
            }
            dealList.add(money);
            timesList.add(collectVo.getOrderCount());
        }
        map.put("expectedData", dealList);
        map.put("actualData", timesList);
        return map;
    }

    @Override
    public Map<String, String> queryTime(String type) {
        Map<String, String> map = new HashMap<>();
        //获取当天起始时间
        if ("1".equals(type)) {
            Calendar calNow = Calendar.getInstance();
            calNow.set(Calendar.HOUR_OF_DAY, 0);
            calNow.set(Calendar.SECOND, 0);
            calNow.set(Calendar.MINUTE, 0);
            calNow.set(Calendar.MILLISECOND, 0);
            String startTime = calNow.getTime().toLocaleString();
            calNow.set(Calendar.HOUR_OF_DAY, 23);
            calNow.set(Calendar.SECOND, 59);
            calNow.set(Calendar.MINUTE, 59);
            calNow.set(Calendar.MILLISECOND, 59);
            String endTime = calNow.getTime().toLocaleString();
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            return map;
        } else if ("2".equals(type)) {
            //获取昨天起始时间
            Calendar calYesterday = Calendar.getInstance();
            calYesterday.set(Calendar.HOUR_OF_DAY, -24);
            calYesterday.set(Calendar.SECOND, 0);
            calYesterday.set(Calendar.MINUTE, 0);
            calYesterday.set(Calendar.MILLISECOND, 0);
            String startTime = calYesterday.getTime().toLocaleString();
            calYesterday.set(Calendar.HOUR_OF_DAY, 23);
            calYesterday.set(Calendar.SECOND, 59);
            calYesterday.set(Calendar.MINUTE, 59);
            calYesterday.set(Calendar.MILLISECOND, 59);
            String endTime = calYesterday.getTime().toLocaleString();
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            return map;
        } else if ("3".equals(type)) {
            //获取本周
            Calendar cal = Calendar.getInstance();
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            //设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            //获得当前日期是一个星期的第几天
            int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (dayWeek == 1) {
                dayWeek = 8;
            }
            // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
            cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - dayWeek);
            String startTime = cal.getTime().toLocaleString();

            cal.add(Calendar.DATE, 4 + cal.getFirstDayOfWeek());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.MILLISECOND, 59);
            String endTime = cal.getTime().toLocaleString();

            map.put("startTime", startTime);
            map.put("endTime", endTime);
            return map;
        } else {
            //获取本月
            Calendar calMonth = Calendar.getInstance();
            calMonth.set(calMonth.get(Calendar.YEAR), calMonth.get(Calendar.MONDAY), calMonth.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            calMonth.set(Calendar.DAY_OF_MONTH, calMonth.getActualMinimum(Calendar.DAY_OF_MONTH));
            String startTime = calMonth.getTime().toLocaleString();
            calMonth.set(calMonth.get(Calendar.YEAR), calMonth.get(Calendar.MONDAY), calMonth.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            calMonth.set(Calendar.DAY_OF_MONTH, calMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
            calMonth.set(Calendar.HOUR_OF_DAY, 23);
            calMonth.set(Calendar.SECOND, 59);
            calMonth.set(Calendar.MINUTE, 59);
            calMonth.set(Calendar.MILLISECOND, 59);
            String endTime = calMonth.getTime().toLocaleString();
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            return map;
        }
    }


}
