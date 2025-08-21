package com.xlerp.api.ClManagement.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.ClJxjjLc;

import java.util.ArrayList;
import java.util.List;

public class JxjjLcService {

    private static final ClJxjjLc dao = new ClJxjjLc();
    private static final String UPLOAD_DIR = "uploads/";

    public Page<ClJxjjLc> paginate(int pageNumber, int pageSize, String mafactory, String maQuality, String matMaterial, String orderno, String matRecheckNo, String tensileStrength, String elongation, String hardness, String leavefactoryDate, String detectionTime, String certificate, String contractNo, String woNo, String ipoNo, String writer, String writeTime) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from cl_jxjj_lc where isdelete = 0");

        // 动态构建查询条件
        if (StrKit.notBlank(mafactory)) {
            from.append(" and mafactory like ?");
        }
        if (StrKit.notBlank(maQuality)) {
            from.append(" and maQuality like ?");
        }
        if (StrKit.notBlank(matMaterial)) {
            from.append(" and matMaterial like ?");
        }
        if (StrKit.notBlank(orderno)) {
            from.append(" and orderno like ?");
        }
        if (StrKit.notBlank(matRecheckNo)) {
            from.append(" and matRecheckNo like ?");
        }

        if (StrKit.notBlank(tensileStrength)) {
            from.append(" and tensileStrength like ?");
        }
        if (StrKit.notBlank(elongation)) {
            from.append(" and elongation like ?");
        }
        if (StrKit.notBlank(hardness)) {
            from.append(" and hardness like ?");
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
        if (StrKit.notBlank(maQuality)) {
            params.add("%" + maQuality + "%");
        }
        if (StrKit.notBlank(matMaterial)) {
            params.add("%" + matMaterial + "%");
        }
        if (StrKit.notBlank(orderno)) {
            params.add("%" + orderno + "%");
        }
        if (StrKit.notBlank(matRecheckNo)) {
            params.add("%" + matRecheckNo + "%");
        }

        if (StrKit.notBlank(tensileStrength)) {
            params.add("%" + tensileStrength + "%");
        }
        if (StrKit.notBlank(elongation)) {
            params.add("%" + elongation + "%");
        }
        if (StrKit.notBlank(hardness)) {
            params.add("%" + hardness + "%");
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

    public ClJxjjLc findById(int id) {
        return dao.findById(id);
    }

    public boolean save(ClJxjjLc jxjjLc) {
        return jxjjLc.save();
    }

    public boolean update(ClJxjjLc jxjjLc) {
        return jxjjLc.update();
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
