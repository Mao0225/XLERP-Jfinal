package com.xlerp.api.System.Service;

import com.xlerp.common.model.Sysuser;
import com.jfinal.plugin.activerecord.Page;

public class UserService {
    private static final Sysuser dao = new Sysuser().dao();

    public Page<Sysuser> paginate(int pageNumber, int pageSize, String username) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from sysuser");
        if (username != null && !username.trim().isEmpty()) {
            from.append(" where username like ? and status = 0");
        }else {
            from.append(" where status = 0");
        }
        from.append(" order by id desc");
        if (username != null && !username.trim().isEmpty()) {
            return dao.paginate(pageNumber, pageSize, select, from.toString(), "%" + username + "%");
        } else {
            return dao.paginate(pageNumber, pageSize, select, from.toString());
        }
    }

    public Sysuser findById(int id) {
        return dao.findById(id);
    }

    public boolean save(Sysuser user) {
        user.setStatus(0);//默认为0
        return user.save();
    }

    public boolean update(Sysuser user) {
        return user.update();
    }

    public boolean deleteById(int id) {
        Sysuser user = dao.findById(id);
        if (user == null) {
            return false; // 用户不存在，删除失败
        }
        user.setStatus(-1); // 设置为删除状态
        return user.update(); // 返回更新结果
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        // 查找用户
        Sysuser user = dao.findById(userId);
        if (user == null) {
            return false; // 用户不存在
        }

        if (!user.getPassword().equals(oldPassword)) {
            return false; // 旧密码错误
        }
        user.setPassword(newPassword);
        return user.update();
    }
}