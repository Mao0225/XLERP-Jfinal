package com.xlerp.api.Inspection.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.Bgxjlc;

import java.util.List;

public class BgxjlcService {
    private static final Bgxjlc dao = new Bgxjlc();

    public Page<Bgxjlc> paginate(int pageNumber, int pageSize, String contractNo, String supplier, String paiId, String inspectionNo,
                                   String Al, String Si, String Fe, String Cu, String Mg, String Mn, String Zn, String Ti, String Cr,
                                   String lstrength, String elongation, String outDate, String inDate, String certificate) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from bgxjlc where isdelete = 0");

        if (StrKit.notBlank(contractNo)) {
            from.append(" and contractNo like ?");
        }
        if (StrKit.notBlank(supplier)) {
            from.append(" and supplier like ?");
        }
        if (StrKit.notBlank(paiId)) {
            from.append(" and paiId like ?");
        }
        if (StrKit.notBlank(inspectionNo)) {
            from.append(" and inspectionNo like ?");
        }
        if (StrKit.notBlank(Al)) {
            from.append(" and Al like ?");
        }
        if (StrKit.notBlank(Si)) {
            from.append(" and Si like ?");
        }
        if (StrKit.notBlank(Fe)) {
            from.append(" and Fe like ?");
        }
        if (StrKit.notBlank(Cu)) {
            from.append(" and Cu like ?");
        }
        if (StrKit.notBlank(Mg)) {
            from.append(" and Mg like ?");
        }
        if (StrKit.notBlank(Mn)) {
            from.append(" and Mn like ?");
        }
        if (StrKit.notBlank(Zn)) {
            from.append(" and Zn like ?");
        }
        if (StrKit.notBlank(Ti)) {
            from.append(" and Ti like ?");
        }
        if (StrKit.notBlank(Cr)) {
            from.append(" and Cr like ?");
        }
        if (StrKit.notBlank(lstrength)) {
            from.append(" and lstrength like ?");
        }
        if (StrKit.notBlank(elongation)) {
            from.append(" and elongation like ?");
        }
        if (StrKit.notBlank(outDate)) {
            from.append(" and outDate like ?");
        }
        if (StrKit.notBlank(inDate)) {
            from.append(" and inDate like ?");
        }
        if (StrKit.notBlank(certificate)) {
            from.append(" and certificate like ?");
        }
        from.append(" order by id desc");

        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(contractNo)) {
            params.add("%" + contractNo + "%");
        }
        if (StrKit.notBlank(supplier)) {
            params.add("%" + supplier + "%");
        }
        if (StrKit.notBlank(paiId)) {
            params.add("%" + paiId + "%");
        }
        if (StrKit.notBlank(inspectionNo)) {
            params.add("%" + inspectionNo + "%");
        }
        if (StrKit.notBlank(Al)) {
            params.add("%" + Al + "%");
        }
        if (StrKit.notBlank(Si)) {
            params.add("%" + Si + "%");
        }
        if (StrKit.notBlank(Fe)) {
            params.add("%" + Fe + "%");
        }
        if (StrKit.notBlank(Cu)) {
            params.add("%" + Cu + "%");
        }
        if (StrKit.notBlank(Mg)) {
            params.add("%" + Mg + "%");
        }
        if (StrKit.notBlank(Mn)) {
            params.add("%" + Mn + "%");
        }
        if (StrKit.notBlank(Zn)) {
            params.add("%" + Zn + "%");
        }
        if (StrKit.notBlank(Ti)) {
            params.add("%" + Ti + "%");
        }
        if (StrKit.notBlank(Cr)) {
            params.add("%" + Cr + "%");
        }
        if (StrKit.notBlank(lstrength)) {
            params.add("%" + lstrength + "%");
        }
        if (StrKit.notBlank(elongation)) {
            params.add("%" + elongation + "%");
        }
        if (StrKit.notBlank(outDate)) {
            params.add("%" + outDate + "%");
        }
        if (StrKit.notBlank(inDate)) {
            params.add("%" + inDate + "%");
        }
        if (StrKit.notBlank(certificate)) {
            params.add("%" + certificate + "%");
        }

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public Bgxjlc findById(int id) {
        return dao.findById(id);
    }

    public boolean save(Bgxjlc bgxjlc) {
        return bgxjlc.save();
    }

    public boolean update(Bgxjlc bgxjlc) {
        return bgxjlc.update();
    }

    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }
}