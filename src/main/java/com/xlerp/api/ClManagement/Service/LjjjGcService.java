package com.xlerp.api.ClManagement.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.ClLjjjGc;

import java.util.ArrayList;
import java.util.List;

public class LjjjGcService {

    private static final ClLjjjGc dao = new ClLjjjGc();
    private static final String UPLOAD_DIR = "uploads/";

    public Page<ClLjjjGc> paginate(int pageNumber, int pageSize, String mafactory, String matMaterial, String orderno, String matSpec, String batch, String recheckBatchNo, String tensileStrength, String yieldStrength, String elongation, String leavefactoryDate, String detectionTime, String certificate, String contractNo, String woNo, String ipoNo, String writer, String writeTime) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from cl_ljjj_gc where isdelete = 0");

        // 动态构建查询条件
        if (StrKit.notBlank(mafactory)) {
            from.append(" and mafactory like ?");
        }
        if (StrKit.notBlank(matMaterial)) {
            from.append(" and matMaterial like ?");
        }
        if (StrKit.notBlank(orderno)) {
            from.append(" and orderno like ?");
        }
        if (StrKit.notBlank(matSpec)) {
            from.append(" and matSpec like ?");
        }
        if (StrKit.notBlank(batch)) {
            from.append(" and batch like ?");
        }
        if (StrKit.notBlank(recheckBatchNo)) {
            from.append(" and recheckBatchNo like ?");
        }


        if (StrKit.notBlank(tensileStrength)) {
            from.append(" and tensileStrength like ?");
        }
        if (StrKit.notBlank(yieldStrength)) {
            from.append(" and yieldStrength like ?");
        }
        if (StrKit.notBlank(elongation)) {
            from.append(" and elongation like ?");
        }
        if (StrKit.notBlank(leavefactoryDate)){
            from.append(" and leavefactoryDate like ?");
        }
        if (StrKit.notBlank(detectionTime)){
            from.append(" and detectionTime like ?");
        }
        if (StrKit.notBlank(certificate)){
            from.append(" and certificate like ?");
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
        if (StrKit.notBlank(writeTime)){
            from.append(" and writeTime like ?");
        }
        from.append(" order by id desc");

        // 准备参数
        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(mafactory)) {
            params.add("%" + mafactory + "%");
        }
        if (StrKit.notBlank(matMaterial)) {
            params.add("%" + matMaterial + "%");
        }
        if (StrKit.notBlank(orderno)) {
            params.add("%" + orderno + "%");
        }
        if (StrKit.notBlank(matSpec)) {
            params.add("%" + matSpec + "%");
        }
        if (StrKit.notBlank(batch)) {
            params.add("%" + batch + "%");
        }
        if (StrKit.notBlank(recheckBatchNo)) {
            params.add("%" + recheckBatchNo + "%");
        }


        if (StrKit.notBlank(tensileStrength)) {
            params.add("%" + tensileStrength + "%");
        }
        if (StrKit.notBlank(yieldStrength)) {
            params.add("%" + yieldStrength + "%");
        }
        if (StrKit.notBlank(elongation)) {
            params.add("%" + elongation + "%");
        }
        if (StrKit.notBlank(leavefactoryDate)){
            params.add("%" + leavefactoryDate + "%");
        }
        if (StrKit.notBlank(detectionTime)){
            params.add("%" + detectionTime + "%");
        }
        if (StrKit.notBlank(certificate)){
            params.add("%" + certificate + "%");
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
        if (StrKit.notBlank(writeTime)){
            params.add("%" + writeTime + "%");
        }


        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public ClLjjjGc findById(int id) {
        return dao.findById(id);
    }

    public boolean save(ClLjjjGc ljjjGc) {
        return ljjjGc.save();
    }

    public boolean update(ClLjjjGc ljjjGc) {
        return ljjjGc.update();
    }

    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }

    public Page<Record> getWoNoList(int pageNumber, int pageSize, String woNo, String ipoNo, String contractNo) {
        // 构建SQL查询语句
        StringBuilder select = new StringBuilder("SELECT woNo, ipoNo, contractNo ");
        StringBuilder from = new StringBuilder("FROM plshengchangongdan ");
        List<Object> params = new ArrayList<>();

        // 添加查询条件（如果有合同号条件）
        if (woNo != null && !woNo.trim().isEmpty()) {
            from.append("WHERE woNo LIKE ? ");
            params.add("%" + woNo.trim() + "%");
        }

        if (ipoNo != null && !ipoNo.trim().isEmpty()) {
            from.append("WHERE ipoNo LIKE ? ");
            params.add("%" + ipoNo.trim() + "%");
        }
        if (contractNo != null && !contractNo.trim().isEmpty()) {
            from.append("WHERE contractNo LIKE ? ");
            params.add("%" + contractNo.trim() + "%");
        }

        // 添加排序（根据需要调整）
        from.append("ORDER BY woNo ASC");

        // 执行分页查询
        return Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), params.toArray());
    }
}
