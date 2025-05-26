package com.xlerp.api.System.Service;


import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.Sysmenu;
import com.xlerp.common.model.Sysusermenu;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MenuService {
    private static final Sysmenu dao = new Sysmenu();

    public Page<Sysmenu> paginate(int pageNumber, int pageSize) {
        return dao.paginate(pageNumber, pageSize, "select *", "from sysmenu order by id desc");
    }

    public Sysmenu findById(int id) {
        return dao.findById(id);
    }

    public boolean save(Sysmenu menu) {
        return menu.save();
    }

    public boolean update(Sysmenu menu) {
        return menu.update();
    }

    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }



    private List<Record> buildChildren(int parentId, List<Sysmenu> menus) {
        List<Record> children = new ArrayList<>();
        for (Sysmenu menu : menus) {
            if (parentId == menu.getInt("parentid")) {
                Record node = new Record();
                node.set("id", menu.getInt("id"));
                node.set("path", menu.getStr("path"));
                node.set("title", menu.getStr("title"));
                node.set("icon", menu.getStr("icon"));
                node.set("layout", menu.getStr("layout"));
                node.set("component", menu.getStr("component"));
                node.set("children", buildChildren(menu.getInt("id"), menus));
                children.add(node);
            }
        }
        return children;
    }
    public List<Record> getMenuTreeByUserId(int userId) {
        System.out.println("开始根据用户ID获取菜单树，用户ID: " + userId);
        
        Sysusermenu  usermenu = new Sysusermenu();
        List<Sysusermenu> usermenus = usermenu.find("select menuid from sysusermenu where userid = ?", userId);
        return getMenuTree(usermenus);
    }
    public List<Record> getMenuTree(List<Sysusermenu> usermenus) {
        // 初始化菜单 DAO
        Sysmenu menu = new Sysmenu();

        // 确定要查询的菜单 ID
        List<Integer> menuIds;
        if (usermenus == null || usermenus.isEmpty()) {
            // 情况 1：未提供用户菜单，获取所有菜单 ID
            System.out.println("未提供用户菜单，查询所有菜单 ID...");
            List<Sysmenu> allMenus = menu.find("select id from sysmenu");
            menuIds = allMenus.stream().map(m -> m.getInt("id")).collect(Collectors.toList());
            System.out.println("所有菜单 ID 列表: " + menuIds);
        } else {
            // 情况 2：提供用户菜单，提取关联的菜单 ID
            menuIds = usermenus.stream().map(rm -> rm.getInt("menuid")).collect(Collectors.toList());
            System.out.println("用户拥有的菜单 ID 列表: " + menuIds);
        }

        // 处理空菜单 ID 的情况
        if (menuIds.isEmpty()) {
            System.out.println("未找到任何菜单 ID，返回空列表");
            return new ArrayList<>();
        }

        // 查询菜单详细信息
        System.out.println("查询菜单表中对应的菜单详细信息...");
        String sql = "select * from sysmenu where id in (" + String.join(",", menuIds.stream().map(String::valueOf).collect(Collectors.toList())) + ")";
        System.out.println("执行的 SQL 语句: " + sql);
        List<Sysmenu> menus = menu.find(sql);
        System.out.println("查询到的菜单数量: " + menus.size());

        // 构建菜单树
        System.out.println("开始构建菜单树结构...");
        List<Record> tree = new ArrayList<>();
        for (Sysmenu m : menus) {
            // 顶级菜单：parentid 为 null 或 0
            if (m.getInt("parentid") == null || m.getInt("parentid") == 0) {
                System.out.println("发现顶级菜单: ID=" + m.getInt("id") + ", 名称=" + m.getStr("title"));
                Record node = new Record();
                node.set("id", m.getInt("id"));
                node.set("path", m.getStr("path"));
                node.set("title", m.getStr("title"));
                node.set("icon", m.getStr("icon"));
                node.set("layout", m.getStr("layout"));
                node.set("component", m.getStr("component"));

                List<Record> children = buildChildren(m.getInt("id"), menus);
                System.out.println("菜单 ID=" + m.getInt("id") + "的子菜单数量: " + children.size());
                node.set("children", children);
                tree.add(node);
            }
        }

        System.out.println("菜单树构建完成，共找到 " + tree.size() + " 个顶级菜单");
        return tree;
    }
}