package com.xlerp.api.System.Service;

import com.xlerp.common.model.Sysusermenu;
import com.jfinal.plugin.activerecord.Db;

import java.util.List;
import java.util.ArrayList;

public class UserMenuService {
    private static final Sysusermenu dao = new Sysusermenu();

    public boolean addUsermenu(int userId, int menuId) {
        Sysusermenu userMenu = new Sysusermenu();
        userMenu.set("userid", userId).set("menuid", menuId);
        return userMenu.save();
    }

    public boolean deleteUsermenu(int userId, int menuId) {
        return Db.delete("delete from sysusermenu where userid = ? and menuid = ?", userId, menuId) > 0;
    }

    public List<Sysusermenu> findAll(int userId) {
        return dao.find("select * from sysusermenu where userid = ?", userId);
    }
    
}