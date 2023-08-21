<template>
  <div id="member-app" class="dashboard-container">
    <div class="container">
      <div class="tableBar">
        <el-input
          v-model="input"
          placeholder="请输入员工信息"
          style="width: 250px"
          clearable
          @keyup.enter.native="handleQuery"
        />

        <el-select v-model="selectedOption" placeholder="状态">
          <el-option label="全部" :value="2" />
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>

        <i
          slot="prefix"
          class="el-input__icon el-icon-search"
          style="cursor: pointer"
          @click="handleQuery"
        />

        <el-button
          type="primary"
          @click="addMemberHandle('add')"
        >
          + 添加员工
        </el-button>
      </div>
      <el-table
        :data="tableData"
        stripe
        class="tableBox"
      >

        <el-table-column
          prop="name"
          label="员工姓名"
        />
        <el-table-column
          prop="username"
          label="账号"
        />
        <el-table-column
          prop="phone"
          label="手机号"
        />
        <el-table-column
          prop="sex"
          label="性别"
        ><template slot-scope="scope">
          {{ String(scope.row.sex) === '0' ? '女' : '男' }}
        </template>
        </el-table-column>
        <el-table-column label="账号状态">
          <template slot-scope="scope">
            {{ String(scope.row.status) === '0' ? '已禁用' : '正常' }}
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="160"
          align="center"
        >
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              class="blueBug"
              :class="{notAdmin:user !== 'admin'}"
              @click="addMemberHandle(scope.row.id)"
            >
              编辑
            </el-button>
            <el-button
              v-if="user === 'admin'"
              type="text"
              size="small"
              class="delBut non"
              @click="statusHandle(scope.row)"
            >
              {{ scope.row.status == '1' ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        class="pageList"
        :page-sizes="[10, 20, 30, 40]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="counts"
        :current-page.sync="page"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>

</template>

<script>
import { getMemberList, enableOrDisableEmployee } from '../../api/member.js'
export default {
  data() {
    return {
      input: '',
      counts: 0,
      page: 1,
      pageSize: 10,
      tableData: [],
      id: '',
      status: this.selectedOption,
      selectedOption: 2
    }
  },
  computed: {},
  created() {
    this.init()
  },
  mounted() {},
  methods: {
    selectValue() {
      return this.selectedOption
    },
    async init() {
      const params = {
        page: this.page,
        pageSize: this.pageSize,
        name: this.input ? this.input : undefined,
        phone: this.input ? this.input : undefined,
        username: this.input ? this.input : undefined,
        status: this.selectValue()
      }
      await getMemberList(params).then(res => {
        if (String(res.code) === '1') {
          this.tableData = res.data.records || []
          this.counts = res.data.total
          console.log('====', res.data.records || [])
        }
      }).catch(err => {
        this.$message.error('请求出错了：' + err)
      })
    },
    handleQuery() {
      this.page = 1
      this.init()
    },
    // 添加
    addMemberHandle(st) {
      if (st === 'add') {
        window.parent.menuHandle({
          id: '2',
          url: '/backend/page/member/add.html',
          name: '添加员工'
        }, true)
      } else {
        window.parent.menuHandle({
          id: '2',
          url: '/backend/page/member/add.html?id=' + st,
          name: '修改员工'
        }, true)
      }
    },
    // 状态修改
    statusHandle(row) {
      this.id = row.id
      this.status = row.status
      this.$confirm('确认调整该账号的状态?', '提示', {
        'confirmButtonText': '确定',
        'cancelButtonText': '取消',
        'type': 'warning'
      }).then(() => {
        enableOrDisableEmployee({ 'id': this.id, 'status': !this.status ? 1 : 0 }).then(res => {
          console.log('enableOrDisableEmployee', res)
          if (String(res.code) === '1') {
            this.$message.success('账号状态更改成功！')
            this.handleQuery()
          }
        }).catch(err => {
          this.$message.error('请求出错了：' + err)
        })
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.init()
    },
    handleCurrentChange(val) {
      this.page = val
      this.init()
    }
  }
}
</script>

<style lang="scss" scoped>
.wscn-http404-container{
  transform: translate(-50%,-50%);
  position: absolute;
  top: 40%;
  left: 50%;
}
.wscn-http404 {
  position: relative;
  width: 1200px;
  padding: 0 50px;
  overflow: hidden;
  .pic-404 {
    position: relative;
    float: left;
    width: 600px;
    overflow: hidden;
    &__parent {
      width: 100%;
    }
    &__child {
      position: absolute;
      &.left {
        width: 80px;
        top: 17px;
        left: 220px;
        opacity: 0;
        animation-name: cloudLeft;
        animation-duration: 2s;
        animation-timing-function: linear;
        animation-fill-mode: forwards;
        animation-delay: 1s;
      }
      &.mid {
        width: 46px;
        top: 10px;
        left: 420px;
        opacity: 0;
        animation-name: cloudMid;
        animation-duration: 2s;
        animation-timing-function: linear;
        animation-fill-mode: forwards;
        animation-delay: 1.2s;
      }
      &.right {
        width: 62px;
        top: 100px;
        left: 500px;
        opacity: 0;
        animation-name: cloudRight;
        animation-duration: 2s;
        animation-timing-function: linear;
        animation-fill-mode: forwards;
        animation-delay: 1s;
      }
      @keyframes cloudLeft {
        0% {
          top: 17px;
          left: 220px;
          opacity: 0;
        }
        20% {
          top: 33px;
          left: 188px;
          opacity: 1;
        }
        80% {
          top: 81px;
          left: 92px;
          opacity: 1;
        }
        100% {
          top: 97px;
          left: 60px;
          opacity: 0;
        }
      }
      @keyframes cloudMid {
        0% {
          top: 10px;
          left: 420px;
          opacity: 0;
        }
        20% {
          top: 40px;
          left: 360px;
          opacity: 1;
        }
        70% {
          top: 130px;
          left: 180px;
          opacity: 1;
        }
        100% {
          top: 160px;
          left: 120px;
          opacity: 0;
        }
      }
      @keyframes cloudRight {
        0% {
          top: 100px;
          left: 500px;
          opacity: 0;
        }
        20% {
          top: 120px;
          left: 460px;
          opacity: 1;
        }
        80% {
          top: 180px;
          left: 340px;
          opacity: 1;
        }
        100% {
          top: 200px;
          left: 300px;
          opacity: 0;
        }
      }
    }
  }
  .bullshit {
    position: relative;
    float: left;
    width: 300px;
    padding: 30px 0;
    overflow: hidden;
    &__oops {
      font-size: 32px;
      font-weight: bold;
      line-height: 40px;
      color: #1482f0;
      opacity: 0;
      margin-bottom: 20px;
      animation-name: slideUp;
      animation-duration: 0.5s;
      animation-fill-mode: forwards;
    }
    &__headline {
      font-size: 20px;
      line-height: 24px;
      color: #222;
      font-weight: bold;
      opacity: 0;
      margin-bottom: 10px;
      animation-name: slideUp;
      animation-duration: 0.5s;
      animation-delay: 0.1s;
      animation-fill-mode: forwards;
    }
    &__info {
      font-size: 13px;
      line-height: 21px;
      color: grey;
      opacity: 0;
      margin-bottom: 30px;
      animation-name: slideUp;
      animation-duration: 0.5s;
      animation-delay: 0.2s;
      animation-fill-mode: forwards;
    }
    &__return-home {
      display: block;
      float: left;
      width: 110px;
      height: 36px;
      background: #1482f0;
      border-radius: 100px;
      text-align: center;
      color: #ffffff;
      opacity: 0;
      font-size: 14px;
      line-height: 36px;
      cursor: pointer;
      animation-name: slideUp;
      animation-duration: 0.5s;
      animation-delay: 0.3s;
      animation-fill-mode: forwards;
    }
    @keyframes slideUp {
      0% {
        transform: translateY(60px);
        opacity: 0;
      }
      100% {
        transform: translateY(0);
        opacity: 1;
      }
    }
  }
}
</style>
