package com.yishang.yspay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yishang.yspay.bean.PayOrder;
import com.yishang.yspay.bean.SysUser;
import com.yishang.yspay.bean.vo.CollectVo;
import com.yishang.yspay.bean.vo.PayOrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Map;

/**
 * ${comments}
 *
 * @author zjf
 * @email 962700753@qq.com
 * @date 2020-06-22 09:11:10
 */
@Mapper
public interface PayOrderMapper extends BaseMapper<PayOrder> {

    /**
     * 商户流水查询
     * lqp
     *
     * @param orderPage
     * @param search
     * @return
     */
    IPage<PayOrder> streamPage(Page<PayOrder> orderPage, @Param("search") PayOrderVo search);

    /**
     * 根据支付状态及查询条件求订单数
     * lqp
     *
     * @param search
     * @return
     */
    Integer countByPaystate(@Param("search") PayOrderVo search, @Param("paystate") Integer paystate);

    /**
     * 根据查询条件求已支付订单金额总数
     * lqp
     *
     * @param search
     * @return
     */
    BigDecimal sumOrderMoney(@Param("search") PayOrderVo search);


    /**
     * 查询商户交易总汇
     *
     * @param page
     * @param params
     * @param user
     * @return
     */
    IPage<CollectVo> collectlist(Page<CollectVo> page, Map<String, String> params, SysUser user);

    /**
     * 根据开始和结束时间获取交易总额、交易笔数
     * lqp
     *
     * @param users
     * @param startTime
     * @param endTime
     * @return
     */
    CollectVo homePageTodayCollect(@Param("search") SysUser users, @Param("startTime") String startTime, @Param("endTime") String endTime);
}
