package com.xlerp.api.ClManagement.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.ClNzxjGc;

import java.util.List;

/**
 * Service for ClNzxjGc 耐张线夹钢材数据采集表
 */
public class ClNzxjGcService {
    private static final ClNzxjGc dao = new ClNzxjGc();

    public Page<ClNzxjGc> paginate(
            int pageNumber,
            int pageSize,
            String mafactory,
            String batch,
            String orderno,
            String matMaterial,
            String matSpec,
            String matRecheckNo,
            String chemC,
            String chemSi,
            String chemMn,
            String chemP,
            String chemS,
            String chemCr,
            String chemNi,
            String chemMo,
            String leavefactoryDate,
            String detectionTime,
            String certificate,
            String contractNo,
            String woNo,
            String ipoNo,
            String writer,
            String writeTime,
            String flag,
            String status,
            String memo,
            String type
    ) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from cl_nzxj_gc where isdelete = 0");

        if (StrKit.notBlank(mafactory)) from.append(" and mafactory like ?");
        if (StrKit.notBlank(batch)) from.append(" and batch like ?");
        if (StrKit.notBlank(orderno)) from.append(" and orderno like ?");
        if (StrKit.notBlank(matMaterial)) from.append(" and matMaterial like ?");
        if (StrKit.notBlank(matSpec)) from.append(" and matSpec like ?");
        if (StrKit.notBlank(matRecheckNo)) from.append(" and matRecheckNo like ?");
        if (StrKit.notBlank(chemC)) from.append(" and chemC like ?");
        if (StrKit.notBlank(chemSi)) from.append(" and chemSi like ?");
        if (StrKit.notBlank(chemMn)) from.append(" and chemMn like ?");
        if (StrKit.notBlank(chemP)) from.append(" and chemP like ?");
        if (StrKit.notBlank(chemS)) from.append(" and chemS like ?");
        if (StrKit.notBlank(chemCr)) from.append(" and chemCr like ?");
        if (StrKit.notBlank(chemNi)) from.append(" and chemNi like ?");
        if (StrKit.notBlank(chemMo)) from.append(" and chemMo like ?");
        if (StrKit.notBlank(leavefactoryDate)) from.append(" and leavefactoryDate like ?");
        if (StrKit.notBlank(detectionTime)) from.append(" and detectionTime like ?");
        if (StrKit.notBlank(certificate)) from.append(" and certificate like ?");
        if (StrKit.notBlank(contractNo)) from.append(" and contractNo like ?");
        if (StrKit.notBlank(woNo)) from.append(" and woNo like ?");
        if (StrKit.notBlank(ipoNo)) from.append(" and ipoNo like ?");
        if (StrKit.notBlank(writer)) from.append(" and writer like ?");
        if (StrKit.notBlank(writeTime)) from.append(" and writeTime like ?");
        if (StrKit.notBlank(flag)) from.append(" and flag like ?");
        if (StrKit.notBlank(status)) from.append(" and status like ?");
        if (StrKit.notBlank(memo)) from.append(" and memo like ?");
        if (StrKit.notBlank(type)) from.append(" and type like ?");

        from.append(" order by id desc");

        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(mafactory)) params.add("%" + mafactory + "%");
        if (StrKit.notBlank(batch)) params.add("%" + batch + "%");
        if (StrKit.notBlank(orderno)) params.add("%" + orderno + "%");
        if (StrKit.notBlank(matMaterial)) params.add("%" + matMaterial + "%");
        if (StrKit.notBlank(matSpec)) params.add("%" + matSpec + "%");
        if (StrKit.notBlank(matRecheckNo)) params.add("%" + matRecheckNo + "%");
        if (StrKit.notBlank(chemC)) params.add("%" + chemC + "%");
        if (StrKit.notBlank(chemSi)) params.add("%" + chemSi + "%");
        if (StrKit.notBlank(chemMn)) params.add("%" + chemMn + "%");
        if (StrKit.notBlank(chemP)) params.add("%" + chemP + "%");
        if (StrKit.notBlank(chemS)) params.add("%" + chemS + "%");
        if (StrKit.notBlank(chemCr)) params.add("%" + chemCr + "%");
        if (StrKit.notBlank(chemNi)) params.add("%" + chemNi + "%");
        if (StrKit.notBlank(chemMo)) params.add("%" + chemMo + "%");
        if (StrKit.notBlank(leavefactoryDate)) params.add("%" + leavefactoryDate + "%");
        if (StrKit.notBlank(detectionTime)) params.add("%" + detectionTime + "%");
        if (StrKit.notBlank(certificate)) params.add("%" + certificate + "%");
        if (StrKit.notBlank(contractNo)) params.add("%" + contractNo + "%");
        if (StrKit.notBlank(woNo)) params.add("%" + woNo + "%");
        if (StrKit.notBlank(ipoNo)) params.add("%" + ipoNo + "%");
        if (StrKit.notBlank(writer)) params.add("%" + writer + "%");
        if (StrKit.notBlank(writeTime)) params.add("%" + writeTime + "%");
        if (StrKit.notBlank(flag)) params.add("%" + flag + "%");
        if (StrKit.notBlank(status)) params.add("%" + status + "%");
        if (StrKit.notBlank(memo)) params.add("%" + memo + "%");
        if (StrKit.notBlank(type)) params.add("%" + type + "%");

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public ClNzxjGc findById(int id) {
        return dao.findById(id);
    }

    public boolean save(ClNzxjGc clNzxjGc) {
        return clNzxjGc.save();
    }

    public boolean update(ClNzxjGc clNzxjGc) {
        return clNzxjGc.update();
    }

    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }
}