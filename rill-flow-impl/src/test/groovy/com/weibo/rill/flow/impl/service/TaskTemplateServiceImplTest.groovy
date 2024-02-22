package com.weibo.rill.flow.impl.service

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.weibo.rill.flow.olympicene.traversal.runners.AbstractTaskRunner
import com.weibo.rill.flow.task.template.dao.mapper.TaskTemplateDAO
import com.weibo.rill.flow.task.template.dao.model.TaskTemplateDO
import com.weibo.rill.flow.task.template.model.TaskTemplate
import com.weibo.rill.flow.task.template.model.TaskTemplateParams
import spock.lang.Specification
/**
 * TaskTemplateServiceImpl 测试类
 */
class TaskTemplateServiceImplTest extends Specification {
    TaskTemplateServiceImpl taskTemplateService = new TaskTemplateServiceImpl()
    AbstractTaskRunner functionTaskRunner = Mock(AbstractTaskRunner)
    AbstractTaskRunner choiceTaskRunner = Mock(AbstractTaskRunner)
    AbstractTaskRunner paasTaskRunner = Mock(AbstractTaskRunner)
    Map<String, AbstractTaskRunner> taskRunnerMap = ["functionTaskRunner": functionTaskRunner, "paasTaskRunner": paasTaskRunner, "choiceTaskRunner": choiceTaskRunner]
    TaskTemplateDAO taskTemplateDAO = Mock(TaskTemplateDAO)

    def setup() {
        functionTaskRunner.getCategory() >> "function"
        functionTaskRunner.getIcon() >> "function base64 icon code"
        functionTaskRunner.getFields() >> ["field1": "field1", "field2": "field2"]
        functionTaskRunner.isEnable() >> true
        choiceTaskRunner.getCategory() >> "choice"
        choiceTaskRunner.getIcon() >> "choice base64 icon code"
        choiceTaskRunner.getFields() >> ["field1": "field1", "field2": "field2"]
        choiceTaskRunner.isEnable() >> true
        paasTaskRunner.getCategory() >> "paas"
        paasTaskRunner.getIcon() >> "base64 icon code"
        paasTaskRunner.isEnable() >> false
        taskTemplateService.taskRunnerMap = taskRunnerMap
        taskTemplateService.taskTemplateDAO = taskTemplateDAO
    }

    def "test getTaskMetaDataList"() {
        when:
        JSONArray array = taskTemplateService.getTaskMetaDataList()
        then:
        array.size() == 2
        array.getJSONObject(0).getString("category") == "function"
        array.getJSONObject(0).getString("icon") == "function base64 icon code"
        array.getJSONObject(0).getJSONObject("fields") == ["field1": "field1", "field2": "field2"]
    }

    def "test getTaskTemplates"() {
        given:
        TaskTemplateParams params = TaskTemplateParams.builder().build()
        when:
        List<TaskTemplate> array = taskTemplateService.getTaskTemplates(params, 0, 1)
        then:
        array.size() == 2
        array.get(0).getCategory() == "function"
        array.get(0).getIcon() == "function base64 icon code"
        array.get(0).getMetaData().getFields() == ["field1": "field1", "field2": "field2"]
    }

    def "test getTaskTemplates when db return null"() {
        given:
        TaskTemplateParams params = TaskTemplateParams.builder().build()
        taskTemplateDAO.getTaskTemplateList(_) >> null
        when:
        List<TaskTemplate> array = taskTemplateService.getTaskTemplates(params, 0, 1)
        then:
        array.size() == 2
        array.get(0).getCategory() == "function"
        array.get(0).getIcon() == "function base64 icon code"
        array.get(0).getMetaData().getFields() == ["field1": "field1", "field2": "field2"]
    }

    def "test getTaskTemplates with templates"() {
        given:
        TaskTemplateParams params = TaskTemplateParams.builder().build()
        TaskTemplateDO taskTemplateDO = new TaskTemplateDO()
        taskTemplateDO.setCategory("function")
        taskTemplateDO.setIcon("function template base64 icon code")
        taskTemplateDO.setName("function template")
        taskTemplateDO.setEnable(1)
        taskTemplateDO.setType(0)
        taskTemplateDO.setSchema("{}")
        taskTemplateDO.setTaskYaml("resourceName: function template")
        taskTemplateDO.setId(1L)
        taskTemplateDO.setOutput("{}")
        taskTemplateDO.setCreateTime(new Date())
        taskTemplateDO.setUpdateTime(new Date())
        taskTemplateDAO.getTaskTemplateList(_) >> [taskTemplateDO]
        when:
        List<TaskTemplate> array = taskTemplateService.getTaskTemplates(params, 0, 1)
        then:
        array.size() == 3
        array.get(0).getCategory() == "function"
        array.get(0).getIcon() == "function base64 icon code"
        array.get(0).getMetaData().getFields() == ["field1": "field1", "field2": "field2"]
        array.get(2).getCategory() == "function"
        array.get(2).getId() == 1L
        array.get(2).getIcon() == "function template base64 icon code"
        array.get(2).getSchema() == "{}"
        array.get(2).getOutput() == "{}"
    }

    def "test createTaskTemplate"() {
        given:
        TaskTemplateDO taskTemplateDO = new TaskTemplateDO()
        taskTemplateDO.setCategory("function")
        taskTemplateDO.setName("function template")
        taskTemplateDO.setIcon("function template base64 icon code")
        taskTemplateDO.setSchema("{}")
        taskTemplateDO.setOutput("{}")
        taskTemplateDO.setTaskYaml("resourceName: function_template")
        taskTemplateDO.setEnable(1)
        taskTemplateDO.setType(0)
        taskTemplateDO.setCreateTime(new Date())
        taskTemplateDO.setUpdateTime(new Date())
        taskTemplateDAO.insert(_) >> 5L
        expect:
        5L == taskTemplateService.createTaskTemplate(JSON.toJSON(taskTemplateDO) as JSONObject)
    }

    def "test createTaskTemplate by setting default values"() {
        given:
        TaskTemplateDO taskTemplateDO = new TaskTemplateDO()
        taskTemplateDO.setCategory("function")
        taskTemplateDO.setName("function template")
        taskTemplateDO.setEnable(1)
        taskTemplateDO.setType(0)
        taskTemplateDO.setCreateTime(new Date())
        taskTemplateDO.setUpdateTime(new Date())
        taskTemplateDAO.insert(_) >> 5L
        expect:
        5L == taskTemplateService.createTaskTemplate(JSON.toJSON(taskTemplateDO) as JSONObject)
    }

    def "test createTaskTemplate with exception"() {
        when:
        taskTemplateService.createTaskTemplate(new JSONObject(["hello": "world"]))
        then:
        thrown Exception
    }

    def "test updateTaskTemplate without id"() {
        given:
        TaskTemplateDO taskTemplateDO = new TaskTemplateDO()
        taskTemplateDO.setCategory("function")
        taskTemplateDO.setName("function template")
        taskTemplateDO.setEnable(1)
        taskTemplateDO.setType(0)
        taskTemplateDO.setCreateTime(new Date())
        taskTemplateDO.setUpdateTime(new Date())
        when:
        taskTemplateService.updateTaskTemplate(JSON.toJSON(taskTemplateDO) as JSONObject)
        then:
        thrown IllegalArgumentException
    }

    def "test updateTaskTemplate when input cannot be parsed"() {
        when:
        taskTemplateService.updateTaskTemplate(new JSONObject(["hello": "world"]))
        then:
        thrown IllegalArgumentException
    }

    def "test updateTaskTemplate"() {
        given:
        TaskTemplateDO taskTemplateDO = new TaskTemplateDO()
        taskTemplateDO.setCategory("function")
        taskTemplateDO.setName("function template")
        taskTemplateDO.setEnable(1)
        taskTemplateDO.setId(5L)
        taskTemplateDO.setType(0)
        taskTemplateDO.setCreateTime(new Date())
        taskTemplateDO.setUpdateTime(new Date())
        taskTemplateDAO.update(_) >> 1
        expect:
        1 == taskTemplateService.updateTaskTemplate(JSON.toJSON(taskTemplateDO) as JSONObject)
    }

    def "test disableTaskTemplate"() {
        given:
        taskTemplateDAO.disable(_) >> 1
        expect:
        1 == taskTemplateService.disableTaskTemplate(1L)
    }

    def "test disableTaskTemplate throw exception"() {
        given:
        taskTemplateDAO.disable(_) >> {throw new Exception("timeout")}
        when:
        taskTemplateService.disableTaskTemplate(1L)
        then:
        thrown Exception
    }

    def "test enableTaskTemplate"() {
        given:
        taskTemplateDAO.enable(_) >> 0
        expect:
        0 == taskTemplateService.enableTaskTemplate(1L)
    }

    def "test enableTaskTemplate throw exception"() {
        given:
        taskTemplateDAO.enable(_) >> {throw new Exception("timeout")}
        when:
        taskTemplateService.enableTaskTemplate(1L)
        then:
        thrown Exception
    }
}