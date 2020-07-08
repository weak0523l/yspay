package com.yishang.yspay.bean.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易汇总
 */
@Data
public class CollectVo {

    /**
     * 订单日期
     */
    private String orderDate;

    /**
     * 总金额
     */
    private BigDecimal money;

    /**
     * 订单数
     */
    private String orderCount;

    /**
     * 商户名称
     */
    private String merchname;

    /**
     * 网点名称
     */
    private String branchname;

    /**
     * 行社名称
     */
    private String clubname;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;


    /**
     * 退款订单数
     */
    private BigDecimal refundCount;

}
