<template>
	<section>
		<!--工具条-->
		<el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
			<el-form :inline="true" :model="filters">
				<el-form-item>
					<el-input v-model="filters.name" placeholder="姓名"></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" v-on:click="getInterfaceList">查询</el-button>
				</el-form-item>
			</el-form>
		</el-col>

		<template>
			<el-table :data="dataList" v-loading="loading" :row-class-name="tableRowClassName"
					  style="width: 100%">
				<el-table-column type="expand" >
					<template slot-scope="scope">
						<el-table :data="scope.row.ipInfoList" :row-class-name="tableRowClassName">
							<el-table-column type="expand" >
								<template slot-scope="scope2">
									<el-table :data="scope2.row.methodInfoList" :row-class-name="tableRowClassName">
										<el-table-column type="index" width="60" label="No.">
										</el-table-column>
										<el-table-column prop="methodName" label="方法名" sortable align="center">
										</el-table-column>
										<el-table-column prop="num" label="请求次数" sortable align="center">
										</el-table-column>
									</el-table>
								</template>
							</el-table-column>
							<el-table-column type="index" width="60" label="No.">
							</el-table-column>
							<el-table-column prop="ip" label="ip" sortable align="center">
							</el-table-column>
							<el-table-column prop="applicationName" label="项目名" sortable align="center">
							</el-table-column>
							<el-table-column prop="date" label="连接时间" :formatter="dateFormate" sortable align="center">
							</el-table-column>
							<el-table-column prop="totalNum" label="请求总量" sortable align="center">
							</el-table-column>
							<el-table-column prop="methodNum" label="方法数量" sortable align="center">
							</el-table-column>
						</el-table>
					</template>
				</el-table-column>
				<el-table-column type="index" width="60" label="No.">
				</el-table-column>
				<el-table-column prop="interfaceName" label="接口名" sortable align="center">
				</el-table-column>
				<el-table-column prop="date" label="创建时间" sortable align="center">
				</el-table-column>
				<el-table-column prop="num" label="消费机器数" sortable align="center">
				</el-table-column>
				<el-table-column prop="status" label="状态" sortable align="center" :formatter="formatSex">
				</el-table-column>
			</el-table>
		</template>
	</section>
</template>
<style>
	.el-table .warning-row {
		background: oldlace;
	}

	.el-table .success-row {
		background: #f0f9eb;
	}
</style>
<script>
	export default {
		data() {
			return {
				filters: {
					name: ''
				},
				loading: false,
				dataList: [
				]
			}
		},
		methods: {
			formatSex: function (row, column) {
				return row.status == 1 ? '运行中' : row.sex == 0 ? '未运行' : '未知';
			},
			tableRowClassName:function(row,rowIndex) {
				if (rowIndex%2 === 0) {
					return 'success-row';
				}
				return '';
			},
			dateFormate:function(row,column){
				var date =new Date( row.date);
				var Y = date.getFullYear() + '-'
				var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-'
				var D = date.getDate() + ' '
				var h = date.getHours() + ':'
				var m = date.getMinutes() + ':'
				var s = date.getSeconds()
				return Y + M + D + h + m + s
			},
			getInterfaceList: function () {
				this.loading = true;
				// 默认axios使用json传给后台
				// 如果要传application/x-www-form-urlencoded 到后台，使用qs.stringify(object)
				this.axios.post('getInterfaceList').then(response => {
					this.dataList = response.data;
					this.loading = false;
				}).catch(error => {
					this.$message({
						type: 'error',
						message: error.response.status
					});
					console.log(error)
				});
			}
		},
		mounted() {
			this.getInterfaceList();
		}
	};

</script>

<style scoped>

</style>