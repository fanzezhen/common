package com.github.fanzezhen.common.magic.doc.yapi;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.fanzezhen.common.magic.doc.CommonMagicDocProperties;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.ssssssss.magicapi.core.model.*;
import org.ssssssss.magicapi.core.web.MagicResourceController;
import org.ssssssss.magicapi.servlet.jakarta.MagicJakartaHttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author fanzezhen
 * @createTime 2024/2/5 15:32
 * @since 1
 */
@Service
@SuppressWarnings("unused")
public class YApiFacade {
    static final String KEY_PROPERTIES = "properties";
    @Resource
    private MagicResourceController magicResourceController;
    @Resource
    private CommonMagicDocProperties docProperties;

    public JSONArray sync(HttpServletRequest request) {
        return getApiJSONArray(request);
    }

    public JSONArray getApiJSONArray(HttpServletRequest request) {
        MagicJakartaHttpServletRequest magicHttpServletRequest = new MagicJakartaHttpServletRequest(request, null);
        JSONArray apis = new JSONArray();
        JsonBean<Map<String, TreeNode<Attributes<Object>>>> resources = magicResourceController.resources(magicHttpServletRequest);
        Map<String, TreeNode<Attributes<Object>>> treeNodeMap = resources.getData();
        TreeNode<Attributes<Object>> apiTreeNode = treeNodeMap.get("api");
        Attributes<Object> node = apiTreeNode.getNode();
        List<TreeNode<Attributes<Object>>> children = apiTreeNode.getChildren();
        loadApiJson(magicHttpServletRequest, apis, node, children);
        return apis;
    }

    private void loadApiJson(MagicJakartaHttpServletRequest request, JSONArray folders, Attributes<Object> folderNode, List<TreeNode<Attributes<Object>>> childrenList) {
        if (folderNode instanceof Group group && (childrenList != null && !childrenList.isEmpty())) {
            JSONArray apis = new JSONArray(childrenList.size());
            for (TreeNode<Attributes<Object>> treeNode : childrenList) {
                if (treeNode.getNode() instanceof ApiInfo apiInfo) {
                    JsonBean<MagicEntity> detail = magicResourceController.detail(apiInfo.getId(), request);
                    if (detail != null && detail.getData() instanceof ApiInfo magicEntity) addApi(apis, magicEntity);
                } else {
                    loadApiJson(request, folders, treeNode.getNode(), treeNode.getChildren());
                }
            }
            if (!apis.isEmpty()) {
                folders.fluentAdd(new JSONObject().fluentPut("name", group.getName()).fluentPut("list", apis));
            }
        }
    }

    private void addApi(JSONArray apis, ApiInfo magicEntity) {
        String path = magicEntity.getPath();
        if (path != null) {
            if (!path.startsWith(StrPool.SLASH)) {
                path = StrPool.SLASH + path;
            }
            JSONObject api = new JSONObject()
                .fluentPut("title", magicEntity.getName())
                .fluentPut("path", path)
                .fluentPut("query_path", new JSONObject().fluentPut("path", path))
                .fluentPut("status", "done")
                .fluentPut("res_body_type", "json")
                .fluentPut("res_body_is_json_schema", true)
                .fluentPut("req_headers", new JSONArray())
                .fluentPut("req_body_type", "json")
                .fluentPut("req_body_other", magicEntity.getRequestBody());
            List<Parameter> parameterList = magicEntity.getParameters();
            if (CollUtil.isNotEmpty(parameterList)) {
                api.fluentPut("req_query", parameterList.stream().map(parameter -> new JSONObject(BeanUtil.beanToMap(parameter))
                    .fluentPut("example", parameter.getValue())
                    .fluentPut("desc", parameter.getDescription())
                    .fluentPut("required", parameter.isRequired() ? 1 : 0)
                ).toList());
            }
            JSONObject properties = JSON.parseObject(docProperties.getResultJson());
            JSONObject resBody = new JSONObject()
                .fluentPut("$schema", "http://json-schema.org/draft-04/schema#")
                .fluentPut("type", "object")
                .fluentPut(KEY_PROPERTIES, properties);
            BaseDefinition responseBodyDefinition = magicEntity.getResponseBodyDefinition();
            if (responseBodyDefinition != null && CollUtil.isNotEmpty(responseBodyDefinition.getChildren())) {
                putResponse(properties, responseBodyDefinition.getChildren());
            }
            api.fluentPut("res_body", resBody.toJSONString());
            apis.fluentAdd(api);
        }
    }

    private static JSONObject putResponse(JSONObject properties, ArrayList<BaseDefinition> definitions) {
        if (definitions != null) {
            for (BaseDefinition definition : definitions) {
                String key = definition.getName();
                properties.computeIfAbsent(key, k -> {
                    String type = CharSequenceUtil.lowerFirst(definition.getDataType().name());
                    JSONObject json = new JSONObject()
                        .fluentPut("type", type)
                        .fluentPut("description", definition.getDescription());
                    if ("object".equals(type)) {
                        json.fluentPut(KEY_PROPERTIES, putResponse(new JSONObject(), definition.getChildren()));
                    } else if ("array".equals(type)) {
                        ArrayList<BaseDefinition> items = definition.getChildren();
                        if (CollUtil.isNotEmpty(items)) {
                            json.fluentPut("items", new JSONObject()
                                .fluentPut("type", CharSequenceUtil.lowerFirst(items.get(0).getDataType().name()))
                                .fluentPut(KEY_PROPERTIES, putResponse(new JSONObject(), items.get(0).getChildren())));
                        }
                    }
                    return json;
                });
            }
        }
        return properties;
    }
}
