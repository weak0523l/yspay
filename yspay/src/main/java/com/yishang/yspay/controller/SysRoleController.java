package com.yishang.yspay.controller;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yishang.yspay.bean.PayBranch;
import com.yishang.yspay.bean.SysMenu;
import com.yishang.yspay.bean.SysRole;
import com.yishang.yspay.bean.SysRolemenumapping;
import com.yishang.yspay.service.SysMenuService;
import com.yishang.yspay.service.SysRoleService;
import com.yishang.yspay.service.SysRolemenumappingService;
import com.yishang.yspay.util.MenuTree;
import com.yishang.yspay.util.R;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping("pay/role")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysRolemenumappingService sysRolemenumappingService;

    /**
     * 列表
     */
    @ApiOperation(value = "角色分页管理", notes = "获取角色列表")
    @ApiImplicitParam(name = "params", value = "表单条件查询对象(page-当前页,limit-每页条数 必传)", required = true, dataType = "Map<String,String>", paramType = "body")
    @PostMapping("/list")
    public R list(@RequestBody Map<String, String> params){
        String pageNow = params.get("page");
        String pageNum = params.get("limit");
        //校验参数
        if (pageNow == null || StringUtils.isBlank(pageNow)) {
            return R.error(400, "page为空！");
        }
        if (pageNum == null || StringUtils.isBlank(pageNum)) {
            return R.error(400, "limit为空！");
        }
        Page<SysRole> page = new Page<>(Integer.parseInt(pageNow), Integer.parseInt(pageNum));
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();


        List<SysMenu> list = sysMenuService.list();
        MenuTree menuTree = new MenuTree(list);
        list = menuTree.builTree();
        /*转为json看看效果*/
        String jsonOutput = JSON.toJSONString(list);

        return R.ok(sysRoleService.page(page, wrapper)).put("menuList", list);
    }


    /**
     * 信息
     */
    @ApiOperation(value = "根据ID获取角色", notes = "获取单个角色信息")
    @ApiImplicitParam(name = "params", value = "角色ID", required = true, dataType = "Map<String, String>")
    @PostMapping("/info")
    public R info(@RequestBody Map<String, String> params) {
        String id = params.get("id");
        //参数校验
        if (id == null || StringUtils.isBlank(id)) {
            return R.error(400, "id为空！");
        }
        SysRole sysRole = sysRoleService.getById(id);
        if (sysRole == null) {
            return R.error(400, "未查询到该结果！");
        }
        return R.ok().put("sysRole", sysRole);
    }

    /**
     * 编辑
     */
    @ApiOperation(value = "编辑角色", notes = "编辑角色信息")
    @ApiImplicitParam(name = "sysRole", value = "角色对象", required = true, dataType = "SysRole")
    @PostMapping("/edit")
    public R edit(@RequestBody SysRole sysRole) {
        //参数校验
        if (sysRole.getRolename() == null || StringUtils.isBlank(sysRole.getRolename())) {
            return R.error(400, "角色名称为空！");
        }

        if (sysRole.getId() == null || StringUtils.isBlank(sysRole.getId())) {
            sysRole.setId(UUID.randomUUID() + "");
            sysRole.setRolename(sysRole.getRolename());
            sysRole.setRemarks(sysRole.getRemarks());
            sysRole.setCreatetime(new Date());
            sysRoleService.save(sysRole);
            return R.ok("添加成功！");
        }
        sysRoleService.updateById(sysRole);
        return R.ok("编辑成功！");
    }



    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation(value = "根据ID批量删除角色", notes = "删除角色")
    @ApiImplicitParam(name = "params", value = "角色ids", required = true)
    public R delete(@RequestBody Map<String,String> params){

        String[] ids = params.get("ids").split(",");
        //参数校验
        if (ids.length <= 0) {
            return R.error(400, "ids长度<=0");
        }
        for (String id : ids) {
            if (id == null || StringUtils.isBlank(id)) {
                return R.error(400, "id为空");
            }
            QueryWrapper<SysRolemenumapping> wrapper = new QueryWrapper();
            wrapper.eq("RoleId",id);
            sysRolemenumappingService.remove(wrapper);
        }

        sysRoleService.removeByIds(Arrays.asList(ids));
        return R.ok("删除成功！");
    }

    /**
     * 获取角色权限菜单
     */
    @PostMapping("/getTree")
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "params", value = "", required = true)
    public R getTree(@RequestBody Map<String,String> params){
        String id = params.get("id");
        //参数校验
        if (id == null || StringUtils.isBlank(id)) {
            return R.error(400, "id为空！");
        }
        QueryWrapper<SysRolemenumapping> wrapper2 = new QueryWrapper();
        wrapper2.eq("RoleId",id);
        List<SysRolemenumapping> sysRolemenumappings = sysRolemenumappingService.list(wrapper2);

        List<String> ids = new ArrayList<>() ;
        for(SysRolemenumapping sysRolemenumapping: sysRolemenumappings){
            ids.add(sysRolemenumapping.getMenuid());
        }
        if(ids.size()<=0){
            ids=null;
        }
        return R.ok().put("menuIds",ids);
    }


    /**
     * 配置角色权限菜单
     */
    @PostMapping("/setTree")
    @ApiOperation(value = "", notes = "")
    @ApiImplicitParam(name = "params", value = "", required = true)
    public R setTree(@RequestBody Map<String,String> params){
        String str = params.get("menuStr").replace("\"","");
        String roleId = params.get("id");
        String[] ids = str.substring(1,str.length()-1).split(",");
        if (ids.length <= 0) {
            return R.error(400, "ids长度<=0");
        }
        if (roleId == null || StringUtils.isBlank(roleId)) {
            return R.error(400, "角色为空");
        }
        QueryWrapper<SysRolemenumapping> wrapper2 = new QueryWrapper();
        wrapper2.eq("roleId",roleId);
        sysRolemenumappingService.remove(wrapper2);
            for(String id: ids){
                if(id != null && StringUtils.isNotBlank(id)){
                    SysRolemenumapping sysRolemenumapping = new SysRolemenumapping();
                    sysRolemenumapping.setId(UUID.randomUUID() + "");
                    sysRolemenumapping.setMenuid(id);
                    sysRolemenumapping.setRoleid(roleId);
                    sysRolemenumappingService.save(sysRolemenumapping);
                }

        }

        return R.ok("配置成功！");
    }
}
