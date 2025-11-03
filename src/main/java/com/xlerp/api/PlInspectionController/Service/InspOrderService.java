package com.xlerp.api.PlInspectionController.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.PlInspOrder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InspOrderService {
    private static final PlInspOrder dao = new PlInspOrder().dao();

    /**
     * 自动生成检验单号
     * 格式：INS + yyyyMMdd + 4位流水号（如 INS202511030001）
     * @return 生成的单号字符串
     */
    public String generateOrderNo() {
        String prefix = "INS" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        Long nextSeq = Db.queryLong(
                "select ifnull(max(cast(right(orderNo,4) as unsigned)), 0) + 1 " +
                        "from pl_insp_order where orderNo like ?", prefix + "%");
        return prefix + String.format("%04d", nextSeq);
    }

    /**
     * 更新检验单状态
     * @param id     检验单ID
     * @param status 新状态值（如：报检中、检验中、待入库）
     * @return 更新成功返回 true
     */
    public boolean updateStatus(long id, String status) {
        return Db.update("update pl_insp_order set status = ? where id = ?", status, id) > 0;
    }

    /**
     * 更新检验单状态并写入入库备注
     * @param id     检验单ID
     * @param status 新状态（如：已入库、拒绝入库）
     * @param remark 入库阶段备注
     * @return 更新成功返回 true
     */
    public boolean updateStatusAndRemark(long id, String status, String remark) {
        String sql = "update pl_insp_order set status = ?, stockRemark = ? where id = ?";
        return Db.update(sql, status, remark, id) > 0;
    }

    /**
     * 分页查询检验单列表（可扩展）
     */
    public Page<PlInspOrder> paginate(int pageNumber, int pageSize, String param) {
        // 实现同 InspItemService.paginate
        return null;
    }
}