package com.xlerp.api.ClManagement.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.ClNzxjLbgx;

import java.util.List;

/**
 * Service for ClNzxjLbgx 耐张线夹铝包钢线数据采集表
 */
public class ClNzxjLbgxService {
    private static final ClNzxjLbgx dao = new ClNzxjLbgx();

    public Page<ClNzxjLbgx> paginate(
            int pageNumber,
            int pageSize,
            String matRecheckNo,
            String orderno,
            String size,
            String singleWireStrength,
            String mafactory,
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
        StringBuilder from = new StringBuilder("from cl_nzxj_lbgx where isdelete = 0");

        if (StrKit.notBlank(matRecheckNo)) from.append(" and matRecheckNo like ?");
        if (StrKit.notBlank(orderno)) from.append(" and orderno like ?");
        if (StrKit.notBlank(size)) from.append(" and size like ?");
        if (StrKit.notBlank(singleWireStrength)) from.append(" and singleWireStrength like ?");
        if (StrKit.notBlank(mafactory)) from.append(" and mafactory like ?");
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
        if (StrKit.notBlank(matRecheckNo)) params.add("%" + matRecheckNo + "%");
        if (StrKit.notBlank(orderno)) params.add("%" + orderno + "%");
        if (StrKit.notBlank(size)) params.add("%" + size + "%");
        if (StrKit.notBlank(singleWireStrength)) params.add("%" + singleWireStrength + "%");
        if (StrKit.notBlank(mafactory)) params.add("%" + mafactory + "%");
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

    public ClNzxjLbgx findById(int id) {
        return dao.findById(id);
    }

    public boolean save(ClNzxjLbgx clNzxjLbgx) {
        return clNzxjLbgx.save();
    }

    public boolean update(ClNzxjLbgx clNzxjLbgx) {
        return clNzxjLbgx.update();
    }

    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }
}