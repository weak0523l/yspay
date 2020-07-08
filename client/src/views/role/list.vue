<template>
  <div class="app-container">
    <!--列表开始-->
    <div class="filter-container">
      <!-- <el-input v-model="query.rolename" style="width: 13%;" placeholder="请输入角色名称" /> -->
      <el-button type="primary" icon="el-icon-search" @click="fetchData">
        查询
      </el-button>
      <el-button type="danger" icon="el-icon-search" @click="handleReset">
        重置
      </el-button>
      <el-button
        v-waves
        class="filter-item"
        type="success"
        icon="el-icon-plus"
        @click="editData()"
      >
        新增
      </el-button>
    </div>
    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="Loading"
      border
      fit
      highlight-current-row
    >
      <el-table-column
        label="序号"
        fixed="left"
        type="index"
        :index="indexMethod"
        align="center"
        width="65"
      ></el-table-column>
      <el-table-column label="角色名称" align="center">
        <template slot-scope="scope">
          {{ scope.row.rolename }}
        </template>
      </el-table-column>
      <el-table-column label="角色说明" align="center">
        <template slot-scope="scope">
          {{ scope.row.remarks }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center">
        <template slot-scope="scope">
          {{ scope.row.createtime }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="300px">
        <template slot-scope="scope">
          <!-- <el-button @click="editData(scope.row)" type="success" size="mini" icon="el-icon-plus">
            详情
          </el-button> -->
          <el-button
            class="filter-item"
            @click="editData(scope.row)"
            type="primary"
            size="mini"
            icon="el-icon-edit"
          >
            编辑
          </el-button>
          <el-button
            class="filter-item"
            @click="handleDelete(scope.row.id)"
            type="danger"
            size="mini"
            icon="el-icon-remove"
          >
            删除
          </el-button>
          <el-button
            class="filter-item"
            @click="updateTree(scope.row)"
            type="warning"
            size="mini"
            icon="el-icon-remove"
          >
            配置
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      :total="total"
      :current-page="page"
      :page-sizes="[15, 20, 50, 100, 200, 300, 400]"
      style="margin-top: 8px;"
      layout="total, prev, pager, next, sizes"
      @size-change="sizeChange"
      @current-change="pageChange"
    />

    <el-dialog
      title="配置角色菜单"
      :center="true"
      :visible.sync="menuTree.show"
      id="menuTree"
    >
      <el-form
        :rules="rules"
        status-icon
        :model="menuTree"
        ref="menuTree"
        style="max-width: 880px;margin: 0 auto;"
        label-position="right"
        label-width="80px"
      >
        <el-form-item label="选择菜单" >
          <el-row :gutter="100">
            <el-col :lg="24">
              <el-tree
                ref="menu"
                :data="menuList"
                show-checkbox
                node-key="id"
                default-expand-all=""
                :props="defaultProps"
              >
              </el-tree>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button
          type="primary"
          @click="submitForm('menuTree')"
          icon="el-icon-check"
          >提交</el-button
        >
        <el-button
          type="primary"
          v-waves
          @click="cancel()"
          icon="el-icon-success"
          >取消</el-button
        >
      </div>
    </el-dialog>

    <!-- 编辑-->
    <el-dialog
      id="editForm"
      title="详情"
      :center="true"
      :visible.sync="editForm.show"
    >
      <el-form
        :rules="rules"
        status-icon
        :model="editForm"
        ref="editForm"
        style="max-width: 880px;margin: 0 auto;"
        label-position="right"
        label-width="80px"
      >
        <el-row :gutter="100">
          <el-col :lg="24">
            <el-form-item label="角色名称" prop="rolename">
              <el-input
                v-model="editForm.rolename"
                placeholder="请输入角色名称"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="100">
          <el-col :lg="24">
            <el-form-item label="角色说明" prop="remarks">
              <el-input
                v-model="editForm.remarks"
                placeholder="请输入角色说明"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button
          type="primary"
          @click="onSubmit('editForm')"
          icon="el-icon-check"
          >提交</el-button
        >
        <el-button
          type="primary"
          v-waves
          @click="cancel()"
          icon="el-icon-success"
          >取消</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getpageList,
  edit,
  del,
  info,
  setTree,
  getTree
} from "@/api/role/role";
import { Alert } from "element-ui";

export default {
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: "success",
        draft: "gray",
        deleted: "danger"
      };
      return statusMap[status];
    }
  },

  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      dialogFormVisible: false,
      titleText: "新增网点",
      menuList: "",
      menuIds: [],
      query: {
        rolename: "",
        page: 1,
        limit: 15
      },
      formData: {
        DotName: "",
        DotAddress: ""
      },
      editForm: {
        show: false,
        id: "",
        rolename: "",
        remarks: "",
      },
      menuTree: {
        show: false,
        id: "",
        menuIds: "",
        menuStr:[]
      },
      defaultProps: {
        children: "children",
        label: "title"
      },
      rules: {
        rolename: [
          {
            required: true,
            message: "请填写角色名称",
            trigger: "blur"
          }
        ]
      }
    };
  },
  created() {
    this.fetchData();
  },
  methods: {
    // updateTree(data) {
    //   console.log(data);
    // },
    fetchData() {
      this.listLoading = true;
      getpageList(this.query).then(response => {
        //debugger;
        this.list = response.data.records;
        this.total = parseInt(response.data.total);
        this.listLoading = false;
        this.menuList = response.menuList;
      });
    },
    // 改变页码
    pageChange(e) {
      //debugger;
      this.query.page = e;
      this.fetchData();
    },
    // 改变每页显示数
    sizeChange(e) {
      //debugger;
      this.query.page = 1;
      this.query.limit = e;
      this.fetchData();
    },
    //重置
    handleReset() {
      this.query.rolename = "";
      this.fetchData();
    },
    //详情
    editData(row) {
      this.editForm.show = true;
      this.editForm.id = "";
      this.editForm.rolename = "";
      this.editForm.remarks = "";
      this.$nextTick(() => {
        this.$refs.editForm.clearValidate();
        this.$refs.editForm.resetFields();
      });
      if (!row) {
        return;
      }
      info({
        id: row.id
      }).then(res => {
        this.editForm.id = res.sysRole.id;
        this.editForm.rolename = res.sysRole.rolename;
        this.editForm.remarks = res.sysRole.remarks;
      });
    },
    //配置权限
    updateTree(row) {
      this.menuTree.id = "";
      this.menuTree.menuStr = [];
      this.menuIds = "";
      this.$nextTick(() => {
        this.$refs.menuTree.clearValidate();
        this.$refs.menuTree.resetFields();
      });
      this.menuTree.show = true;
      // this.$refs.menu.setCheckedKeys(undefined);
      if(row){
        this.menuTree.id = row.id;
        getTree({
        id: row.id
        }).then(res => {
          
          this.$nextTick(() => {
    
            if(res.menuIds===null){
                this.$refs.menu.setCheckedKeys([]);
            }
            this.$refs.menu.setCheckedKeys(res.menuIds);
          }); 
        });
      }
      
    },
  
    //提交表单
    onSubmit(editForm) {
      this.$refs[editForm].validate(valid => {
        if (valid) {
          edit(this.editForm).then(res => {
            this.$notify({
              title: "提示",
              message: "提交成功",
              type: "success"
            });
            this.fetchData();
            this.editForm.show = false;
          });
        } else {
          return false;
        }
      });
    },
    //提交菜单
    submitForm(menuTree) {
      var str = [];
      str = JSON.stringify(this.$refs.menu.getCheckedKeys());
      this.menuTree.menuStr = str;
       this.$refs[menuTree].validate(valid => {
         if (valid) {
          setTree(this.menuTree).then(res => {
            this.$notify({
              title: "提示",
              message: "提交成功",
              type: "success"
            });
            this.fetchData();
            this.menuTree.show = false;
          });
        } else {
          return false;
        }
      });
    },


    // submitForm(menuTree) {
    //   var str = [];
    //   str = JSON.stringify(this.$refs.menu.getCheckedKeys());
    //   this.$refs[menuTree].validate(valid => {
    //     alert(1)
    //     if (valid) {
    //       setTree({ str: str, roleId: id }).then(res => {
    //         this.$notify({
    //           title: "提示",
    //           message: "提交成功",
    //           type: "success"
    //         });
    //         this.fetchData();
    //         this.menuIds = "";
    //         this.dialogFormVisible = false;
    //       });
    //     } else {
    //       return false;
    //     }
    //   });
    // },
    //取消
    cancel() {
      this.menuIds = "";
      this.editForm.show = false;
      this.menuTree.show = false;
    },
    //删除用户
    handleDelete(id) {
      this.$confirm("此操作将永久删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          del({ ids: id }).then(res => {
            this.$message({
              type: "success",
              message: "删除成功!"
            });
            this.fetchData();
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除"
          });
        });
    }
  }
};
</script>
