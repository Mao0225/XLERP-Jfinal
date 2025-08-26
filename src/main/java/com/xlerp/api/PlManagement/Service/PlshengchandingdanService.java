package com.xlerp.api.PlManagement.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.Pldingdanitem;
import com.xlerp.common.model.Plshengchandingdan;

import java.util.*;
import java.util.stream.Collectors;

public class PlshengchandingdanService {
    private static final Plshengchandingdan dao = new Plshengchandingdan();

    public Page<Record> paginate(int pageNumber, int pageSize, String ipoNo ,String contractNo) {
        String select = "select ssdd.*,c.name as contractName";
        StringBuilder from = new StringBuilder("from plshengchandingdan ssdd " +
                "left join bascontract c on ssdd.contractNo = c.no " +
                "where ssdd.isdelete = 0");

// 动态构建查询条件
        if (StrKit.notBlank(ipoNo))
            from.append(" and ipoNo like ?");
        if (StrKit.notBlank(contractNo))
            from.append(" and ssdd.contractNo like ?");

        from.append(" order by id desc");

// 准备参数
        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(ipoNo)) {
            params.add("%" + ipoNo + "%");
        }
        if (StrKit.notBlank(contractNo)) {
            params.add("%" + contractNo + "%");
        }


        return Db.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    //根据登录用户的部门编号查询生产订单
    public Page<Record> paginateBydep(int pageNumber, int pageSize, String ipoNo, String contractNo, String depNo) {
        String select = "SELECT ssdd.*, c.name AS contractName";
        StringBuilder from = new StringBuilder("FROM plshengchandingdan ssdd " +
                "LEFT JOIN bascontract c ON ssdd.contractNo = c.no " +
                "WHERE ssdd.isdelete = 0");

        // 动态构建查询条件
        List<Object> params = new ArrayList<>();

        if (StrKit.notBlank(ipoNo)) {
            from.append(" AND ssdd.ipoNo LIKE ?");
            params.add("%" + ipoNo + "%");
        }
        if (StrKit.notBlank(contractNo)) {
            from.append(" AND ssdd.contractNo LIKE ?");
            params.add("%" + contractNo + "%");
        }
        if (StrKit.notBlank(depNo)) {
            from.append(" AND ssdd.ipoNo IN (SELECT di.ipoNo FROM pldingdanitem di WHERE di.workshopName LIKE ?)");
            params.add("%" + depNo + "%");
        }

        from.append(" ORDER BY ssdd.id DESC");

        return Db.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public List<Record> getDingdanItemByNoAndDepNo(String ipoNo, String depNo) {
        StringBuilder sql = new StringBuilder("SELECT * FROM pldingdanitem WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (StrKit.notBlank(ipoNo)) {
            sql.append(" AND ipoNo = ?");
            params.add(ipoNo);
        }
        if (StrKit.notBlank(depNo)) {
            sql.append(" AND workshopName LIKE ?");
            params.add("%" + depNo + "%");
        }

        // 打印完整SQL语句（带参数值）
        String fullSql = buildFullSql(sql.toString(), params);
        System.out.println("完整SQL语句: " + fullSql);

        return Db.find(sql.toString(), params.toArray());
    }

    // 构建完整的SQL语句（将参数值替换到占位符中）
    private String buildFullSql(String sql, List<Object> params) {
        String fullSql = sql;
        for (Object param : params) {
            // 处理字符串类型的参数
            if (param instanceof String) {
                fullSql = fullSql.replaceFirst("\\?", "'" + param + "'");
            } else {
                fullSql = fullSql.replaceFirst("\\?", param.toString());
            }
        }
        return fullSql;
    }

    public Plshengchandingdan findById(int id) {
        return dao.findFirst("SELECT ssdd.*, c.name as contractName " +
                "FROM plshengchandingdan ssdd " +
                "LEFT JOIN bascontract c ON ssdd.contractNo = c.no " +
                "WHERE ssdd.id = ? AND ssdd.isdelete = 0", id);
    }

    public boolean save(Plshengchandingdan plshengchandingdan) {
        return plshengchandingdan.save();
    }

    public boolean update(Plshengchandingdan plshengchandingdan) {
        return plshengchandingdan.update();
    }

    public boolean logicalDeleteById(int id) {
        return Db.update("update plshengchandingdan set isdelete = 1 where id = ? and isdelete = 0", id) > 0;
    }

    public boolean batchLogicalDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "update plshengchandingdan set isdelete = 1 where id in (" + placeholders + ") and isdelete = 0";
        return Db.update(sql, ids.toArray()) > 0;
    }


    private static final Pldingdanitem itemDao = new Pldingdanitem();

    public List<Record> getDingdanItemByNo(String ipoNo) {
        return Db.find("select * from pldingdanitem where ipoNo = ?", ipoNo);
    }

    public boolean saveDingdanItem(Pldingdanitem item) {
        return item.save();
    }

    public boolean updateDingdanItem(Pldingdanitem item) {
        return item.update();
    }

    public boolean deleteDingdanItem(int id) {
        return itemDao.deleteById( id);
    }

    public List<Record> getItemCount(String itemId) {
        // 1. 执行原始查询
        List<Record> records = executeQuery(itemId);

        if (records.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 处理顶层数据（从第一条记录提取基础信息）
        Map<String, Object> data = new HashMap<>();
        Record first = records.get(0);
        data.put("itemNo", first.getStr("itemNo"));
        data.put("itemName", first.getStr("itemName"));
        data.put("spec", first.getStr("spec"));
        data.put("totalAmount", first.getBigDecimal("totalAmount"));
        data.put("allocatedOrderAmount", first.getBigDecimal("allocatedOrderAmount"));

        // 3. 按订单号分组处理（核心修改：处理 null 值的 ipoNo）
        Map<String, List<Record>> orderGroups = records.stream()
                .collect(Collectors.groupingBy(r -> {
                    String ipoNo = r.getStr("ipoNo");
                    // 关键修复：将 null 转换为特定字符串（如"无订单号"），避免分组键为 null
                    return ipoNo == null ? "无订单号" : ipoNo;
                }));

        List<Map<String, Object>> orders = new ArrayList<>();
        for (Map.Entry<String, List<Record>> entry : orderGroups.entrySet()) {
            String ipoNo = entry.getKey();
            List<Record> orderRecords = entry.getValue();

            // 订单级数据（从分组的第一条记录提取订单信息）
            Map<String, Object> order = new HashMap<>();
            Record orderFirst = orderRecords.get(0);
            order.put("ipoNo", ipoNo);
            // 处理订单数量可能为 null 的情况
            order.put("orderAmount", orderFirst.getStr("orderAmount") != null ?
                    orderFirst.getStr("orderAmount") : "0");
            // 处理剩余数量可能为 null 的情况
            order.put("remainingInOrder", orderFirst.getBigDecimal("remainingInOrder") != null ?
                    orderFirst.getBigDecimal("remainingInOrder") : 0);
            // 处理车间名称可能为 null 的情况
            order.put("workshopName", orderFirst.getStr("workshopName") != null ?
                    orderFirst.getStr("workshopName") : "");

            // 处理工单列表（过滤 null 和空字符串的 woNo）
            List<Map<String, Object>> workorders = orderRecords.stream()
                    .filter(r -> r.getStr("woNo") != null && !r.getStr("woNo").trim().isEmpty())
                    .map(r -> {
                        Map<String, Object> wo = new HashMap<>();
                        wo.put("woNo", r.getStr("woNo"));
                        // 处理工单数量可能为 null 的情况
                        wo.put("workorderAmount", r.getStr("workorderAmount") != null ?
                                r.getStr("workorderAmount") : "0");
                        return wo;
                    })
                    .distinct() // 去重避免重复工单
                    .collect(Collectors.toList());

            order.put("workorders", workorders);
            orders.add(order);
        }

        data.put("orders", orders);

        // 4. 将处理好的 Map 转换为 Record
        Record resultRecord = new Record();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            resultRecord.set(entry.getKey(), entry.getValue());
        }

        return Collections.singletonList(resultRecord);
    }

    // 执行查询的方法（保持不变）
    private List<Record> executeQuery(String itemId) {
        String sql = "SELECT " +
                "    b.no AS itemNo, " +
                "    b.name AS itemName, " +
                "    b.spec, " +
                "    bi.itemnum AS totalAmount, " +
                "    di.ipoNo, " +
                "    di.amount AS orderAmount, " +
                "    gi.woNo, " +
                "    gi.amount AS workorderAmount, " +
                "    di.workshopName, " +
                "    (SELECT COALESCE(SUM(d.amount), 0) " +
                "     FROM pldingdanitem d " +
                "     WHERE d.conitemId = bi.id) AS allocatedOrderAmount, " +
                "    di.amount - (SELECT COALESCE(SUM(g.amount), 0) " +
                "                FROM plgongdanitem g " +
                "                WHERE g.dingdanitemId = di.id) AS remainingInOrder " +
                "FROM " +
                "    bascontractitem bi " +
                "    LEFT JOIN pldingdanitem di ON di.conitemId = bi.id " +
                "    LEFT JOIN plgongdanitem gi ON gi.dingdanitemId = di.id " +
                "    LEFT JOIN basitem b ON bi.itemid = b.id " +
                "WHERE " +
                "    bi.id = ? " +
                "ORDER BY " +
                "    di.ipoNo, gi.woNo";

        return Db.find(sql, itemId);
    }
}