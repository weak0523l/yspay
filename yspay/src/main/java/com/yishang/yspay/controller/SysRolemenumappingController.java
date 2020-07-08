package com.yishang.yspay.controller;

import java.util.Arrays;
import java.util.Map;

import com.yishang.yspay.bean.SysRolemenumapping;
import com.yishang.yspay.service.SysRolemenumappingService;
import com.yishang.yspay.util.PageUtils;
import com.yishang.yspay.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * ${comments}
 *
 * @author zjf
 * @email 962700753@qq.com
 * @date 2020-06-22 09:11:10
 */
@RestController
@RequestMapping("pay/sysrolemenumapping")
public class SysRolemenumappingController {
    @Autowired
    private SysRolemenumappingService sysRolemenumappingService;

    /**
     * 列表
     */
    @PostMapping("/list")
    public R list(@RequestParam Map<String, Object> params){


        return R.ok();
    }


    /**
     * 信息
     */
    @PostMapping("/info/{id}")
    public R info(@PathVariable("id") String id){
		SysRolemenumapping sysRolemenumapping = sysRolemenumappingService.getById(id);

        return R.ok().put("sysRolemenumapping", sysRolemenumapping);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody SysRolemenumapping sysRolemenumapping){
		sysRolemenumappingService.save(sysRolemenumapping);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    public R update(@RequestBody SysRolemenumapping sysRolemenumapping){
		sysRolemenumappingService.updateById(sysRolemenumapping);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody String[] ids){
		sysRolemenumappingService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
