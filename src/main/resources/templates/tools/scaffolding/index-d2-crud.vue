<%
List persistentProps=domainClass.gormPersistentEntity.persistentProperties;
List excludedProps = ['id','version','dateCreated','lastUpdated'];
int propertySize=domainClass.gormPersistentEntity.persistentProperties?.size();
%>
<template>
  <d2-container>
    <template slot="header">${classZhAnnotation}管理</template>
    <el-button type="primary" @click="addRow">创建</el-button>
    <el-button type="primary" @click="deleteAll">删除</el-button>
    <el-button type="primary" @click="exportExcel">导出</el-button>
    <d2-crud
        ref="d2Crud"
        :columns="columns"
        :data="data"
        :loading="loading"
        :options="options"
        :loading-options="loadingOptions"
        selection-row
        @selection-change="handleSelectionChange"
        :pagination="pagination"
        @pagination-current-change="paginationCurrentChange"
        :rowHandle="rowHandle"
        :form-options="formOptions"
        :add-template="formTemplate"
        :edit-template="formTemplate"
        @dialog-open="handleDialogOpen"
        edit-title="修改"
        @row-edit="handleRowEdit"
        @dialog-cancel="handleDialogCancel"
        @row-remove="handleRowRemove"
        @row-add="handleRowAdd"
    />

  </d2-container>
</template>

<script>
import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import D2Crud from '@d2-projects/d2-crud'
import request from '@/plugin/axios'

Vue.use(ElementUI)
Vue.use(D2Crud)

export default {
  data() {
    return {
      columns: [
        <% domainClass.gormPersistentEntity.persistentProperties.eachWithIndex
    {
      property, index->
      if (excludedProps.contains(property.name)) {
        return
      }
      if (index > 7) {
        return
      }
      if (property.type != java.util.SortedSet && property.type != java.util.Set && property.type != ([] as
      Byte[]
    ).
      class && property.type != ([]
      as
      byte[]
    ).
      class && property.type != java.sql.Blob
    )
      {%>
        {
          title: '${propertiesZhAnnotation[property.name]}', key
        :
          '${property.name}'
        }
        <%if ((index + 1) != propertySize) {%>,
        <%
      }
      %>   <%
      }
    %>    <%
    }
  %>
  ],
    data: [],
        selectedData
  :
    [],
        rowHandle
  :
    {
      columnHeader: '操作',
          edit
    :
      {
        icon: 'el-icon-edit',
            text
      :
        '编辑',
            size
      :
        'small',
            show(index, row)
        {
          return true
        }
      ,
        disabled(index, row)
        {
          return false
        }
      }
    ,
      remove: {
        icon: 'el-icon-delete',
            text
      :
        '删除',
            size
      :
        'small',
            show(index, row)
        {
          return true
        }
      ,
        disabled(index, row)
        {
          return false
        }
      }
    }
  ,
    formTemplate: {
      id: {
        title: '标识',
            value
      :
        '',
            component
      :
        {
          show: false
        }
      }
    ,
      <%persistentProps.eachWithIndex
      {
        property, index->
        if (excludedProps.contains(property.name)) {
          return
        }
        def
        cp = constrainedProperties[property.name];
        String
        required = "required";
        if (cp?.nullable || cp?.blank) {
          required = "";
        }
        if (property.type == Boolean || property.type == boolean) {%>
          ${property.name}
        :
          {
            title: '${propertiesZhAnnotation[property.name]}',
                value
          :
            true,
                component
          :
            {
              name: 'el-select',
                  options
            :
              [{value: true, label: '是'}, {value: false, label: '否'}]
            }
          }
          <%if ((index + 1) != propertySize) {%>,
            <%
          }
        %>
        <%
        } else if (property.type && Number.isAssignableFrom(property.type) || (property.type?.isPrimitive() && property.type != boolean)) {%>
          ${property.name}
        :
          {
            title: '${propertiesZhAnnotation[property.name]}',
                value
          :
            true,
                component
          :
            {
              name: 'el-input-number'
            }
          }
          <%if ((index + 1) != propertySize) {%>,
            <%
          }
        %>
        <%
        } else if (property.type == String) {%>
          ${property.name}
        :
          {
            title: '${propertiesZhAnnotation[property.name]}',
                value
          :
            ''
          }
          <%if ((index + 1) != propertySize) {%>,
            <%
          }
        %>
        <%
        } else if (property.type == Date || property.type == java.sql.Date || property.type == java.sql.Time || property.type == Calendar) {%>
          ${property.name}
        :
          {
            title: '${propertiesZhAnnotation[property.name]}',
                value
          :
            '',
                component
          :
            {
              name: 'el-date-picker',
                  type
            :
              'date',
                  valueFormat
            :
              'yyyy-MM-dd'
            }
          }
          <%if ((index + 1) != propertySize) {%>,
            <%
          }
        %>
        <%
        } else if (property.type == URL) {
        } else if (property.type == TimeZone) {
        } else if (property.type == Locale) {
        } else if (property.type == Currency) {
        } else if (property.type == ([] as
        Byte[]
      ).
        class || property.type == ([]
        as
        byte[]
      ).
        class || property.type == java.sql.Blob
      )
        {
        }
      else
        if (org.yunchen.gb.core.GbSpringUtils.isDomain(property.type)) {
        } else if (property.type == java.util.SortedSet || property.type == java.util.Set) {
        }
      }
    %>
    }
  ,
    formOptions: {
      labelWidth: '80px',
          labelPosition
    :
      'left',
          saveLoading
    :
      false
    }
  ,
    options: {
      stripe: true,
          border
    :
      true,
          defaultSort
    :
      {
        prop: 'id',
            order
      :
        'ascending'
      }
    }
  ,
    loading: false,
        loadingOptions
  :
    {
      text: '拼命加载中',
          spinner
    :
      'el-icon-loading',
          background
    :
      'rgba(0, 0, 0, 0.8)'
    }
  ,
    pagination: {
      currentPage: 1,
          pageSize
    :
      10,
          total
    :
      0
    }
  }
  },
  mounted() {
    this.fetchData()
  },
  methods: {
    addRow() {
      this.\$refs.d2Crud.showDialog({
        mode: 'add'
      })
    },
    deleteAll() {
      this.\$confirm('确认删除, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        var idsList = this.selectedData.map(function (row) {
          return row.id
        })
        this.loading = true
        this.postDeleteIds(idsList)
      }).catch(() => {
        this.\$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    postDeleteIds(idsList) {
      request({
        url: '/${propertyName}/deletes',
        method: 'POST',
        data: JSON.stringify({ids: idsList.join(',')})
      }).then(res => {
        if (res.result) {
          //local删除row
          this.\$refs.d2Crud.d2CrudData = this.\$refs.d2Crud.d2CrudData.filter(function (row, index) {
            let inids = true
            idsList.forEach(id => {
              if (id == row.id) {
                inids = false
              }
            })
            return inids
          });
          //显示message
          setTimeout(() => {
            this.loading = false
            this.\$message({
              message: '删除成功',
              type: 'success'
            })
          }, 30)
        }
      }).catch(err => {
        console.log('err', err)
        this.loading = false
      })
    },
    exportExcel() {
      this.loading = true
      request({
        url: '/${propertyName}/download',
        method: 'POST',
        data: {},
        responseType: 'blob'
      }).then(res => {
        this.loading = false
        let blob = new Blob([res], {
          type: 'application/vnd.ms-excel'
        })
        //console.log(blob)
        let fileName = '${propertyName}-' + Date.parse(new Date()) + '.xls'
        if (window.navigator.msSaveOrOpenBlob) {
          // console.log(2)
          navigator.msSaveBlob(blob, fileName)
        } else {
          // console.log(3)
          var link = document.createElement('a')
          link.href = window.URL.createObjectURL(blob)
          link.download = fileName
          link.click()
          //释放内存
          window.URL.revokeObjectURL(link.href)
        }
      }).catch(err => {
        console.log('err', err)
        this.\$message({
          message: err,
          type: 'error'
        })
      })
    },
    handleRowAdd(row, done) {
      this.formOptions.saveLoading = true
      request({
        url: '/${propertyName}/save',
        method: 'POST',
        data: row
      }).then(res => {
        if (res.result) {
          row.id = res.id
          setTimeout(() => {
            this.\$message({
              message: '保存成功',
              type: 'success'
            });
            done()
            this.formOptions.saveLoading = false
          }, 30)
        } else {
          this.\$message({
            message: res.message,
            type: 'error'
          })
        }

      }).catch(err => {
        console.log('err', err)
        this.\$message({
          message: err,
          type: 'error'
        })
      })
    },
    handleSelectionChange(selection) {
      //console.log(selection)
      this.selectedData = selection
    },
    paginationCurrentChange(currentPage) {
      this.pagination.currentPage = currentPage
      this.fetchData()
    },
    handleDialogOpen({mode, row}) {
      //
    },
    fetchData() {
      this.loading = true;
      var max = this.pagination.pageSize;
      var offset = (this.pagination.currentPage - 1) * max;
      request({
        url: '/${propertyName}/json',
        method: 'POST',
        data: JSON.stringify({max: max, offset: offset, sort: 'id', order: 'desc'})
      }).then(res => {
        this.data = res.rows;
        this.pagination.total = res.total
        this.loading = false
      }).catch(err => {
        console.log('err', err)
        this.loading = false
      })
    },
    handleRowEdit({index, row}, done) {
      this.formOptions.saveLoading = true
      request({
        url: '/${propertyName}/update',
        method: 'POST',
        data: row
      }).then(res => {
        if (res.result) {
          setTimeout(() => {
            this.\$message({
              message: '编辑成功',
              type: 'success'
            })
            done()
            this.formOptions.saveLoading = false
          }, 30)
        } else {
          this.\$message({
            message: res.message,
            type: 'error'
          })
        }

      }).catch(err => {
        console.log('err', err)
        this.\$message({
          message: err,
          type: 'error'
        })
      })
    },
    handleDialogCancel(done) {
      done()
    },
    handleRowRemove({index, row}, done) {
      request({
        url: '/${propertyName}/delete',
        method: 'POST',
        data: JSON.stringify({id: row.id})
      }).then(res => {
        if (res.result) {
          setTimeout(() => {
            this.\$message({
              message: '删除成功',
              type: 'success'
            })
            done()
          }, 30)
        } else {
          this.\$message({
            message: res.message,
            type: 'error'
          })
        }

      }).catch(err => {
        console.log('err', err)
        this.\$message({
          message: err,
          type: 'error'
        })
      })
    }
  }
}
</script>
