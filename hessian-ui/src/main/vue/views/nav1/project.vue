<template>
    <section>
        <!--工具条-->
        <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
            <el-form :inline="true" :model="filters">
                <el-form-item>
                    <el-input v-model="filters.name" placeholder="项目名称"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" v-on:click="getProjectList">查询</el-button>
                </el-form-item>
            </el-form>
        </el-col>
        <template>
            <el-table :data="projectList" v-loading="loading"
                      style="width: 100%">
                <el-table-column type="expand" >
                    <template slot-scope="scope">
                        <el-table :data="scope.row.children">
                            <el-table-column type="index" width="60" label="No.">
                            </el-table-column>
                            <el-table-column prop="name" label="ip" sortable align="center">
                            </el-table-column>
                            <el-table-column prop="num" label="数量" sortable align="center">
                            </el-table-column>
                        </el-table>
                    </template>
                </el-table-column>
                <el-table-column type="index" width="60" label="No.">
                </el-table-column>
                <el-table-column prop="name" label="项目名" sortable align="center">
                </el-table-column>
                <el-table-column prop="date" label="创建时间" sortable align="center">
                </el-table-column>
                <el-table-column prop="num" label="节点数" sortable align="center">
                </el-table-column>
                <el-table-column prop="status" label="状态" sortable align="center" :formatter="formatSex">
                </el-table-column>
            </el-table>
        </template>
<!--        <template>-->
<!--            <el-table :data="project" highlight-current-row v-loading="loading" style="width: 100%;">-->
<!--                <el-table-column type="index" width="60">-->
<!--                </el-table-column>-->
<!--                <el-table-column prop="name" label="项目名" width="350" sortable>-->
<!--                </el-table-column>-->
<!--                <el-table-column prop="time" label="创建时间" width="150" sortable>-->
<!--                </el-table-column>-->
<!--                <el-table-column prop="num" label="节点数" width="100" sortable>-->
<!--                </el-table-column>-->
<!--                <el-table-column prop="status" label="状态" width="150" :formatter="formatSex" sortable>-->
<!--                </el-table-column>-->
<!--            </el-table>-->
<!--        </template>-->

    </section>
</template>
<script>
    export default {
        data() {
            return {
                filters: {
                    name: ''
                },
                loading: false,
                projectList: []

            }
        },
        created() {
            this.getProjectList()
        },
        methods: {
            formatSex: function (row, column) {
                return row.status == 1 ? '运行中' : row.sex == 0 ? '未运行' : '未知';
            },
            getProjectList: function () {
                this.loading = true;
                // 默认axios使用json传给后台
                // 如果要传application/x-www-form-urlencoded 到后台，使用qs.stringify(object)
                this.axios.post('/project/getProjectList').then(response => {
                    this.projectList = response.data;
                    this.loading = false;
                }).catch(error => {
                    this.$message({
                        type: 'error',
                        message: error.response.status
                    });
                    console.log(error)
                });
            }
        }
    };

</script>

<style scoped>

</style>