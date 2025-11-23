package com.xlerp.api.Tongzhi.Service;

import com.jfinal.plugin.activerecord.Db;

public class TongzhiService {

    public boolean updateStatus(String id, String status) {
        return Db.update("update bascontractitem set noticestatus = ? where id = ?", status, id) > 0;
    }

    public boolean updateBatchStatus(String noticeid, String status) {
        return Db.update("update bascontractitem set noticestatus = ? where noticeid = ?", status, noticeid) > 0;
    }
}