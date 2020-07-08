package com.yishang.yspay.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yishang.yspay.bean.SysMenu;
import com.yishang.yspay.service.SysMenuService;
import com.yishang.yspay.util.MenuTree;
import com.yishang.yspay.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * ${comments}
 *
 * @author zjf
 * @email 962700753@qq.com
 * @date 2020-06-22 09:11:10
 */
@RestController
@Api(tags = "菜单表接口")
@RequestMapping("pay/menu")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 列表
     */
    @ApiOperation(value = "菜单分页管理", notes = "获取菜单列表")
    @ApiImplicitParam(name = "params", value = "表单条件查询对象(page-当前页,limit-每页条数 必传)", required = true, dataType = "Map<String,String>", paramType = "body")
    @PostMapping("/list")
    public R list(@RequestBody Map<String, String> params) {
        String pageNow = params.get("page");
        String pageNum = params.get("limit");
        //校验参数
        if (pageNow == null || StringUtils.isBlank(pageNow)) {
            return R.error(400, "page为空！");
        }
        if (pageNum == null || StringUtils.isBlank(pageNum)) {
            return R.error(400, "limit为空！");
        }
        Page<SysMenu> page = new Page<>(Integer.parseInt(pageNow), Integer.parseInt(pageNum));
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        return R.ok(sysMenuService.page(page, wrapper));
    }


    /**
     * 信息
     */
    @ApiOperation(value = "根据ID获取菜单", notes = "获取单个菜单信息")
    @ApiImplicitParam(name = "params", value = "菜单ID", required = true, dataType = "String")
    @PostMapping("/info")
    public R info(@RequestBody Map<String, String> params) {
        String id = params.get("menuId");
        //参数校验
        if (id == null || StringUtils.isBlank(id)) {
            return R.error(400, "ID为空！");
        }
        SysMenu sysMenu = sysMenuService.getById(id);
        if (sysMenu == null) {
            return R.error(400, "未查询到该结果！");
        }
        return R.ok().put("sysMenu", sysMenu);
    }

    /**
     * 编辑
     *
     * @param sysMenu
     * @return
     */
    @ApiOperation(value = "编辑商户", notes = "编辑商户信息")
    @ApiImplicitParam(name = "sysMenu", value = "商户对象", required = true, dataType = "SysMenu", paramType = "body")
    @PostMapping("/edit")
    public R edit(@RequestBody SysMenu sysMenu) {
        //参数校验
//        if (sysMenu.getName() == null || StringUtils.isBlank(sysMenu.getName())) {
//            return R.error(400,"菜单名称为空！");
//        }
//        if (sysMenu.getTitle() == null || StringUtils.isBlank(sysMenu.getTitle())) {
//            return R.error(400,"组件名称为空！");
//        }
//        if (sysMenu.getIcon() == null || StringUtils.isBlank(sysMenu.getIcon())) {
//            return R.error(400,"菜单图标为空！");
//        }
        if (sysMenu.getId() == null || StringUtils.isBlank(sysMenu.getId())) {
            sysMenu.setId(UUID.randomUUID() + "");
            sysMenu.setCreatetime(new Date());
            sysMenuService.save(sysMenu);
            return R.ok("添加成功！");
        }
        sysMenuService.updateById(sysMenu);
        return R.ok("修改成功！");
    }

    /**
     * 保存
     */
    @ApiOperation(value = "编辑菜单", notes = "编辑菜单信息")
    @ApiImplicitParam(name = "sysMenu", value = "菜单对象", required = true, dataType = "SysMenu", paramType = "body")
    @PostMapping("/save")
    public R save(@RequestBody SysMenu sysMenu) {
        sysMenuService.save(sysMenu);

        return R.ok("添加成功！");
    }

    /**
     * 修改
     */
    @ApiOperation(value = "修改菜单", notes = "修改菜单信息")
    @ApiImplicitParam(name = "sysMenu", value = "菜单对象", required = true, dataType = "SysMenu", paramType = "body")
    @PostMapping("/update")
    public R update(@RequestBody SysMenu sysMenu) {
        sysMenuService.updateById(sysMenu);

        return R.ok("修改成功！");
    }

    /**
     * 删除
     */
    @ApiOperation(value = "根据id批量删除银行", notes = "删除银行")
    @ApiImplicitParam(name = "params", value = "银行ids ", required = true)
    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, String> params) {
        String[] ids = params.get("ids").split(",");
        //参数校验
        if (ids.length <= 0) {
            return R.error(400, "ids长度<=0");
        }
        for (String id : ids) {
            if (id == null || StringUtils.isBlank(id)) {
                return R.error(400, "id为空");
            }
        }
        sysMenuService.removeByIds(Arrays.asList(ids));
        return R.ok("删除成功！");
    }

    @ApiOperation(value = "获取菜单", notes = "获取菜单")
    @PostMapping("/findMenu")
    public R findMenu() {
        List<SysMenu> list = sysMenuService.list();
        MenuTree menuTree = new MenuTree(list);
        list = menuTree.builTree();
        /*转为json看看效果*/
        String jsonOutput = JSON.toJSONString(list);
        System.out.println(jsonOutput);
        return R.ok().put("tree", list);
    }

    /**
     * 获取所有树形菜单
     *
     * @return
     */
    @PostMapping("/menuTree")
    @ResponseBody
    @ApiOperation(value = "获取所有菜单", notes = "获取所有菜单")
    public R menuTree() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> list = sysMenuService.list(wrapper);
        MenuTree menuTree = new MenuTree(list);
        list = menuTree.builTree();
        return R.ok().put("data", list);
    }

    /**
     * 获取所有菜单集合
     *
     * @return
     */
    @PostMapping("/selectMenuList")
    @ResponseBody
    @ApiOperation(value = "获取所有菜单集合", notes = "获取所有菜单集合")
    public R selectMenuList() {
        List<SysMenu> list = sysMenuService.list();
        return R.ok().put("data", list);
    }

    /**
     * 根据菜单ID获取详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable String id)
    {
        return R.ok().put("menu",sysMenuService.getById(id));
    }

    /**
     * 根据菜单ID删除菜单
     */
    @DeleteMapping("/{id}")
    public R remove(@PathVariable("id") String id){
        sysMenuService.removeById(id);
        return R.ok();
    }
}
