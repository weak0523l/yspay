<template>
  <div class="app-container">
    <!--列表开始-->
    <div class="filter-container">
			<el-row style="padding: 10px;">
				<el-col :lg="4">
					<el-input v-model="query.merchantName" style="width: 90%;" placeholder="请输入商户" />
				</el-col>
				<el-col :lg="4">
					<el-input v-model="query.operauser" style="width: 90%;" placeholder="请输入操作员" />
					</el-col>
				</el-col>
				<el-col :lg="6">
					<el-input v-model="query.swiffpassmerchno" style="width: 90%;" placeholder="请输入威富通商户号或统一收单账号" />
				</el-col>
				<el-col :lg="4">
					<el-select v-model="query.paytype" style="width: 90%;" placeholder="请选择支付方式">
					    <el-option
					      v-for="item in options"
					      :key="item.value"
					      :label="item.label"
					      :value="item.value">
					    </el-option>
					</el-select>
				</el-col>
				<el-col :lg="4">
					<el-select v-model="query.paychannel" style="width: 90%;" placeholder="请选择支付通道">
					    <el-option
					      v-for="item in paychannel"
					      :key="item.value"
					      :label="item.label"
					      :value="item.value">
					    </el-option>
					</el-select>
				</el-col>
			</el-row>
			<el-row style="padding: 10px;">
				<el-col :lg="6">
					<el-date-picker v-model="query.startTime" type="datetime" placeholder="选择开始日期时间" style="width: 90%;">
					</el-date-picker>  至
				</el-col>
				<el-col :lg="6">
					<el-date-picker v-model="query.endTime" type="datetime" placeholder="选择结束日期时间" style="width: 90%;">
					</el-date-picker>
				</el-col>
				<el-col :lg="6">
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
				</el-col>
				<el-col :lg="6">
					<el-button type="primary" icon="el-icon-search" @click="fetchData">
						查询
					</el-button>
					<el-button type="danger" icon="el-icon-search" @click="handleReset">
						重置
					</el-button>
				</el-col>
			</el-row>
			<el-row style="padding: 10px;">
				<p><span style="margin-right: 50px;">交易总金额:{{moneyNum}}</span>
				<span style="margin-right: 100px;">交易总数:{{ total }}</span>
				<span style="margin-right: 100px;">已支付数:{{havePaid}}</span>
				<span style="margin-right: 100px;">未支付数:{{notPaid}}</span></p>
			</el-row>
      <!-- <el-button v-waves class="filter-item" type="success" icon="el-icon-plus" @click="editData()">
        新增
      </el-button> -->
    </div>
    <el-table v-loading="listLoading" :data="list" element-loading-text="Loading" border fit highlight-current-row>
      <el-table-column label="序号" fixed="left" type="index" :index="indexMethod" align="center" width="65"></el-table-column>
      <el-table-column label="订单号" align="center">
        <template slot-scope="scope">
          {{ scope.row.orderno }}
        </template>
      </el-table-column>
      <el-table-column label="付款金额" align="center">
        <template slot-scope="scope">
          {{ scope.row.money }}
        </template>
      </el-table-column>
      <el-table-column label="所属商户" align="center">
        <template slot-scope="scope">
          {{ scope.row.merchantName }}
        </template>
      </el-table-column>
	  <el-table-column label="威富通商户号" align="center">
	    <template slot-scope="scope">
	      {{ scope.row.swiffpassmerchno }}
	    </template>
	  </el-table-column>
      <el-table-column label="付款码" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.authcode }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作员" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.operauser }}</span>
        </template>
      </el-table-column>
	  <!-- <el-table-column label="支付状态" align="center">
	    <template slot-scope="scope">
			<span v-if="scope.row.paystate === 1">已支付</span>
			<span v-if="scope.row.paystate === 0">未支付</span>
	    </template>
	  </el-table-column> -->
	  <el-table-column label="支付状态" align="center">
	    <template slot-scope="scope">
			<span>{{ scope.row.paystateName }}</span>
	    </template>
	  </el-table-column>
	  <el-table-column label="支付时间" align="center">
	    <template slot-scope="scope">
	      <span>{{ scope.row.paydate }}</span>
	    </template>
	  </el-table-column>
	  <el-table-column label="支付方式" align="center">
	    <template slot-scope="scope">
	      <span>{{ scope.row.paytype }}</span>
	    </template>
	  </el-table-column>
	  <el-table-column label="支付通道" align="center">
	    <template slot-scope="scope">
			<span v-if="scope.row.paychannel === 1">威富通</span>
			<span v-if="scope.row.paychannel === 2">统一收单</span>
	    </template>
	  </el-table-column>
	  <el-table-column label="退款订单号" align="center">
	    <template slot-scope="scope">
	  			<span>{{ scope.row.refundorderno }}</span>
	    </template>
	  </el-table-column>
	  <el-table-column label="创建时间" align="center">
	    <template slot-scope="scope">
			<span>{{ scope.row.createtime }}</span>
	    </template>
	  </el-table-column>

      <el-table-column label="操作" align="center" width="150px">
        <template slot-scope="scope">
          <el-button @click="editData(scope.row)" type="success" size="mini" icon="el-icon-plus" v-if="scope.row.paystate === 'HAVE'">
            退款
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination :total="total" :current-page="query.page" :page-sizes="[15, 20, 50, 100, 200, 300, 400]" style="margin-top: 8px;"
      layout="total, prev, pager, next, sizes" @size-change="sizeChange" @current-change="pageChange" />

    <!-- 编辑-->
    <el-dialog id="editForm" title="详情" :center="true" :visible.sync="editForm.show">
      <el-form :rules="editForm.rules" status-icon :model="editForm" ref="editForm" style="max-width: 880px;margin: 0 auto;"
        label-position="right" label-width="100px">
        <el-row :gutter="100">
          <el-col :lg="24">
            <el-form-item label="订单号:">
              <el-input v-model="editForm.orderno" placeholder="" :disabled="true"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
		<el-row :gutter="100">
		  <el-col :lg="24">
		    <el-form-item label="退款金额:" prop="money">
		      <el-input v-model="editForm.money" :placeholder="editForm.moneys" @keyup.native="editForm.money = oninput(editForm.money)">
			  </el-input>
		    </el-form-item>
		  </el-col>
		</el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="onSubmit('editForm')" icon="el-icon-check">提交</el-button>
		<el-button type="primary" v-waves @click="cancel()" icon="el-icon-success">取消</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
  import {
    getpageList,getTimes,edit
  } from "@/api/merchantStream/index";

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
		havePaid:0,
		notPaid:0,
		moneyNum:0,

		options:[{
			value: '微信',
			label: '微信'},
		{
			value: '银联',
			label: '银联'},
		{
			value: '支付宝',
			label: '支付宝'}],
		paychannel:[{
			value: 1,
			label: '威富通'},
		{
			value: 2,
			label: '统一收单'}],
        query: {
          startTime: "",
          endTime: "",
		  paytype:"",
		  merchantName:"",
		  swiffpassmerchno:"",
		  operauser:"",
		  paychannel:"",
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
          orderno: "",
		  money:"",
		  moneys:"",
		  rules: {
		    money: [{
		      required: true,
		      message: "请输入退款金额",
		      trigger: "blur"
		    }]
		  }
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
		  this.havePaid = response.num.havePaid;
		  this.notPaid = response.num.notPaid;
		  this.moneyNum = response.num.moneyNum;
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
        this.query.startTime = "";
        this.query.endTime = "";
		this.query.paytype = "";
		this.query.merchantName = "";
		this.query.swiffpassmerchno = "";
		this.query.paychannel = "";
		this.query.operauser = "";
		this.pageChange("1");
        this.fetchData();
      },
      //详情
      editData(row) {
        this.editForm.show = true;
        this.editForm.id = "";
        this.editForm.orderno = "";
        this.editForm.money = "";
		this.editForm.moneys = "";
        this.$nextTick(() => {
          this.$refs.editForm.clearValidate();
          this.$refs.editForm.resetFields();
        })
		this.editForm.id = row.id;
		this.editForm.orderno = row.orderno;
		this.editForm.moneys = row.money;
      },
      //提交表单
	  onSubmit(editForm) {
		  if(this.editForm.money > this.editForm.moneys){
			  this.$notify({
			      title: '提示',
			      message: '退款金额大于订单金额',
			      type: 'error'
			  });
			  return;
		  }
		  this.$refs[editForm].validate((valid) => {
			  if (valid) {
				  edit({
					  id:this.editForm.id,
					  orderno:this.editForm.orderno,
					  money:this.editForm.money
				  }).then(res => {
					  this.$notify({
						  title: '提示',
						  message: '退款操作成功!',
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
		oninput(num) {
			var str = num
			var len1 = str.substr(0, 1)
			var len2 = str.substr(1, 1)
			//如果第一位是0，第二位不是点，就用数字把点替换掉
			if (str.length > 1 && len1 == 0 && len2 != ".") {
				str = str.substr(1, 1)
			}
			//第一位不能是.
			if (len1 == ".") {
				str = ""
			}
			//限制只能输入一个小数点
			if (str.indexOf(".") != -1) {
				var str_ = str.substr(str.indexOf(".") + 1)
				if (str_.indexOf(".") != -1) {
					str = str.substr(0, str.indexOf(".") + str_.indexOf(".") + 1)
				}
			}
			//正则替换
			str = str.replace(/[^\d^\.]+/g, '') // 保留数字和小数点
			str = str.replace(/\.\d\d\d$/,'') // 小数点后只能输两位
			return str
		},
    }
  };
</script>
