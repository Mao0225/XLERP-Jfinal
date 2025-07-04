package com.xlerp.api.DataDetection.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.ACSR;

import java.util.ArrayList;
import java.util.List;

public class ACSRService {

    private static final ACSR dao = new ACSR();

    public Page<ACSR> paginate(int pageNumber, int pageSize, String RawmaterialManufacturer, String Size, String IncomingNo, String SinglefilamentStrength, String Factorydata, String Incomingdata, String QualityCertificate, String gridno) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from acsr where isdelete = 0");

        // 动态构建查询条件
        if (StrKit.notBlank(RawmaterialManufacturer)) {
            from.append(" and RawmaterialManufacturer like ?");
        }
        if (StrKit.notBlank(Size)) {
            from.append(" and Size like ?");
        }
        if (StrKit.notBlank(IncomingNo)) {
            from.append(" and IncomingNo like ?");
        }
        if (StrKit.notBlank(SinglefilamentStrength)) {
            from.append(" and SinglefilamentStrength like ?");
        }

        if (StrKit.notBlank(Factorydata)) {
            from.append(" and Factorydata like ?");
        }
        if (StrKit.notBlank(Incomingdata)) {
            from.append(" and Incomingdata like ?");
        }
        if (StrKit.notBlank(QualityCertificate)) {
            from.append(" and QualityCertificate like ?");
        }
        if (StrKit.notBlank(gridno)) {
            from.append(" and gridno like ?");
        }
        from.append(" order by id desc");

        // 准备参数
        List<Object> params = new ArrayList<>();
        if (StrKit.notBlank(RawmaterialManufacturer)) {
            params.add("%" + RawmaterialManufacturer + "%");
        }
        if (StrKit.notBlank(Size)) {
            params.add("%" + Size + "%");
        }
        if (StrKit.notBlank(IncomingNo)) {
            params.add("%" + IncomingNo + "%");
        }
        if (StrKit.notBlank(SinglefilamentStrength)) {
            params.add("%" + SinglefilamentStrength + "%");
        }

        if (StrKit.notBlank(Factorydata)) {
            params.add("%" + Factorydata + "%");
        }
        if (StrKit.notBlank(Incomingdata)) {
            params.add("%" + Incomingdata + "%");
        }
        if (StrKit.notBlank(QualityCertificate)) {
            params.add("%" + QualityCertificate + "%");
        }
        if (StrKit.notBlank(gridno)) {
            params.add("%" + gridno + "%");
        }

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public ACSR findById(int id) {
        return dao.findById(id);
    }

    public boolean save(ACSR acsr) {
        return acsr.save();
    }

    public boolean update(ACSR acsr) {return acsr.update();}

    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }


    public Page<Record> getGridNoList(int pageNumber, int pageSize, String gridno) {
        // 构建SQL查询语句
        StringBuilder select = new StringBuilder("SELECT gridno ");
        StringBuilder from = new StringBuilder("FROM bascontract ");
        List<Object> params = new ArrayList<>();

        // 添加查询条件（如果有合同号条件）
//        if (gridNo != null && !gridNo.trim().isEmpty()) {
//            from.append("WHERE id LIKE ? ");
//            params.add("%" + gridNo.trim() + "%");
//        }
        if (gridno != null && !gridno.trim().isEmpty()) {
            from.append("WHERE gridno LIKE ? ");
            params.add("%" + gridno.trim() + "%");
        }

        // 添加排序（根据需要调整）
        from.append("ORDER BY gridno ASC");

        // 执行分页查询
        return Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), params.toArray());
    }
}
