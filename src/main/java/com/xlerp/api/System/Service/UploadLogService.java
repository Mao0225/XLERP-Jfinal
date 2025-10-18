package com.xlerp.api.System.Service;

import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.SysUploadLog;

public class UploadLogService {
    private static final SysUploadLog dao = new SysUploadLog().dao();
    public Page<SysUploadLog> paginate(int pageNum, int pageSz, String interfaceName) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from sys_upload_log");
        if (interfaceName != null && !interfaceName.trim().isEmpty()) {
            from.append(" where interfaceName like ?");
            from.append(" order by id desc");
            return dao.paginate(pageNum, pageSz, select, from.toString(), "%" + interfaceName + "%");
        } else {
            from.append(" order by id desc");
            return dao.paginate(pageNum, pageSz, select, from.toString());
        }
    }

    public SysUploadLog findById(int i) {
        return dao.findById(i);
    }
}
