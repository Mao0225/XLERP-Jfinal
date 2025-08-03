package com.xlerp.api.ClManagement.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.ClProductionData;

import java.util.List;

public class ClProductionDataService {
    private static final ClProductionData dao = new ClProductionData();

    public Page<ClProductionData> paginate(int pageNumber, int pageSize, String productname, String productmodel, String productionbatch, String processingmethod, Integer processingquantity, String productioncompletiontime, String schedulingplanno, String contractNo, String woNo, String ipoNo, String writer, String writeTime, Integer isdelete, String status, String flag, String type, String memo) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from cl_production_data where isdelete = 0");

        // 动态构建查询条件
        if (StrKit.notBlank(productname)) {
            from.append(" and productname like ?");
        }
        if (StrKit.notBlank(productmodel)) {
            from.append(" and productmodel like ?");
        }
        if (StrKit.notBlank(productionbatch)) {
            from.append(" and productionbatch like ?");
        }
        if (StrKit.notBlank(processingmethod)) {
            from.append(" and processingmethod like ?");
        }
        if (processingquantity != null) {
            from.append(" and processingquantity = ?");
        }
        if (StrKit.notBlank(productioncompletiontime)) {
            from.append(" and productioncompletiontime like ?");
        }
        if (StrKit.notBlank(schedulingplanno)) {
            from.append(" and schedulingplanno like ?");
        }
        if (StrKit.notBlank(contractNo)) {
            from.append(" and contractNo like ?");
        }
        if (StrKit.notBlank(woNo)) {
            from.append(" and woNo like ?");
        }
        if (StrKit.notBlank(ipoNo)) {
            from.append(" and ipoNo like ?");
        }
        if (StrKit.notBlank(writer)) {
            from.append(" and writer like ?");
        }
        if (StrKit.notBlank(writeTime)) {
            from.append(" and writeTime like ?");
        }
        if (isdelete != null) {
            from.append(" and isdelete = ?");
        }
        if (StrKit.notBlank(status)) {
            from.append(" and status like ?");
        }
        if (StrKit.notBlank(flag)) {
            from.append(" and flag like ?");
        }
        if (StrKit.notBlank(type)) {
            from.append(" and type like ?");
        }
        if (StrKit.notBlank(memo)) {
            from.append(" and memo like ?");
        }
        from.append(" order by id desc");

        // 准备参数
        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(productname)) {
            params.add("%" + productname + "%");
        }
        if (StrKit.notBlank(productmodel)) {
            params.add("%" + productmodel + "%");
        }
        if (StrKit.notBlank(productionbatch)) {
            params.add("%" + productionbatch + "%");
        }
        if (StrKit.notBlank(processingmethod)) {
            params.add("%" + processingmethod + "%");
        }
        if (processingquantity != null) {
            params.add(processingquantity);
        }
        if (StrKit.notBlank(productioncompletiontime)) {
            params.add("%" + productioncompletiontime + "%");
        }
        if (StrKit.notBlank(schedulingplanno)) {
            params.add("%" + schedulingplanno + "%");
        }
        if (StrKit.notBlank(contractNo)) {
            params.add("%" + contractNo + "%");
        }
        if (StrKit.notBlank(woNo)) {
            params.add("%" + woNo + "%");
        }
        if (StrKit.notBlank(ipoNo)) {
            params.add("%" + ipoNo + "%");
        }
        if (StrKit.notBlank(writer)) {
            params.add("%" + writer + "%");
        }
        if (StrKit.notBlank(writeTime)) {
            params.add("%" + writeTime + "%");
        }
        if (isdelete != null) {
            params.add(isdelete);
        }
        if (StrKit.notBlank(status)) {
            params.add("%" + status + "%");
        }
        if (StrKit.notBlank(flag)) {
            params.add("%" + flag + "%");
        }
        if (StrKit.notBlank(type)) {
            params.add("%" + type + "%");
        }
        if (StrKit.notBlank(memo)) {
            params.add("%" + memo + "%");
        }
        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public ClProductionData findById(int id) {
        return dao.findById(id);
    }

    public boolean save(ClProductionData clProductionData) {
        return clProductionData.save();
    }

    public boolean update(ClProductionData clProductionData) {
        return clProductionData.update();
    }

    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }
}