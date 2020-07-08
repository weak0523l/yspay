<template>
  <div class="app-container">
    <!--列表开始-->
    <div class="filter-container">
      <el-input v-model="query.merchantname" style="width: 13%;" :placeholder="search" />
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
          <el-button type="primary" icon="el-icon-search" @click="merchantData" v-if="isroot===4 ? false : true">
        商户
      </el-button>
      <el-button type="primary" icon="el-icon-search" @click="branchData"  v-if="isroot===1 || isroot===2 || isroot===3" >
        网点
      </el-button>
            <el-button type="primary" icon="el-icon-search" @click="clubData"  v-if="isroot===1 || isroot===2" >
        行社
      </el-button>
      <br>
    <el-table v-loading="listLoading" :data="list" element-loading-text="Loading" border fit highlight-current-row>
      <el-table-column label="序号" fixed="left" type="index" :index="indexMethod" align="center" width="65"></el-table-column>
      <!-- <el-table-column label="日期" align="center">
        <template slot-scope="scope">
          {{ scope.row.orderDate }}
        </template> 
      </el-table-column> -->
    <el-table-column :label="name" align="center" v>
        <template slot-scope="scope">
          {{ scope.row.merchname }}
        </template>
      </el-table-column>
    <el-table-column label="交易笔数" align="center">
	    <template slot-scope="scope">
	      {{ scope.row.orderCount }}
	    </template>
	  </el-table-column>
	  <el-table-column label="交易金额" align="center">
	    <template slot-scope="scope">
	      {{ scope.row.money }}
	    </template>
	  </el-table-column>

    </el-table>

    <el-pagination :total="total" :current-page="page" :page-sizes="[15, 20, 50, 100, 200, 300, 400]" style="margin-top: 8px;"
      layout="total, prev, pager, next, sizes" @size-change="sizeChange" @current-change="pageChange" />


  </div>
</template>

<script>
  import {
    getpageList,
	getTimes,
  } from "@/api/collect/collect.js";

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
        isroot: "",
        name:"商户名称",
        search:"请输入商户名称",
        query: {
          branchname:"",
          merchantname: "",
					clubname: "",
          type:"1",
		  startTime:"",
		  endTime:"",
          page: 1,
          limit: 15
        },
        formData: {
          DotName: "",
          DotAddress: ""
        },
        
      };
    },
    created() {
      this.fetchData();
    },

    methods: {
      fetchData() {
        this.listLoading = true;
        this.query.merchantname = this.query.merchantname
        this.query.branchname = this.query.merchantname
        this.query.clubname = this.query.merchantname
        getpageList(this.query).then(response => {
          //debugger;      
          this.list = response.data.records;
          this.total = parseInt(response.data.total);
          this.listLoading = false;
          this.isroot = response.isroot;
        });
		
      },
      branchData(){
        this.listLoading = true;
        this.query.type = 2;
        this.name = "网点名称";
        this.search="请输入网点名称";
        this.query.branchname = this.query.merchantname
        getpageList(this.query).then(response => {
          //debugger;
          this.list = response.data.records;
          this.total = parseInt(response.data.total);
          this.listLoading = false;
        });
      },
      clubData(){
        this.listLoading = true;
        this.query.type = 3;
        this.name = "行社名称";
        this.search="请输入行社名称";
		    this.query.clubname = this.query.merchantname
        getpageList(this.query).then(response => {
          //debugger;
          this.list = response.data.records;
          this.total = parseInt(response.data.total);
          this.listLoading = false;
        });
      },
      merchantData(){
        this.listLoading = true;
        this.query.type = 1;
        this.name = "商户名称";
        this.search="请输入商户名称";
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
      //重置
      handleReset() {
		this.query.merchantname= "";
		this.query.swiffpassmerchno= "";
		this.query.merid= "";
		this.query.clubname= "";
    this.query.branchname= "";
    this.query.startTime = "";
	  this.query.endTime = "";
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
	  
    }
  };
</script>
