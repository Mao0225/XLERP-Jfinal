package com.xlerp.api.ItemManagement.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.BasItemClass;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BasItemClassService {
    private static final BasItemClass dao = new BasItemClass();
    private static final String TABLE_NAME = "XLQCERP.bas_item_class";

    public Page<Record> paginate(int pageNumber, int pageSize, String classCode, String className, Integer type) {
        // 使用 LEFT JOIN 动态获取上级分类名称
        String select = "SELECT t.*, p.classname as parent_name";
        StringBuilder from = new StringBuilder(" FROM " + TABLE_NAME + " t LEFT JOIN " + TABLE_NAME + " p ON t.parentId = p.id WHERE t.flag = 0");

        List<Object> params = new ArrayList<>();

        if (StrKit.notBlank(classCode)) {
            from.append(" AND t.classcode LIKE ?");
            params.add("%" + classCode + "%");
        }
        if (StrKit.notBlank(className)) {
            from.append(" AND t.classname LIKE ?");
            params.add("%" + className + "%");
        }
        if (type != null) {
            from.append(" AND t.type = ?");
            params.add(type);
        }
        from.append(" ORDER BY t.classcode");

        System.out.println("执行分页查询SQL: " + select + from.toString());
        System.out.println("参数: " + params);

        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());

        // 调试信息
        if (page.getList() != null && !page.getList().isEmpty()) {
            System.out.println("=== 查询结果调试 ===");
            for (Record record : page.getList()) {
                System.out.println("分类: " + record.getStr("classname") +
                        ", parentId: " + record.getInt("parentid") +
                        ", parent_name: " + record.getStr("parent_name"));
            }
        }

        return page;
    }

    public Record findById(int id) {
        // 同样使用 LEFT JOIN 获取上级分类名称
        String sql = "SELECT t.*, p.classname as parent_name FROM " + TABLE_NAME + " t LEFT JOIN " + TABLE_NAME + " p ON t.parentId = p.id WHERE t.id = ? AND t.flag = 0";
        Record record = Db.findFirst(sql, id);

        if (record != null) {
            System.out.println("单条记录查询 - 分类: " + record.getStr("classname") +
                    ", parentId: " + record.getInt("parentid") +
                    ", parent_name: " + record.getStr("parent_name"));
        }
        return record;
    }

    public boolean save(BasItemClass basItemClass) {
        try {
            // 方法1：先查询最大ID，然后+1
            Integer maxId = Db.queryInt("SELECT COALESCE(MAX(id), 0) FROM " + TABLE_NAME);
            Integer nextId = maxId + 1;

            System.out.println("生成新ID: " + nextId + " (基于最大ID: " + maxId + ")");

            basItemClass.set("id", nextId);
            basItemClass.set("createtime", new Date());
            basItemClass.set("updatetime", new Date());
            basItemClass.set("flag", 0);

            boolean result = basItemClass.save();
            if (result) {
                System.out.println("分类保存成功，ID: " + nextId);
            } else {
                System.out.println("分类保存失败");
            }
            return result;
        } catch (Exception e) {
            System.out.println("保存分类时发生异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(BasItemClass basItemClass) {
        basItemClass.set("updatetime", new Date());
        return basItemClass.update();
    }

    public boolean deleteById(int id) {
        String sql = "UPDATE " + TABLE_NAME + " SET flag = 1, updatetime = ? WHERE id = ?";
        return Db.update(sql, new Date(), id) > 0;
    }

    /**
     * 获取所有分类（树形结构）
     */
    public List<Record> getAllClasses() {
        String sql = "SELECT t.*, p.classname as parent_name FROM " + TABLE_NAME + " t LEFT JOIN " + TABLE_NAME + " p ON t.parentId = p.id WHERE t.flag = 0 ORDER BY t.classcode";
        return Db.find(sql);
    }

    /**
     * 获取父级分类选项 - 修改为根据当前分类级别动态获取
     */
    public List<Record> getParentOptions(Integer currentId, Integer currentType) {
        StringBuilder sql = new StringBuilder("SELECT id, classcode, classname, type FROM " + TABLE_NAME + " WHERE flag = 0");

        // 根据当前分类级别确定可选的父级
        if (currentType != null) {
            if (currentType == 1) {
                // 一级分类不能有父级
                sql.append(" AND 1=0"); // 返回空结果
            } else if (currentType == 2) {
                // 二级分类只能选择一级分类作为父级
                sql.append(" AND type = 1");
            } else if (currentType == 3) {
                // 三级分类只能选择二级分类作为父级
                sql.append(" AND type = 2");
            }
        } else {
            // 新增时，默认可以选一级和二级
            sql.append(" AND type IN (1,2)");
        }

        if (currentId != null) {
            sql.append(" AND id != ?");
        }
        sql.append(" ORDER BY classcode");

        if (currentId != null) {
            return Db.find(sql.toString(), currentId);
        } else {
            return Db.find(sql.toString());
        }
    }

    /**
     * 根据父级ID获取子分类
     */
    public List<Record> getChildrenByParentId(Integer parentId) {
        String sql = "SELECT id, classcode, classname, type, status FROM " + TABLE_NAME + " WHERE parentId = ? AND flag = 0 ORDER BY classcode";
        return Db.find(sql, parentId);
    }

    /**
     * 检查编码是否存在
     */
    public boolean checkCodeExists(String classCode, Integer excludeId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE classcode = ? AND flag = 0");

        if (excludeId != null) {
            sql.append(" AND id != ?");
        }

        if (excludeId != null) {
            return Db.queryLong(sql.toString(), classCode, excludeId) > 0;
        } else {
            return Db.queryLong(sql.toString(), classCode) > 0;
        }
    }

    /**
     * 检查是否有子分类
     */
    public boolean hasChildren(Integer id) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE parentId = ? AND flag = 0";
        return Db.queryLong(sql, id) > 0;
    }

    /**
     * 根据分类级别获取分类列表
     */
    public List<Record> getClassesByLevel(Integer level) {
        String sql = "SELECT t.*, p.classname as parent_name FROM " + TABLE_NAME + " t LEFT JOIN " + TABLE_NAME + " p ON t.parentId = p.id WHERE t.type = ? AND t.flag = 0 ORDER BY t.classcode";
        return Db.find(sql, level);
    }

    /**
     * 获取所有一级分类
     */
    public List<Record> getFirstLevelClasses() {
        return getClassesByLevel(1);
    }

    /**
     * 获取所有二级分类
     */
    public List<Record> getSecondLevelClasses() {
        return getClassesByLevel(2);
    }

    /**
     * 获取所有三级分类
     */
    public List<Record> getThirdLevelClasses() {
        return getClassesByLevel(3);
    }

    /**
     * 根据分类编码查找分类
     */
    public Record findByCode(String classCode) {
        String sql = "SELECT t.*, p.classname as parent_name FROM " + TABLE_NAME + " t LEFT JOIN " + TABLE_NAME + " p ON t.parentId = p.id WHERE t.classcode = ? AND t.flag = 0";
        return Db.findFirst(sql, classCode);
    }

    /**
     * 批量更新分类状态
     */
    public boolean batchUpdateStatus(List<Integer> ids, String status) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        StringBuilder sql = new StringBuilder("UPDATE " + TABLE_NAME + " SET status = ?, updatetime = ? WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append("?");
        }
        sql.append(")");

        Object[] params = new Object[ids.size() + 2];
        params[0] = status;
        params[1] = new Date();
        for (int i = 0; i < ids.size(); i++) {
            params[i + 2] = ids.get(i);
        }

        return Db.update(sql.toString(), params) > 0;
    }

    /**
     * 统计分类数量
     */
    public long countClasses() {
        return Db.queryLong("SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE flag = 0");
    }

    /**
     * 统计各级别的分类数量
     */
    public List<Record> countClassesByLevel() {
        String sql = "SELECT type, COUNT(*) as count FROM " + TABLE_NAME + " WHERE flag = 0 GROUP BY type ORDER BY type";
        return Db.find(sql);
    }

    /**
     * 分页查询子分类
     */
    public Page<Record> paginateChildren(int pageNumber, int pageSize, Integer parentId, String classCode, String className) {
        String select = "SELECT t.*, p.classname as parent_name";
        StringBuilder from = new StringBuilder(" FROM " + TABLE_NAME + " t LEFT JOIN " + TABLE_NAME + " p ON t.parentId = p.id WHERE t.parentId = ? AND t.flag = 0");

        List<Object> params = new ArrayList<>();
        params.add(parentId);

        if (StrKit.notBlank(classCode)) {
            from.append(" AND t.classcode LIKE ?");
            params.add("%" + classCode + "%");
        }
        if (StrKit.notBlank(className)) {
            from.append(" AND t.classname LIKE ?");
            params.add("%" + className + "%");
        }
        from.append(" ORDER BY t.classcode");

        System.out.println("执行子分类分页查询SQL: " + select + from.toString());
        System.out.println("参数: " + params);

        return Db.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    /**
     * 根据ID查找分类（包含父级信息）
     */
    public Record findByIdWithParent(int id) {
        String sql = "SELECT t.*, p.classname as parent_name, p.classcode as parent_code, p.type as parent_type " +
                "FROM " + TABLE_NAME + " t " +
                "LEFT JOIN " + TABLE_NAME + " p ON t.parentId = p.id " +
                "WHERE t.id = ? AND t.flag = 0";
        return Db.findFirst(sql, id);
    }

    /**
     * 获取父级路径
     */
    public List<Record> getParentPath(int id) {
        List<Record> path = new ArrayList<>();
        getParentPathRecursive(id, path);
        // 反转列表，让根节点在前
        List<Record> reversedPath = new ArrayList<>();
        for (int i = path.size() - 1; i >= 0; i--) {
            reversedPath.add(path.get(i));
        }
        return reversedPath;
    }

    private void getParentPathRecursive(int id, List<Record> path) {
        Record current = findById(id);
        if (current != null) {
            path.add(current);
            Integer parentId = current.getInt("parentId");
            if (parentId != null) {
                getParentPathRecursive(parentId, path);
            }
        }
    }

    /**
     * 获取子分类数量
     */
    public long getChildrenCount(int parentId) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE parentId = ? AND flag = 0";
        return Db.queryLong(sql, parentId);
    }

    /**
     * 根据父级ID获取所有子分类（不分页）
     */
    public List<Record> getAllChildrenByParentId(Integer parentId) {
        String sql = "SELECT t.*, p.classname as parent_name FROM " + TABLE_NAME + " t " +
                "LEFT JOIN " + TABLE_NAME + " p ON t.parentId = p.id " +
                "WHERE t.parentId = ? AND t.flag = 0 ORDER BY t.classcode";
        return Db.find(sql, parentId);
    }


    /**
     * 搜索分类（支持按名称和编码模糊匹配）
     */
    public List<Record> search(String keyword, Integer type) {
        StringBuilder sql = new StringBuilder("SELECT t.*, p.classname as parent_name FROM " + TABLE_NAME + " t LEFT JOIN " + TABLE_NAME + " p ON t.parentId = p.id WHERE t.flag = 0");

        List<Object> params = new ArrayList<>();

        if (StrKit.notBlank(keyword)) {
            sql.append(" AND (t.classname LIKE ? OR t.classcode LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        if (type != null) {
            sql.append(" AND t.type = ?");
            params.add(type);
        }

        sql.append(" ORDER BY t.type, t.classcode");

        System.out.println("搜索SQL: " + sql.toString());
        System.out.println("参数: " + params);

        return Db.find(sql.toString(), params.toArray());
    }

    /**
     * 获取指定分类的所有子孙分类
     */
    public List<Record> getAllDescendants(Integer parentId) {
        // 使用递归CTE查询所有子孙（如果数据库支持）
        // 这里使用多次查询的方式实现
        List<Record> allDescendants = new ArrayList<>();
        getAllDescendantsRecursive(parentId, allDescendants);
        return allDescendants;
    }

    private void getAllDescendantsRecursive(Integer parentId, List<Record> result) {
        List<Record> children = getChildrenByParentId(parentId);
        result.addAll(children);

        for (Record child : children) {
            getAllDescendantsRecursive(child.getInt("id"), result);
        }
    }

    /**
     * 根据ID列表获取分类
     */
    public List<Record> findByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder sql = new StringBuilder("SELECT t.*, p.classname as parent_name FROM " + TABLE_NAME + " t LEFT JOIN " + TABLE_NAME + " p ON t.parentId = p.id WHERE t.id IN (");
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append("?");
        }
        sql.append(") AND t.flag = 0 ORDER BY t.type, t.classcode");

        return Db.find(sql.toString(), ids.toArray());
    }
    /**
     * 获取分类的完整层级名称
     */
    public String getFullHierarchyName(int id) {
        List<Record> path = getParentPath(id);
        StringBuilder fullName = new StringBuilder();
        for (Record record : path) {
            if (fullName.length() > 0) {
                fullName.append(" / ");
            }
            fullName.append(record.getStr("classname"));
        }
        return fullName.toString();
    }
}