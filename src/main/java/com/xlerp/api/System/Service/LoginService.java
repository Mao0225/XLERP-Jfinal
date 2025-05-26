package com.xlerp.api.System.Service;

import com.xlerp.common.model.Sysuser;

public class LoginService {
    private static final Sysuser dao = new Sysuser().dao();

    public Sysuser authenticate(String username, String password) {
        Sysuser user = dao.findFirst("select id,username,descr,avatar from sysuser where username = ? and password = ? and status = 0", username, password);
        return user; // Returns user if found, null if authentication fails
    }
}
