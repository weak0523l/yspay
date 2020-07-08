<template>
  <div class="app-container">
    <!--列表开始-->
    <div class="filter-container">
      <el-input v-model="query.merchantName" style="width: 13%;" placeholder="请输入所属商户" />
      <el-input v-model="query.payorderno" style="width: 13%;" placeholder="请输入原交易订单号" />
	  <el-date-picker v-model="query.startTime" type="datetime" placeholder="选择开始日期时间">
	  </el-date-picker>
	  至
	  <el-date-picker v-model="query.endTime" type="datetime" placeholder="选择结束日期时间">
	  </el-date-picker>
	  <el-button type="success" @click="fetchTime(1);">
	    今天
	  </el-button>
	  <el-button type="success" @click="fetchTime(2);">
	    昨天
	  </el-button>
	  <el-button type="success" @click="fetchTime(3);">
	    本周
	  </el-button>
	  <el-button type="success" @click="fetchTime(4);">
	    本月
	  </el-button>
      <el-button type="primary" icon="el-icon-search" @click="fetchData">
        查询
      </el-button>
      <el-button type="danger" icon="el-icon-search" @click="handleReset">
        重置
      </el-button>
    </div>
    <el-table v-loading="listLoading" :data="list" element-loading-text="Loading" border fit highlight-current-row>
      <el-table-column label="序号" fixed="left" type="index" :index="indexMethod" align="center" width="65"></el-table-column>
	  <el-table-column label="所属商户" align="center">
	    <template slot-scope="scope">
	      {{ scope.row.merchantName }}
	    </template>
	  </el-table-column>
      <el-table-column label="退款订单号" align="center">
        <template slot-scope="scope">
          {{ scope.row.refundorderno }}
        </template>
      </el-table-column>
	  <el-table-column label="原订单金额" align="center">
	    <template slot-scope="scope">
	      {{ scope.row.money }}
	    </template>
	  </el-table-column>
      <el-table-column label="退款金额" align="center">
        <template slot-scope="scope">
          {{ scope.row.refundamount }}
        </template>
      </el-table-column>
	  <el-table-column label="退款类型" align="center">
	    <template slot-scope="scope">
	      {{ scope.row.paystateName }}
	    </template>
	  </el-table-column>
	  <el-table-column label="退款时间" align="center">
	    <template slot-scope="scope">
	      <span>{{ scope.row.refundtime }}</span>
	    </template>
	  </el-table-column>
      <el-table-column label="原交易订单号" align="center">
        <template slot-scope="scope">
          {{ scope.row.payorderno }}
        </template>
      </el-table-column>
	  <el-table-column label="操作人" align="center">
	    <template slot-scope="scope">
	      {{ scope.row.operator }}
	    </template>
	  </el-table-column>
    </el-table>

    <el-pagination :total="total" :current-page="page" :page-sizes="[15, 20, 50, 100, 200, 300, 400]" style="margin-top: 8px;"
      layout="total, prev, pager, next, sizes" @size-change="sizeChange" @current-change="pageChange" />

    <!-- 编辑-->

  </div>
</template>

<script>
  import {
    getpageList,getTimes
  } from "@/api/refund/index";

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
        query: {
          merchantName: "",
          payorderno: "",
		  startTime: "",
		  endTime: "",
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
          clubname: "",
          principal: "",
          phone: "",
          message: ""
        },
        rules: {
          DotName: [{
            required: true,
            message: "请填写网点名称",
            trigger: "blur"
          }],
          DotAddress: [{
            required: true,
            message: "请填写网点地址",
            trigger: "blur"
          }]
        }
      };
    },
    created() {
      this.fetchData();
    },

    methods: {
      fetchData() {
        this.listLoading = true;
        getpageList(this.query).then(response => {
          //debugger;
          this.list = response.data.records;
          this.total = parseInt(response.data.total);
          this.listLoading = false;
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
	  //获取时间
	  fetchTime(type){
	  this.query.startTime = "";
	  this.query.endTime = "";
	  getTimes({type:type}).then(res => {
	  	this.query.startTime = new Date(res.times.startTime);
	  	this.query.endTime = new Date(res.times.endTime);
	  });  
	  },
      //重置
      handleReset() {
        this.query.merchantName = "";
        this.query.payorderno = "";
		this.query.startTime = "";
		this.query.endTime = "";
        this.fetchData();
      },
      //详情
      editData(row) {
        this.editForm.show = true;
		this.editForm.id = "";
		this.editForm.clubname = "";
		this.editForm.principal = "";
		this.editForm.phone = "";
		this.editForm.message = "";
        this.$nextTick(() => {
          this.$refs.editForm.clearValidate();
          this.$refs.editForm.resetFields();
        })
        if (!row) {
          return;
        }
        info({
          id: row.id
        }).then(res => {
          this.editForm.id = res.payClub.id;
          this.editForm.clubname = res.payClub.clubname;
          this.editForm.principal = res.payClub.principal;
          this.editForm.phone = res.payClub.phone;
          this.editForm.message = res.payClub.message;
        });
      },
	  //提交表单
	  onSubmit(editForm) {
	      this.$refs[editForm].validate((valid) => {
	          if (valid) {
	              edit(this.editForm).then(res => {
	                  this.$notify({
	                      title: '提示',
	                      message: '提交成功',
	                      type: 'success'
	                  });
	                  this.fetchData();
	                  this.editForm.show = false;
	              })
	          } else {
	              return false;
	          }
	      })
	  },
      //取消
      cancel() {
        this.editForm.show = false;
      },
	  //删除用户
	  handleDelete(id) {
	      this.$confirm('此操作将永久删除, 是否继续?', '提示', {
	              confirmButtonText: '确定',
	              cancelButtonText: '取消',
	              type: 'warning'
	          }).then(() => {
	              del({ids: id}).then(res => {
	                  this.$message({
	                      type: 'success',
	                      message: '删除成功!'
	                  });
	                  this.fetchData();
	              })
	          }).catch(() => {
	              this.$message({
	              type: 'info',
	              message: '已取消删除'
	          });
	      });
	  }
    }
  };
</script>
