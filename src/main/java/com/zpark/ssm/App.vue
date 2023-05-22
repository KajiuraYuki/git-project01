<template>
	<el-container id="one">
		<router-view></router-view>
		<div id="two">
			<!-- 面包屑导航区 -->
			<!-- 面包屑导航区 -->
			<el-breadcrumb separator-class="el-icon-arrow-right">
			  <el-breadcrumb-item :to="{ path: '/admin/welcome' }" @click.native="saveNavState('/admin/welcome')">首页</el-breadcrumb-item>
			  <el-breadcrumb-item>教师管理</el-breadcrumb-item>
			  <el-breadcrumb-item>教师信息管理</el-breadcrumb-item>
			</el-breadcrumb>
			<br />
			<el-card>
				<el-row :gutter="20">			
					<el-col :span="6">
						<el-input placeholder="请输入查找内容" clearable v-model="queryInfo.query" clearable @clear="getTeaList" >
							<el-button slot="append" icon="el-icon-search" @click="getTeaList"></el-button>
						</el-input>					
					</el-col>					
				</el-row>
				<el-row :gutter="20" id="row1">
					<el-col :span="4">
					  <!-- clearable  可以删除 -->
					  <el-select v-model="queryInfo.select_sex" placeholder="请选择性别" >
					    <el-option
					      v-for="item1 in sex"
					      :key="item1.type"
					      :label="item1.type"
					      :value="item1.type"
					    >
					    </el-option>
					  </el-select>
					</el-col>
					<el-col :span="4">
					  <!-- clearable  可以删除 -->
					  <el-select v-model="queryInfo.select_college" placeholder="请选择学院" >
					    <el-option
					      v-for="item1 in college"
					      :key="item1"
					      :label="item1"
					      :value="item1"
					    >
					    </el-option>
					  </el-select>
					</el-col>
					<el-col :span="4">
					  <!-- clearable  可以删除 -->
					  <el-select v-model="queryInfo.select_major" placeholder="请选择专业" >
					    <el-option
					      v-for="item1 in major"
					      :key="item1"
					      :label="item1"
					      :value="item1"
					    >
					    </el-option>
					  </el-select>
					</el-col>
					<!-- 搜索按钮 -->
					<el-col :span="2">
					  <el-button type="primary" @click="getTeaList">搜索</el-button>
					</el-col>
					<!-- 清空搜索按钮 -->
					<el-col :span="3">
					  <el-button type="info" @click="clearValues">清除搜索</el-button>
					</el-col>
					<!-- 添加用户按钮 -->
					<el-col :span="3">
					  <el-button type="primary" @click="addTeaButton">添加教师</el-button>
					</el-col>
				</el-row>
				<!-- 用户信息列表区域 -->
				<el-table :data="TeaList" ref="table" boder stripe :height="tableHeight">
					<!-- stripe  :  斑马条纹
						 border  :  边框
					-->
					<el-table-column align="center" type="index" label="#"></el-table-column>
					<el-table-column align="center" prop="zhanghu" label="账户"></el-table-column>
					<el-table-column align="center" prop="name" label="姓名"></el-table-column>
					<el-table-column align="center" prop="sex" label="性别"></el-table-column>					
					<el-table-column align="center" prop="college" label="学院"></el-table-column>
					<el-table-column align="center" prop="major" label="专业"></el-table-column>
					<el-table-column align="center" prop="ctime" label="创建时间" width="170px"></el-table-column>
					<el-table-column align="center" label="操作" width="200px">
						<template slot-scope="scope">
							<el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row.zhanghu)">编辑</el-button>
							<el-button type="danger" icon="el-icon-delete" size="mini" @click="removeTeaByZhanghu(scope.row.zhanghu)">删除</el-button>
						</template>
					</el-table-column>
				</el-table>
				<!-- 分页区域 -->
				<el-pagination
				  @size-change="handleSizeChange"
				  @current-change="handleCurrentChange"
				  :current-page="queryInfo.pagenum"
				  :page-sizes="[2, 5, 10,15]"
				  :page-size="queryInfo.pagesize"
				  layout="total, sizes, prev, pager, next, jumper"
				  :total="totle"
				></el-pagination>
			</el-card>
			<!-- 编辑角色对话框 -->
			<el-dialog center title="编辑教师信息" :visible.sync="editTeaDialogVisible" width="40%" >
			  <el-form
			    :model="editTeaForm"
			    ref="editTeaForm"
			    :rules="addTeaFormRules"
			    label-width="80px">
			    <el-form-item label="账户" prop="zhanghu">
			      <el-input disabled v-model="editTeaForm.zhanghu"></el-input>
			    </el-form-item>
				<el-form-item disabled label="重置密码" prop="password">
				  <el-input v-model="editTeaForm.password"  placeholder="默认为使用原密码"></el-input>
				</el-form-item>
			    <el-form-item disabled label="姓名" prop="name">
			      <el-input v-model="editTeaForm.name"></el-input>
			    </el-form-item>
				<el-form-item disabled label="性别" prop="sex">
				  <el-radio v-model="editTeaForm.sex" label="男">男</el-radio>
				  <el-radio v-model="editTeaForm.sex" label="女">女</el-radio>
				</el-form-item>
				<el-form-item label="创建时间" prop="ctime">
				  <el-input disabled v-model="editTeaForm.ctime"></el-input>
				</el-form-item>
				<el-form-item disabled label="学院" prop="college">
				  <el-select v-model="editTeaForm.college" @change="collegechange" placeholder="请选择所属学院">
					  <el-option
						v-for="item in CollegeData"
						:key="item.collegename"
						:label="item.collegename"
						:value="item.collegename"></el-option>
				  </el-select>
				</el-form-item>
				<el-form-item disabled label="专业" prop="major">
				  <el-select v-model="editTeaForm.major" placeholder="请选择所属专业">
					  <el-option
						v-for="item in MajorData"
						:key="item.majorid"
						:label="item.majorname"
						:value="item.majorname"></el-option>
				  </el-select>
				</el-form-item>
				<el-form-item disabled label="邮箱" prop="email">
				  <el-input v-model="editTeaForm.email"></el-input>
				</el-form-item>				
			  </el-form>
			  <span slot="footer" class="dialog-footer">
			    <el-button @click="editTeaDialogVisible = false">取 消</el-button>
			    <el-button type="primary" @click="editTea">确 定</el-button>
			  </span>
			</el-dialog>
			
			<!-- 添加学生信息对话框 -->
			<el-dialog center title="添加教师信息" :visible.sync="addDialogVisible" width="50%" @close="addDialogClosed">
				<!-- 主体内容 -->
				<el-form
					:model="addTeaForm"
					ref="addTeaForm"
					:rules="addTeaFormRules"
					label-width="100px"
				  >
					<el-form-item label="初始密码" prop="password">
						<el-input v-model="addTeaForm.password"></el-input>
					</el-form-item>
					<el-form-item label="姓名" prop="name">
						<el-input v-model="addTeaForm.name"></el-input>
					</el-form-item>
					<el-form-item label="性别" prop="sex">
						<el-radio v-model="addTeaForm.sex" label="男">男</el-radio>
						<el-radio v-model="addTeaForm.sex" label="女">女</el-radio>
					</el-form-item>
					<el-form-item label="学院" prop="college">
						<el-select v-model="addTeaForm.college" @change="collegechange" placeholder="请选择所属学院">
							<el-option
								v-for="item in CollegeData"
								:key="item.collegename"
								:label="item.collegename"
								:value="item.collegename">
								</el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="专业" prop="major">
						<el-select v-model="addTeaForm.major" placeholder="请选择所属专业">
						  <el-option
							v-for="item in MajorData"
							:key="item.majorname"
							:label="item.majorname"
							:value="item.majorname">
							</el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="邮箱" prop="email">
						<el-input v-model="addTeaForm.email"></el-input>
					</el-form-item>
					</el-form>
					<span slot="footer" class="dialog-footer">
					  <el-button @click="addDialogVisible = false">取 消</el-button>
					  <el-button type="primary" @click="addTea">确 定</el-button>
					</span>
			</el-dialog>
		</div>
	</el-container>
</template>

<script>
	export default {
		name: 'Tea_Manage',
		data () {
			// 自定义邮箱规则
			var checkEmail = (rule, value, callback) => {
			  const regEmail = /^\w+@\w+(\.\w+)+$/
			  if (regEmail.test(value)) {
			    // 合法邮箱
			    return callback()
			  }
			  callback(new Error('请输入合法邮箱'))
			}
			return {
				tableHeight: 100,
				queryInfo: {
					query: '',
					pagenum: 1,
					pagesize: 5,
					select_sex: '',
					select_college: '',
					select_major: '',				
				},
				TeaList: [],
				sex: [{
					"id": 1,
					"type": "男"
				},								
				{
					"id": 2,
					"type": "女"
				}],
				college: [],
				major: [],
				banji: [],
				totle: 0,
				editTeaForm: {},
				addTeaForm: {
					sex: '男',
				},
				editTeaDialogVisible: false,
				addDialogVisible:false,
				editTeaFormRules: {
					
				},
				addTeaFormRules: {
					zhanghu: [
						{ required: true,message: '学号不能为空！',trigger: 'change' }
					],
					password: [						
						{ min: 6,max: 16,message: '密码长度为6-16位',ttigger: 'change' }
					],
					name: [
						{ required: true,message: '姓名不能为空！',trigger:'change' }
					],		
					college: [
						{ required: true,message: '学院信息不能为空！',trigger:'change' }
					],
					major: [
						{ required: true,message: '专业信息不能为空！',trigger:'change' }
					],
					email: [
						{ required: true,message: '邮箱不能为空！', trigger: 'change' },
						{ validator: checkEmail }
					]
				},
				CollegeData: [],
				MajorData: [],	
				password: ''
			}
		},
		mounted: function() {
			this.$nextTick(function () {
				this.tableHeight = window.innerHeight - this.$refs.table.$el.offsetTop - 95;
				let self = this;
				window.onresize = function() {
					self.tableHeight = window.innerHeight - self.$refs.table.$el.offsetTop - 95
				}
			})
		},
		created() {
			this.saveNavState('/admin/teamanage')
			this.getTeaList()
			this.queryInfo.query = ''
			this.queryInfo.select_college = ''
			this.queryInfo.select_major = ''
			this.queryInfo.select_banji = ''
			this.queryInfo.select_sex = ''
		},
		methods: {	
			// 添加教师按钮事件
			addTeaButton() {
				
				this.MajorData = ''
				
				this.addDialogVisible = true
				// 获取学院信息
				this.$axios.get('teacher/getcollege')
				.then(res => {
					if (res.status !== 200)
						return this.$message.error("获取学院信息失败！")
					this.CollegeData = res.data					
				})
			},
			// 选择学院信息后触发事件
			collegechange(key) {
				this.$axios.post('teacher/getmajor/' + key)
				.then(res => {
					if (res.status !== 200) {
						return this.$message.error("获取专业信息失败！")
					}
					this.MajorData = res.data
					this.editTeaForm.major = ''
				})
			},		
			// 添加教师信息
			addTea() {
				// 验证表单信息				
				this.$refs.addTeaForm.validate(async valid => {
					if (valid) {
						this.$axios.post('admin/addtea',this.addTeaForm)
						.then(res => {
							
							if (res.status !== 200) {
								return this.$message.error("添加教师信息失败！")
							}
							if (res.status === 200) {
								this.addDialogVisible = false
								this.$message.success("添加教师信息成功！")
								this.getTeaList()					
							}									
						})
					}
				})				
			},
			// 监听 添加学生信息 对话框关闭事件
			addDialogClosed() {
				//取消表验证
				this.$refs['addTeaForm'].resetFields();
				//this.$refs.addStuFormRef.resetFields()
			},
			// 情况搜索数据
			clearValues() {
				this.queryInfo.query = '',
				this.queryInfo.select_college = '',
				this.queryInfo.select_major = '',
				this.queryInfo.select_banji = '',
				this.queryInfo.select_sex = '',
				this.getTeaList()
			},
			// 监听 pagesize改变事件
			handleSizeChange (newSize) {
				this.queryInfo.pagesize = newSize
				this.getTeaList()
			},
			// 监听 页码值 改变事件
			handleCurrentChange (newSize) {
			  // console.log(newSize)
			  this.queryInfo.pagenum = newSize
			  this.getTeaList()()
			},
			// 获取学生信息列表函数
			getTeaList() {
				// 获取学院信息
				this.$axios.get('admin/getcollegegroup')
				.then( res => {
					if (res.status !== 200) {
						return this.$message.error("获取学院信息失败！")
					}
					else {
						this.college = res.data	
						//console.log(res.data)
					}
				})
				
				// 获取专业信息
				this.$axios.get('admin/getmajorgroup')
				.then( res => {
					if (res.status !== 200) {
						return this.$message.error("获取专业信息失败！")
					}
					else {
						this.major = res.data	
					}
				})													
				// 获取教师信息列表
				this.$axios.post('admin/tealist',{
					pageNum:  this.queryInfo.pagenum,
					pageSize: this.queryInfo.pagesize,
					college:  this.queryInfo.select_college,
					major:    this.queryInfo.select_major,					
					sex:	  this.queryInfo.select_sex,
					query:    this.queryInfo.query,
					//params: this.queryInfo
				})
				.then(res => {
					if (res.status !== 200) {
						return this.$message.error("获取教师信息列表失败！")
					}
					console.log(res)
					this.TeaList = res.data.records
					this.totle = res.data.total
				})
			},
			// 编辑教师信息
			showEditDialog (zhanghu) {
				
				this.MajorData = ''
				
				this.$axios.get("teacher/getteacherself/" + zhanghu)
				.then(res => {
					if (res.status !== 200)
						return this.$message.error("获取用户信息失败！")
					else					
						//console.log(res.data)
						this.editTeaForm = res.data
						this.password = res.data.password
						this.editTeaForm.password = ''
						this.editTeaDialogVisible = true
				})
				
				// 获取学院信息
				this.$axios.get('teacher/getcollege')
				.then(res => {
					if (res.status !== 200)
						return this.$message.error("获取学院信息失败！")
					this.CollegeData = res.data
				})
				
			},
			// 修改教师信息
			editTea () {
				this.$refs.editTeaForm.validate(async valid => {
					if (valid) {
						if (this.editTeaForm.password === '') {
							this.editTeaForm.password = this.password
						}
						this.$axios.put("admin/updatetea/" + this.editTeaForm.zhanghu,this.editTeaForm)
						.then(res => {
							//console.log(res)
							if (res.status !== 200 ) {
								return this.$message.error("更新教师信息失败！")
							}
							this.editTeaDialogVisible = false
							this.$message.success("更新教师信息成功！")
							this.getTeaList()							
						})
					}
				})				
			},
			// 删除当起行数据
			async removeTeaByZhanghu(zhanghu) {
			   const confirmResult = await this.$confirm('此操作将永久删除该用户，是否继续？',
				'提示',
				{
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).catch(err => err)
				//console.log(confirmResult)
				if (confirmResult !== 'confirm'){
					return this.$message.info("已取消删除")
				}
				this.$axios.delete("admin/deletetea/" + zhanghu)
				.then(res => {
					console.log(res)
					if (res.status !== 200) {
						this.$message.error("删除用户失败")
					}
				this.$message.success("删除用户成功！")
				this.getTeaList()
				})
			},
			// 保存链接的激活地址
			saveNavState(activePath) {
				this.resetSetItem('activePath',activePath)
				// window.sessionStorage.setItem('activePath',activePath)					
			}
		}
	}
</script>

<style scoped="scoped">
	#one {
		width: 100%;
		height: auto;		
	}
	#two {
		width: 100%;
		height: 100%;	
	}
	.el-card {
		height: 100%;
		width: 100%;
	}
	.el-pagination {
		text-align: left;
		padding-top: 20px;
	}
	#row1 {
		padding-top: 20px;
	}
</style>
