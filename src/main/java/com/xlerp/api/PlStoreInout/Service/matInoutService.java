package com.xlerp.api.PlStoreInout.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.PlMatInoutDoc;
import com.xlerp.common.model.PlMatInoutItem;

import java.util.ArrayList;
import java.util.List;

public class matInoutService {
    private static final PlMatInoutDoc dao = new PlMatInoutDoc();
    private static final PlMatInoutItem itemDao = new PlMatInoutItem();

    public Page<PlMatInoutDoc> paginate(int pageNumber, int pageSize,
                                         String status, String inOutType) {
        // 构建查询字段
        String select = "select m.*";

        // 构建FROM子句和基础WHERE条件
        StringBuilder from = new StringBuilder("from pl_mat_inout_doc m ");
        from.append("where isDeleted = 0 "); // 基础条件，简化后续拼接

        // 构建查询参数
        List<Object> params = new ArrayList<>();


        if (status != null && !status.isEmpty()) {
            from.append("and m.status >= ? ");
            params.add(status);
        }

        if (inOutType != null && !inOutType.isEmpty()) {
            from.append("and m.inOutType = ? ");
            params.add(inOutType);
        }
        // 添加排序
        from.append("order by m.id desc");

        // 执行分页查询
        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public PlMatInoutDoc findById(int id) {
        return dao.findFirst("select * from pl_mat_inout_doc where id = ? ", id);
    }

    public boolean save(PlMatInoutDoc pl_mat_inout_doc) {
        return pl_mat_inout_doc.save();
    }

    public boolean update(PlMatInoutDoc pl_mat_inout_doc) {
        return pl_mat_inout_doc.update();
    }

    public boolean LogicDeleteById(int id) {
        return Db.update("update pl_mat_inout_doc set isDeleted = 1 where id = ? ", id)>0;
    }

    public boolean updateStatus(String id, String status) {
        return Db.update("update pl_mat_inout_doc set status = ? where id = ? ", status, id) > 0;
    }




    // ===== 明细表方法 =====
    public PlMatInoutItem findItemById(int id) {
        return itemDao.findById( id);
    }

    public boolean saveItem(PlMatInoutItem item) {
        return item.save();
    }

    public boolean updateItem(PlMatInoutItem item) {
        return item.update();
    }

    public boolean deleteItemById(int id) {
        return itemDao.deleteById(id);
    }

    public List<PlMatInoutItem> findItemsByDocNo(String docNo) {
        return itemDao.find("select * from pl_mat_inout_item where docNo=?", docNo);
    }

    public Page<PlMatInoutItem> itemPaginate(int pageNumber, int pageSize, String docNo,String materialCode,String materialName,String materialSpec,String inOutType,String status) {
        // 选择需要的字段（避免使用*）
        String select = "select m.*, d.inOutType, d.deliveryOrg, d.term, d.handler,d.status as docStatus";

        // 构建 FROM 和 WHERE 子句
        StringBuilder from = new StringBuilder("from pl_mat_inout_item m ");
        from.append("right join pl_mat_inout_doc d on d.docNo = m.docNo ");
        from.append("where 1 = 1 ");

        // 构建参数列表
        List<Object> params = new ArrayList<>();

        if (docNo != null && !docNo.trim().isEmpty()) {
            from.append("and m.docNo = ? ");
            params.add(docNo);
        }
        if (materialCode != null && !materialCode.trim().isEmpty()) {
            from.append("and m.materialCode = ? ");
            params.add(materialCode);
        }
        if (materialName != null && !materialName.trim().isEmpty()) {
            from.append("and m.materialName like ? ");
            params.add("%" + materialName + "%");
        }
        if (materialSpec != null && !materialSpec.trim().isEmpty()) {
            from.append("and m.materialSpec = ? ");
            params.add(materialSpec);
        }
        if (inOutType != null && !inOutType.trim().isEmpty()) {
            from.append("and d.inOutType = ? ");
            params.add(inOutType);
        }
        if (status != null && !status.trim().isEmpty()) {
            from.append("and d.status = ? ");
            params.add(status);
        }

        // 关键改进：先按docNo排序（相同的放一起），再按id排序
        from.append("order by m.docNo, m.id desc");

        // 执行分页查询
        return itemDao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

}