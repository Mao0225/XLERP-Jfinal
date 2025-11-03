package com.xlerp.api.ItemManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.ItemManagement.Service.BasItemClassService;
import com.xlerp.common.model.BasItemClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Before(HttpMethodInterceptor.class)
public class BasItemClassController extends Controller {
    private final BasItemClassService basItemClassService = new BasItemClassService();

    @ActionKey("/basitemclass/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String classCode = getPara("classCode");
        String className = getPara("className");
        String type = getPara("type");

        try {
            // 安全解析分页参数
            int pageNum = 1;
            int pageSz = 10;

            try {
                pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
                pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;
            } catch (NumberFormatException e) {
                renderJson(Result.badRequest("页码或每页大小格式错误"));
                return;
            }

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            // 安全处理 type 参数
            Integer typeInt = null;
            if (type != null && !type.trim().isEmpty()) {
                try {
                    typeInt = Integer.parseInt(type);
                } catch (NumberFormatException e) {
                    renderJson(Result.badRequest("分类级别格式错误"));
                    return;
                }
            }

            Page<Record> page = basItemClassService.paginate(pageNum, pageSz, classCode, className, typeInt);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.serverError("查询分类列表失败: " + e.getMessage()));
        }
    }

    @ActionKey("/basitemclass/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("分类ID不能为空"));
            return;
        }

        try {
            Record itemClass = basItemClassService.findById(Integer.parseInt(id));
            if (itemClass != null) {
                renderJson(Result.success("查询分类成功").putData("itemClass", itemClass));
            } else {
                renderJson(Result.notFound("分类未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("分类ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("查询分类失败: " + e.getMessage()));
        }
    }

    @ActionKey("/basitemclass/save")
    @HttpMethod("POST")
    public void save() {
        try {
            // 设置字符编码
            getRequest().setCharacterEncoding("UTF-8");

            // 调试信息：打印所有参数
            System.out.println("=== 开始处理保存请求 ===");
            System.out.println("Content-Type: " + getRequest().getContentType());

            // 使用 JFinal 的 getFile 方法来处理 multipart/form-data
            // 这会触发 JFinal 解析 multipart 请求
            getFile();

            // 现在可以使用 getPara 获取参数了
            String classcode = getPara("classcode");
            String classname = getPara("classname");
            String typeStr = getPara("type");
            String parentIdStr = getPara("parentId");
            String status = getPara("status");
            String systempreset = getPara("systempreset");
            String controlstrategy = getPara("controlstrategy");
            String createorg = getPara("createorg");
            String memo = getPara("memo");

            System.out.println("=== 获取到的参数 ===");
            System.out.println("classcode: '" + classcode + "'");
            System.out.println("classname: '" + classname + "'");
            System.out.println("type: '" + typeStr + "'");
            System.out.println("parentId: '" + parentIdStr + "'");
            System.out.println("status: '" + status + "'");
            System.out.println("systempreset: '" + systempreset + "'");
            System.out.println("controlstrategy: '" + controlstrategy + "'");
            System.out.println("createorg: '" + createorg + "'");
            System.out.println("memo: '" + memo + "'");

            // 检查必填字段
            if (classcode == null || classcode.trim().isEmpty()) {
                System.out.println("错误：分类编码为空");
                renderJson(Result.badRequest("分类编码不能为空"));
                return;
            }

            if (classname == null || classname.trim().isEmpty()) {
                System.out.println("错误：分类名称为空");
                renderJson(Result.badRequest("分类名称不能为空"));
                return;
            }

            if (typeStr == null || typeStr.trim().isEmpty()) {
                System.out.println("错误：分类级别为空");
                renderJson(Result.badRequest("分类级别不能为空"));
                return;
            }

            // 解析类型
            Integer type;
            try {
                type = Integer.parseInt(typeStr);
            } catch (NumberFormatException e) {
                System.out.println("错误：分类级别格式错误 - " + typeStr);
                renderJson(Result.badRequest("分类级别格式错误"));
                return;
            }

            // 检查编码是否重复
            if (basItemClassService.checkCodeExists(classcode, null)) {
                renderJson(Result.badRequest("分类编码已存在"));
                return;
            }

            // 创建 BasItemClass 对象
            BasItemClass basItemClass = new BasItemClass();
            basItemClass.set("classcode", classcode.trim());
            basItemClass.set("classname", classname.trim());
            basItemClass.set("type", type);

            // 设置父级ID
            if (parentIdStr != null && !parentIdStr.trim().isEmpty()) {
                try {
                    basItemClass.set("parentId", Integer.parseInt(parentIdStr.trim()));
                } catch (NumberFormatException e) {
                    basItemClass.set("parentId", null);
                }
            } else {
                basItemClass.set("parentId", null);
            }

            // 设置状态，有默认值
            if (status != null && !status.trim().isEmpty()) {
                basItemClass.set("status", status.trim());
            } else {
                basItemClass.set("status", "可用");
            }

            // 设置系统预设
            if (systempreset != null) {
                basItemClass.set("systempreset", systempreset.trim());
            } else {
                basItemClass.set("systempreset", "");
            }

            // 设置控制策略，有默认值
            if (controlstrategy != null && !controlstrategy.trim().isEmpty()) {
                basItemClass.set("controlstrategy", controlstrategy.trim());
            } else {
                basItemClass.set("controlstrategy", "全局共享");
            }

            // 设置创建组织，有默认值
            if (createorg != null && !createorg.trim().isEmpty()) {
                basItemClass.set("createorg", createorg.trim());
            } else {
                basItemClass.set("createorg", "电力金具");
            }

            // 设置描述
            if (memo != null) {
                basItemClass.set("memo", memo.trim());
            } else {
                basItemClass.set("memo", "");
            }

            // 验证层级关系
            if (!validateLevelRelation(basItemClass)) {
                renderJson(Result.badRequest("分类级别与父级不匹配"));
                return;
            }

            System.out.println("开始保存分类...");
            boolean success = basItemClassService.save(basItemClass);
            if (success) {
                System.out.println("分类保存成功，ID: " + basItemClass.getInt("id"));
                renderJson(Result.success("分类保存成功").putData("classId", basItemClass.getInt("id")));
            } else {
                System.out.println("分类保存失败");
                renderJson(Result.serverError("保存分类失败"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.serverError("保存分类时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/basitemclass/update")
    @HttpMethod("PUT")
    public void update() {
        try {
            // 设置字符编码
            getRequest().setCharacterEncoding("UTF-8");

            // 对于 PUT 请求，同样需要处理 multipart
            getFile();

            String idStr = getPara("id");
            String classcode = getPara("classcode");
            String classname = getPara("classname");
            String typeStr = getPara("type");
            String parentIdStr = getPara("parentId");
            String status = getPara("status");
            String systempreset = getPara("systempreset");
            String controlstrategy = getPara("controlstrategy");
            String createorg = getPara("createorg");
            String memo = getPara("memo");

            System.out.println("=== 更新操作参数 ===");
            System.out.println("id: '" + idStr + "'");
            System.out.println("classcode: '" + classcode + "'");
            System.out.println("classname: '" + classname + "'");
            System.out.println("type: '" + typeStr + "'");

            // 检查必填字段
            if (idStr == null || idStr.trim().isEmpty()) {
                renderJson(Result.badRequest("分类ID不能为空"));
                return;
            }

            if (classcode == null || classcode.trim().isEmpty()) {
                renderJson(Result.badRequest("分类编码不能为空"));
                return;
            }

            if (classname == null || classname.trim().isEmpty()) {
                renderJson(Result.badRequest("分类名称不能为空"));
                return;
            }

            if (typeStr == null || typeStr.trim().isEmpty()) {
                renderJson(Result.badRequest("分类级别不能为空"));
                return;
            }

            // 解析 ID 和类型
            Integer id;
            Integer type;
            try {
                id = Integer.parseInt(idStr);
                type = Integer.parseInt(typeStr);
            } catch (NumberFormatException e) {
                renderJson(Result.badRequest("ID或分类级别格式错误"));
                return;
            }

            // 检查编码是否重复（排除自身）
            if (basItemClassService.checkCodeExists(classcode, id)) {
                renderJson(Result.badRequest("分类编码已存在"));
                return;
            }

            // 创建 BasItemClass 对象
            BasItemClass basItemClass = new BasItemClass();
            basItemClass.set("id", id);
            basItemClass.set("classcode", classcode.trim());
            basItemClass.set("classname", classname.trim());
            basItemClass.set("type", type);

            // 设置父级ID
            if (parentIdStr != null && !parentIdStr.trim().isEmpty()) {
                try {
                    basItemClass.set("parentId", Integer.parseInt(parentIdStr.trim()));
                } catch (NumberFormatException e) {
                    basItemClass.set("parentId", null);
                }
            } else {
                basItemClass.set("parentId", null);
            }

            // 设置其他字段
            if (status != null && !status.trim().isEmpty()) {
                basItemClass.set("status", status.trim());
            }

            if (systempreset != null) {
                basItemClass.set("systempreset", systempreset.trim());
            }

            if (controlstrategy != null && !controlstrategy.trim().isEmpty()) {
                basItemClass.set("controlstrategy", controlstrategy.trim());
            }

            if (createorg != null && !createorg.trim().isEmpty()) {
                basItemClass.set("createorg", createorg.trim());
            }

            if (memo != null) {
                basItemClass.set("memo", memo.trim());
            }

            // 验证层级关系
            if (!validateLevelRelation(basItemClass)) {
                renderJson(Result.badRequest("分类级别与父级不匹配"));
                return;
            }

            // 检查是否修改了父级导致循环引用
            if (hasCircularReference(id, basItemClass.getInt("parentId"))) {
                renderJson(Result.badRequest("不能选择自己或自己的子分类作为父级"));
                return;
            }

            boolean success = basItemClassService.update(basItemClass);
            if (success) {
                renderJson(Result.success("分类更新成功"));
            } else {
                renderJson(Result.serverError("更新分类失败"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.serverError("更新分类时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/basitemclass/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("分类ID不能为空"));
            return;
        }

        try {
            // 检查是否有子分类
            if (basItemClassService.hasChildren(Integer.parseInt(id))) {
                renderJson(Result.badRequest("该分类下存在子分类，无法删除"));
                return;
            }

            boolean success = basItemClassService.deleteById(Integer.parseInt(id.trim()));
            if (success) {
                renderJson(Result.success("分类删除成功"));
            } else {
                renderJson(Result.notFound("分类不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("分类ID格式错误"));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.serverError("删除分类时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/basitemclass/tree")
    @HttpMethod("GET")
    public void tree() {
        try {
            renderJson(Result.success("获取分类树成功").putData("classTree", basItemClassService.getAllClasses()));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.serverError("获取分类树失败: " + e.getMessage()));
        }
    }

    @ActionKey("/basitemclass/parentoptions")
    @HttpMethod("GET")
    public void parentOptions() {
        String currentId = getPara("currentId");
        String currentType = getPara("currentType");

        try {
            Integer currentIdInt = (currentId != null && !currentId.trim().isEmpty()) ? Integer.parseInt(currentId) : null;
            Integer currentTypeInt = (currentType != null && !currentType.trim().isEmpty()) ? Integer.parseInt(currentType) : null;

            renderJson(Result.success("获取父级选项成功").putData("parentOptions", basItemClassService.getParentOptions(currentIdInt, currentTypeInt)));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("当前分类ID或类型格式错误"));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.serverError("获取父级选项失败: " + e.getMessage()));
        }
    }

    @ActionKey("/basitemclass/detail")
    @HttpMethod("GET")
    public void detail() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("分类ID不能为空"));
            return;
        }

        try {
            Record itemClass = basItemClassService.findById(Integer.parseInt(id));
            if (itemClass != null) {
                renderJson(Result.success("查询分类详情成功").putData("itemClass", itemClass));
            } else {
                renderJson(Result.notFound("分类未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("分类ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("查询分类详情失败: " + e.getMessage()));
        }
    }

    @ActionKey("/basitemclass/children")
    @HttpMethod("GET")
    public void children() {
        String parentId = getPara("parentId");

        if (parentId == null || parentId.trim().isEmpty()) {
            renderJson(Result.badRequest("父级分类ID不能为空"));
            return;
        }

        try {
            renderJson(Result.success("获取子分类成功").putData("children", basItemClassService.getChildrenByParentId(Integer.parseInt(parentId))));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("父级分类ID格式错误"));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.serverError("获取子分类失败: " + e.getMessage()));
        }
    }

    /**
     * 获取子分类分页列表
     */
    @ActionKey("/basitemclass/childrenpage")
    @HttpMethod("GET")
    public void childrenPage() {
        String parentId = getPara("parentId");
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String classCode = getPara("classCode");
        String className = getPara("className");

        if (parentId == null || parentId.trim().isEmpty()) {
            renderJson(Result.badRequest("父级分类ID不能为空"));
            return;
        }

        try {
            // 安全解析分页参数
            int pageNum = 1;
            int pageSz = 10;

            try {
                pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
                pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;
            } catch (NumberFormatException e) {
                renderJson(Result.badRequest("页码或每页大小格式错误"));
                return;
            }

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Record> page = basItemClassService.paginateChildren(pageNum, pageSz, Integer.parseInt(parentId), classCode, className);
            renderJson(Result.success("查询子分类成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("父级分类ID格式错误"));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.serverError("查询子分类失败: " + e.getMessage()));
        }
    }

    /**
     * 获取分类详情及父级信息
     */
    @ActionKey("/basitemclass/detailwithparent")
    @HttpMethod("GET")
    public void detailWithParent() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("分类ID不能为空"));
            return;
        }

        try {
            Record itemClass = basItemClassService.findByIdWithParent(Integer.parseInt(id));
            if (itemClass != null) {
                // 获取父级分类的完整路径
                List<Record> parentPath = basItemClassService.getParentPath(Integer.parseInt(id));
                renderJson(Result.success("查询分类详情成功")
                        .putData("itemClass", itemClass)
                        .putData("parentPath", parentPath));
            } else {
                renderJson(Result.notFound("分类未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("分类ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("查询分类详情失败: " + e.getMessage()));
        }
    }

    /**
     * 获取分类层级信息
     */
    @ActionKey("/basitemclass/hierarchydetail")
    @HttpMethod("GET")
    public void hierarchyDetail() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("分类ID不能为空"));
            return;
        }

        try {
            Record currentClass = basItemClassService.findById(Integer.parseInt(id));
            if (currentClass == null) {
                renderJson(Result.notFound("分类未找到"));
                return;
            }

            // 获取父级信息
            Record parentClass = null;
            Integer parentId = currentClass.getInt("parentId");
            if (parentId != null) {
                parentClass = basItemClassService.findById(parentId);
            }

            // 获取子分类数量
            long childrenCount = basItemClassService.getChildrenCount(Integer.parseInt(id));

            renderJson(Result.success("获取层级信息成功")
                    .putData("currentClass", currentClass)
                    .putData("parentClass", parentClass)
                    .putData("childrenCount", childrenCount));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("分类ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("获取层级信息失败: " + e.getMessage()));
        }
    }

    /**
     * 获取子分类列表（不分页，用于瀑布流展示）
     */
    @ActionKey("/basitemclass/childrenlist")
    @HttpMethod("GET")
    public void childrenList() {
        String parentId = getPara("parentId");

        if (parentId == null || parentId.trim().isEmpty()) {
            renderJson(Result.badRequest("父级分类ID不能为空"));
            return;
        }

        try {
            List<Record> children = basItemClassService.getAllChildrenByParentId(Integer.parseInt(parentId));
            renderJson(Result.success("获取子分类成功").putData("children", children));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("父级分类ID格式错误"));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.serverError("获取子分类失败: " + e.getMessage()));
        }
    }


    /**
     * 搜索分类（支持按名称和编码搜索所有级别）
     */
    @ActionKey("/basitemclass/search")
    @HttpMethod("GET")
    public void search() {
        String keyword = getPara("keyword");
        String type = getPara("type");

        try {
            // 安全处理 type 参数
            Integer typeInt = null;
            if (type != null && !type.trim().isEmpty()) {
                try {
                    typeInt = Integer.parseInt(type);
                } catch (NumberFormatException e) {
                    renderJson(Result.badRequest("分类级别格式错误"));
                    return;
                }
            }

            List<Record> searchResults = basItemClassService.search(keyword, typeInt);
            renderJson(Result.success("搜索成功").putData("searchResults", searchResults));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.serverError("搜索分类失败: " + e.getMessage()));
        }
    }

    /**
     * 获取分类的完整子级树
     */
    @ActionKey("/basitemclass/fullchildren")
    @HttpMethod("GET")
    public void fullChildren() {
        String parentId = getPara("parentId");

        if (parentId == null || parentId.trim().isEmpty()) {
            renderJson(Result.badRequest("父级分类ID不能为空"));
            return;
        }

        try {
            Map<String, Object> result = new HashMap<>();
            Integer parentIdInt = Integer.parseInt(parentId);

            // 获取直接子级
            List<Record> directChildren = basItemClassService.getChildrenByParentId(parentIdInt);
            result.put("directChildren", directChildren);

            // 获取所有子孙（用于搜索结果显示）
            List<Record> allDescendants = basItemClassService.getAllDescendants(parentIdInt);
            result.put("allDescendants", allDescendants);

            renderJson(Result.success("获取完整子级成功").putData("childrenTree", result));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("父级分类ID格式错误"));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.serverError("获取完整子级失败: " + e.getMessage()));
        }
    }
    /**
     * 获取一级分类列表（不分页，用于瀑布流展示）
     */
    @ActionKey("/basitemclass/level1")
    @HttpMethod("GET")
    public void level1() {
        try {
            List<Record> level1Classes = basItemClassService.getFirstLevelClasses();
            renderJson(Result.success("获取一级分类成功").putData("level1", level1Classes));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.serverError("获取一级分类失败: " + e.getMessage()));
        }
    }

    /**
     * 验证层级关系 - 增强版
     */
    private boolean validateLevelRelation(BasItemClass itemClass) {
        Integer parentId = itemClass.getInt("parentId");
        Integer type = itemClass.getInt("type");

        System.out.println("验证层级关系: type=" + type + ", parentId=" + parentId);

        // 一级分类不能有父级
        if (type == 1) {
            if (parentId != null) {
                System.out.println("验证失败：一级分类不能有上级分类");
                return false;
            }
            return true;
        }

        // 二级分类必须有一级父级
        if (type == 2) {
            if (parentId == null) {
                System.out.println("验证失败：二级分类必须选择上级分类");
                return false;
            }
            Record parent = basItemClassService.findById(parentId);
            if (parent == null) {
                System.out.println("验证失败：上级分类不存在");
                return false;
            }
            if (parent.getInt("type") != 1) {
                System.out.println("验证失败：二级分类的上级必须是一级分类，当前上级类型为: " + parent.getInt("type"));
                return false;
            }
            return true;
        }

        // 三级分类必须有二级父级
        if (type == 3) {
            if (parentId == null) {
                System.out.println("验证失败：三级分类必须选择上级分类");
                return false;
            }
            Record parent = basItemClassService.findById(parentId);
            if (parent == null) {
                System.out.println("验证失败：上级分类不存在");
                return false;
            }
            if (parent.getInt("type") != 2) {
                System.out.println("验证失败：三级分类的上级必须是二级分类，当前上级类型为: " + parent.getInt("type"));
                return false;
            }
            return true;
        }

        System.out.println("验证失败：未知的分类级别: " + type);
        return false;
    }

    /**
     * 检查循环引用
     */
    private boolean hasCircularReference(Integer currentId, Integer parentId) {
        if (currentId == null || parentId == null) {
            return false;
        }

        // 不能选择自己作为父级
        if (currentId.equals(parentId)) {
            return true;
        }

        // 检查父级的父级是否指向当前分类（递归检查）
        return checkParentChain(parentId, currentId);
    }

    private boolean checkParentChain(Integer checkId, Integer targetId) {
        if (checkId == null) {
            return false;
        }

        Record record = basItemClassService.findById(checkId);
        if (record == null) {
            return false;
        }

        Integer parentId = record.getInt("parentId");
        if (parentId == null) {
            return false;
        }

        if (parentId.equals(targetId)) {
            return true;
        }

        return checkParentChain(parentId, targetId);
    }
}