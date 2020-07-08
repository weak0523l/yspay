package com.yishang.yspay.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yishang.yspay.bean.PayOrder;
import com.yishang.yspay.bean.SysUser;
import com.yishang.yspay.bean.vo.CollectVo;
import com.yishang.yspay.bean.vo.PayOrderVo;
import com.yishang.yspay.service.PayOrderService;
import com.yishang.yspay.service.SysUserService;
import com.yishang.yspay.util.R;
import com.yishang.yspay.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;
import java.util.Map;


/**
 * ${comments}
 *
 * @author zjf
 * @email 962700753@qq.com
 * @date 2020-06-22 09:11:10
 */
@RestController
@Api(tags = "订单表表接口")
@RequestMapping("pay/order")
public class PayOrderController {
    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 列表
     * pageNow 当前是多少页
     * pageNum 每页显示多少行
     */
    @ApiOperation(value = "分页获取订单", notes = "获取订单列表")
    @ApiImplicitParam(name = "params", value = "表单条件查询对象(page-当前页,limit-每页条数 必传)", required = true, dataType = "Map<String,String>", paramType = "body")
    @PostMapping("/list")
    public R list(@RequestBody Map<String, String> params) {
        String page = params.get("page");
        String limit = params.get("limit");
        //校验参数
        if (page == null || StringUtils.isBlank(page)) {
            return R.error(400, "page为空！");
        }
        if (limit == null || StringUtils.isBlank(limit)) {
            return R.error(400, "limit为空！");
        }

        Page<PayOrder> orderPage = new Page<>(Integer.parseInt(page), Integer.parseInt(limit));
        LambdaQueryWrapper<PayOrder> wrapper = new LambdaQueryWrapper<>();

        return R.ok(payOrderService.page(orderPage, wrapper));
    }


    /**
     * 查询时间（type=1 当天，type=2 昨天，type=3 本周，type=4 本月）
     * lqp
     *
     * @return
     */
    @PostMapping("/queryTime")
    @ApiOperation(value = "根据传入的type获取开始和结束时间", notes = "根据传入的type获取开始和结束时间")
    @ApiImplicitParam(name = "params", value = "type", required = true)
    public R queryTime(@RequestBody Map<String, String> params) {
        String type = params.get("type").trim();
        return R.ok().put("times", payOrderService.queryTime(type));
    }

    /**
     * 商户流水查询
     *
     * @return
     */
    @PostMapping("/streamList")
    @ApiOperation(value = "根据ID批量删除订单", notes = "删除订单")
    @ApiImplicitParam(name = "params", value = "表单条件查询对象(page-当前页,limit-每页条数 必传)", required = true, dataType = "Map<String,String>", paramType = "body")
    public R streamList(@RequestBody Map<String, String> params) throws ParseException {
        String page = params.get("page");
        String limit = params.get("limit");
        //校验参数
        if (page == null || StringUtils.isBlank(page)) {
            return R.error(400, "page为空！");
        }
        if (limit == null || StringUtils.isBlank(limit)) {
            return R.error(400, "limit为空！");
        }
        Page<PayOrder> orderPage = new Page<>(Integer.parseInt(page), Integer.parseInt(limit));
        PayOrderVo payOrderVo = new PayOrderVo();
        //获取当前用户
        SysUser user = sysUserService.getById(UserContext.get());
        payOrderVo.setIsRoot(user.getIsroot());
        payOrderVo.setMerchantid(user.getMerchantid());
        payOrderVo.setBranchid(user.getBranchid());
        payOrderVo.setClubid(user.getClubid());
        IPage<PayOrder> order = payOrderService.streamPage(orderPage, payOrderVo, params);
        Map<String, Object> map = payOrderService.statisticsOrder(payOrderVo, params);
        return R.ok(order).put("num", map);
    }

    /**
     * 商户交易汇总
     *
     * @return
     */
    @PostMapping("/collectlist")
    @ApiOperation(value = "根据token获取角色ID查询对应的交易汇总", notes = "商户交易汇总")
    @ApiImplicitParam(name = "params", value = "起始日期，结束日期", required = true)
    public R collectlist(@RequestBody Map<String, String> params) {
        String page = params.get("page");
        String limit = params.get("limit");
        String branchname = params.get("branchname");
        System.out.println(branchname);
        //校验参数
        if (page == null || StringUtils.isBlank(page)) {
            return R.error(400, "page为空！");
        }
        if (limit == null || StringUtils.isBlank(limit)) {
            return R.error(400, "limit为空！");
        }
        Page<CollectVo> collectVoPage = new Page<>(Integer.parseInt(page), Integer.parseInt(limit));
        SysUser user = sysUserService.getById(UserContext.get());
        IPage<CollectVo> collect = payOrderService.collectlist(collectVoPage, params, user);
        return R.ok(collect).put("isroot", user.getIsroot());
    }

    /**
     * 首页今日、累计交易汇总
     * lqp
     *
     * @return
     */
    @PostMapping("/homePageCollect")
    @ApiOperation(value = "根据token获取角色ID查询对应的汇总", notes = "今日、累计交易汇总")
    public R homePageCollect() {
        SysUser user = sysUserService.getById(UserContext.get());
        Map<String, Object> map = payOrderService.homePageCollect(user);
        return R.ok().put("collect", map);
    }

    /**
     * 获取最近七天日期
     * lqp
     *
     * @return
     */
    @PostMapping("/latelyDate")
    @ApiOperation(value = "获取最近七天日期", notes = "获取最近七天日期")
    public R findLatelyDate() {
        List<String> list = payOrderService.findLatelyDate();
        return R.ok().put("lately", list);
    }

    /**
     * 获取最近七天汇总
     * lqp
     *
     * @return
     */
    @PostMapping("/latelyLine")
    @ApiOperation(value = "获取最近七天汇总", notes = "获取最近七天汇总")
    public R findLatelyLine() {
        List<String> list = payOrderService.findLatelyDate();
        SysUser user = sysUserService.getById(UserContext.get());
        Map<String, Object> map = payOrderService.findLatelyLine(user, list);
        return R.ok().put("lineMap", map);
    }


}
