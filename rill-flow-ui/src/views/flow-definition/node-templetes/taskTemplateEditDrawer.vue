<template>
  <BasicDrawer
    v-bind="$attrs"
    @register="registerDrawer"
    showFooter
    title="编辑任务模板"
    width="400"
    @ok="handleSubmit"
  >
    <BasicForm @register="registerForm" autoFocusFirstItem:true :actionColOptions="{ span: 24 }" />

    <template #insertFooter>
      <a-button @click="handlePreviewSchema">预览 schema</a-button>
    </template>
  </BasicDrawer>
  <SchemaPreviewModal @register="schemaPreviewModalRegister" :minHeight="100" />
</template>

<script lang="ts">
import {defineComponent} from 'vue';
import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
import {useForm, BasicForm} from "@/components/Form";
import {createTemplateApi, updateTemplateApi} from "@/api/table";
import {useModal} from "@/components/Modal";
import SchemaPreviewModal from "@/views/flow-definition/node-templetes/schemaPreviewModal.vue";
import {templateSchema} from "@/views/flow-definition/node-templetes/tableData";

export default defineComponent({
  name: 'taskTemplateEditDrawer',
  components: {
    SchemaPreviewModal,
    BasicDrawer,
    BasicForm,
  },

  setup(_, { emit }) {
    let action;
    let id;

    const [schemaPreviewModalRegister, { openModal }] = useModal();

    const formSchemas = [
      {
        field: 'name',
        component: 'Input',
        label: '模板名称',
        rules: [{ required: true }],
      },
      {
        field: 'type',
        component: 'Select',
        label: '模板类型',
        componentProps: {
          options: [
            { label: "函数模板", value: 0},
            { label: "插件模板", value: 1},
            { label: "逻辑模板", value: 2}
          ],
          multiple: false,
          filterable: true
        },
      },
      {
        field: 'category',
        component: 'Input',
        label: 'category',
        rules: [{ required: true }],
      },
      {
        field: 'icon',
        component: 'InputTextArea',
        label: 'icon base64',
      }, {
        field: 'task_yaml',
        component: 'InputTextArea',
        label: '模板任务默认 yaml',
      }, {
        field: 'schema',
        component: 'InputTextArea',
        label: '模板输入结构(schema)',
      }, {
        field: 'output',
        component: 'InputTextArea',
        label: '模板输出结构',
      }
    ]
    const [registerForm, { getFieldsValue, setFieldsValue, validateFields, resetFields }] = useForm({
        layout: 'vertical',
        schemas: formSchemas,
        showResetButton: false,
        showSubmitButton: false,
        labelWidth: 140,
        baseColProps: { span: 24 },
      });

    const [registerDrawer, { closeDrawer }] = useDrawerInner(async (data) => {
      action = data.action
      id = data.id
      resetFields()
      setFieldsValue(data)
    });

    async function handleSubmit() {
      const data = await validateFields();
      data.id = id
      let res;
      if (action == 'update') {
        res = await updateTemplateApi(data);
      } else {
        res = await createTemplateApi(data);
      }
      emit('response', res);
      closeDrawer();
    }

    function handlePreviewSchema() {
      const data = getFieldsValue()
      templateSchema.value = JSON.parse(data.schema)
      openModal(true);
    }

    return {
      registerDrawer,
      handleSubmit,
      registerForm,
      handlePreviewSchema,
      schemaPreviewModalRegister,
    };
  }

})
</script>