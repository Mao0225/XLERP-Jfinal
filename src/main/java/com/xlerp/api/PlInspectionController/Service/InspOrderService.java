package com.xlerp.api.PlInspectionController.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.PlInspOrder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InspOrderService {
    private static final PlInspOrder dao = new PlInspOrder().dao();


    /**
     * 分页查询检验单列表（可扩展）
     */
    public Page<PlInspOrder> paginate(int pageNumber, int pageSize, String param, String status) {
        String select = "select p.*,bi.no as itemCode,bi.name as itemName,bi.spec as itemSpec ";
        // 核心：先加 where 1=1，后续条件统一用 and 拼接
        StringBuilder from = new StringBuilder("from pl_insp_order p " +
                "left join basitem bi on bi.id = p.itemId where 1=1 ");
        List<Object> params = new ArrayList<>();

        // 只判断一次param，有值就拼接两个 and 条件
        if (StrKit.notBlank(param)) {
            from.append("and orderNo like ? "); // 统一用 and
            params.add("%" + param + "%");
        }
        // 只判断一次param，有值就拼接两个 and 条件
        if (StrKit.notBlank(status)) {
            from.append("and p.status >= ? "); // 统一用 and
            params.add(status);
        }

        from.append("order by id desc");
        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }


    public boolean deleteById(long id) {
        return dao.deleteById(id);
    }

    /**
     * 更新检验单状态 + 自动填充审核人/时间/备注
     *
     * @param id        检验单ID
     * @param newStatus 新状态值 (10,11,12,20,21,22,23,30,31,32,99)
     * @param operator  当前操作人
     * @param remark    备注（拒绝原因等，可为空）
     * @return 是否更新成功
     */
    public boolean updateStatusAndRemark(long id, String newStatus, String operator, String remark) {
        // 1. 查询原记录
        Record order = Db.findById("pl_insp_order", id);
        if (order == null) {
            return false;
        }

        String oldStatus = order.getStr("status");

        // 2. 准备更新字段
        Record update = new Record();
        update.set("id", id);
        update.set("status", newStatus);
        // 3. 根据新状态，自动填充审核人 + 时间 + 备注字段
        switch (newStatus) {
            // === 报检审核 ===
            case "10"://提交报检
                update.set("reportReviewer", operator);
                update.set("reportReviewTime", Timestamp.valueOf(LocalDateTime.now()));
                break;
            case "11": // 报检通过
                update.set("reportReviewer", operator);
                break;
            case "12": // 报检拒绝
                update.set("reportReviewer", operator);
                update.set("remark", remark); // 拒绝原因放整单备注
                break;

            // === 检验阶段 ===
            case "20": // 开始检验
                update.set("inspector", operator);
                update.set("inspectTime", Timestamp.valueOf(LocalDateTime.now()));
                break;
            case "21": // 检验完成
                // 可选：记录完成时间
                update.set("inspectFinishTime", Timestamp.valueOf(LocalDateTime.now()));
                break;
            case "22": // 检验合格
                update.set("inspectReviewer", operator);
                break;
            case "23": // 检验不合格
                update.set("inspectReviewer", operator);
                update.set("inspRemark", remark); // 检验不合格原因
                break;

            // === 入库阶段 ===
            case "30": // 入库中
                // 可由其他流程触发
                break;
            case "31": // 入库完成
                update.set("storageReviewer", operator);
                update.set("inStockTime", Timestamp.valueOf(LocalDateTime.now()));
                break;
            case "32": // 入库拒绝
                update.set("storageReviewer", operator);
                update.set("stockRemark", remark); // 入库拒绝原因
                break;

            case "99": // 作废
                update.set("remark", "作废原因: " + (remark != null ? remark : "无"));
                break;

            default:
                return false; // 不支持的状态
        }

        // 4. 执行更新
        boolean success = Db.update("pl_insp_order", update);

        // 5. （可选）后续可插入日志表
        // if (success) insertLog(order, oldStatus, newStatus, operator, remark);

        return success;
    }

    public PlInspOrder findById(int i) {
        return dao.findById(i);
    }
}