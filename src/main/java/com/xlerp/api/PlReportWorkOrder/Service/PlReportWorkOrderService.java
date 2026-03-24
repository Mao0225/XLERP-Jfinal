package com.xlerp.api.PlReportWorkOrder.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.PlReporderMaterial;
import com.xlerp.common.model.PlReportWorkOrder;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlReportWorkOrderService {
    private static final PlReportWorkOrder dao = new PlReportWorkOrder();

    public Page<PlReportWorkOrder> paginate(int pageNumber, int pageSize,
                                            String contractNo, String contractName, String reportNo,
                                            String status) {
        String select = "select p.*,bc.no as contractNo,bc.name as contractName,bci.itemnum as contractAmount, bi.name as itemName," +
                " bi.spec as itemSpec,bci.itemunit as itemUnit,pwo.amount as woAmount,pwo.materialsCode as itemCode";

        StringBuilder from = new StringBuilder("from pl_report_work_order p ");
        from.append("left join pl_production_order ppo on ppo.ipoNo = p.ipoNo ");
        from.append("left join pl_work_order pwo on pwo.woNo = p.woNo ");
        from.append("left join bascontractitem bci on ppo.poItemId = bci.id ");
        from.append("left join bascontract bc on bc.no = bci.no ");
        from.append("left join basitem bi on bci.itemid = bi.id ");
        from.append("where 1 = 1 ");

        List<Object> params = new ArrayList<>();

        if (contractNo != null && !contractNo.isEmpty()) {
            from.append("and bci.no like ? ");
            params.add("%" + contractNo + "%");
        }

        if (contractName != null && !contractName.isEmpty()) {
            from.append("and bc.name like ? ");
            params.add("%" + contractName + "%");
        }

        if (reportNo != null && !reportNo.isEmpty()) {
            from.append("and p.reportNo like ? ");
            params.add("%" + reportNo + "%");
        }

        if (status != null && !status.isEmpty()) {
            from.append("and p.status = ? ");
            params.add(status);
        }

        from.append("order by p.id desc");

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public PlReportWorkOrder findById(int id) {
        return dao.findFirst("select * from pl_report_work_order where id = ? ", id);
    }

    public Map<String, Object> saveWithMaterial(String jsonData) {
        if (StrKit.isBlank(jsonData)) {
            throw new IllegalArgumentException("Request body is required");
        }

        JSONObject jsonObject = JSON.parseObject(jsonData);
        if (jsonObject == null || jsonObject.isEmpty()) {
            throw new IllegalArgumentException("Report data is required");
        }

        JSONObject mainData = extractMainData(jsonObject);
        if (mainData.isEmpty()) {
            throw new IllegalArgumentException("Main report data is required");
        }

        PlReportWorkOrder order = buildReportWorkOrder(mainData);
        String processCode = mainData.getString("processCode");
        Integer reportUserId = getIntegerValue(mainData.get("reportId"));
        if (reportUserId == null) {
            reportUserId = order.getReporterId();
        }

        return doSaveWithMaterial(
                order,
                processCode,
                reportUserId,
                mainData.getString("writer"),
                extractSelectmatArray(jsonObject, mainData)
        );
    }

    public Map<String, Object> saveWithMaterial(PlReportWorkOrder plReportWorkOrder, String reportId, String selectmatJson) {
        if (plReportWorkOrder == null) {
            throw new IllegalArgumentException("Report data is required");
        }

        Integer reportUserId = getIntegerValue(reportId);
        if (reportUserId != null) {
            plReportWorkOrder.set("reportId", reportUserId);
        } else {
            reportUserId = plReportWorkOrder.getReporterId();
        }

        return doSaveWithMaterial(
                plReportWorkOrder,
                plReportWorkOrder.getProcessCode(),
                reportUserId,
                plReportWorkOrder.getWriter(),
                parseSelectmatArray(selectmatJson)
        );
    }

    public boolean save(PlReportWorkOrder plReportWorkOrder) {
        return plReportWorkOrder.save();
    }

    public boolean update(PlReportWorkOrder plReportWorkOrder) {
        return plReportWorkOrder.update();
    }

    public boolean DeleteById(int id) {
        return dao.deleteById(id);
    }

    public boolean batchDelete(List<Integer> ids) {
        return dao.deleteByIds(ids);
    }

    public boolean updateStatus(String id, String status) {
        return Db.update("update pl_report_work_order set status = ? where id = ? ", status, id) > 0;
    }

    public List<Record> findBywoNo(String woNo, String processCode, Integer proccessType) {
        if (processCode != null && !processCode.isEmpty() && proccessType == 1) {
            return Db.find("select * from pl_report_work_order where woNo = ? and processCode = ? ", woNo, processCode);
        }
        if (processCode != null && !processCode.isEmpty() && proccessType != 1) {
            return findInspWorkOrder(woNo);
        }
        return Db.find("select * from pl_report_work_order where woNo = ? ", woNo);
    }

    public List<Record> findInspWorkOrder(String woNo) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.*, bi.no AS itemCode, bi.name AS itemName, bi.spec AS itemSpec ")
                .append("FROM pl_insp_work_order p ")
                .append("LEFT JOIN basitem bi ON bi.id = p.itemId ")
                .append("WHERE p.woNo = ?");

        return Db.find(sql.toString(), woNo);
    }

    public List<Record> findMaterialRelationsByReportWorkOrderId(Long reportWorkOrderId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT rm.*, ")
                .append("d.requestId, d.materialId, d.requestQty, d.approvedQty, d.actualQty, ")
                .append("d.usedQty AS detailUsedQty, d.detailStatus, d.remark AS detailRemark, ")
                .append("m.requestNo, m.applicantName, m.departmentName, m.projectName, ")
                .append("b.no AS itemCode, b.name AS itemName, b.spec AS itemSpec, b.unit AS itemUnit ")
                .append("FROM pl_reporder_material rm ")
                .append("LEFT JOIN pl_material_request_detail d ON d.id = rm.matDetailId ")
                .append("LEFT JOIN pl_material_request_main m ON m.id = d.requestId ")
                .append("LEFT JOIN basitem b ON b.id = d.materialId ")
                .append("WHERE rm.repOrderId = ? ")
                .append("ORDER BY rm.id ASC");

        return Db.find(sql.toString(), reportWorkOrderId);
    }

    private Map<String, Object> doSaveWithMaterial(PlReportWorkOrder plReportWorkOrder,
                                                   String processCode,
                                                   Integer reportUserId,
                                                   String reportUsername,
                                                   JSONArray selectmatArray) {
        final RuntimeException[] errorHolder = new RuntimeException[1];
        final Long[] recordIdHolder = new Long[1];
        final int[] relationCountHolder = new int[1];

        boolean success = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                try {
                    Date now = new Date();
                    if (plReportWorkOrder.getCreatedTime() == null) {
                        plReportWorkOrder.setCreatedTime(now);
                    }
                    if (plReportWorkOrder.getUpdatedTime() == null) {
                        plReportWorkOrder.setUpdatedTime(now);
                    }

                    if (!plReportWorkOrder.save()) {
                        throw new RuntimeException("Save report work order failed");
                    }

                    recordIdHolder[0] = plReportWorkOrder.getId();
                    relationCountHolder[0] = saveReporderMaterial(
                            plReportWorkOrder.getId(),
                            processCode,
                            reportUserId,
                            reportUsername,
                            selectmatArray,
                            now
                    );
                    return true;
                } catch (RuntimeException e) {
                    errorHolder[0] = e;
                    return false;
                } catch (Exception e) {
                    errorHolder[0] = new RuntimeException("Save report work order failed: " + e.getMessage(), e);
                    return false;
                }
            }
        });

        if (!success) {
            if (errorHolder[0] != null) {
                throw errorHolder[0];
            }
            throw new RuntimeException("Save report work order failed");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("recordId", recordIdHolder[0]);
        result.put("materialRelationCount", relationCountHolder[0]);
        return result;
    }

    private int saveReporderMaterial(Long reportWorkOrderId,
                                     String processCode,
                                     Integer reportUserId,
                                     String reportUsername,
                                     JSONArray selectmatArray,
                                     Date createTime) {
        if (!isCuttingProcess(processCode) || selectmatArray == null || selectmatArray.isEmpty()) {
            return 0;
        }

        int relationCount = 0;
        for (int i = 0; i < selectmatArray.size(); i++) {
            JSONObject item = selectmatArray.getJSONObject(i);
            if (item == null) {
                throw new IllegalArgumentException("Invalid selectmat item at index " + i);
            }

            Long matDetailId = item.getLong("id");
            Integer usedQty = getIntegerValue(item.get("quantity"));

            if (matDetailId == null) {
                throw new IllegalArgumentException("selectmat.id is required at index " + i);
            }
            if (usedQty == null) {
                throw new IllegalArgumentException("selectmat.quantity is required at index " + i);
            }

            PlReporderMaterial relation = new PlReporderMaterial();
            relation.setRepOrderId(Math.toIntExact(reportWorkOrderId));
            relation.setMatDetailId(Math.toIntExact(matDetailId));
            relation.setUsedQty(usedQty);
            relation.setReportUserId(reportUserId);
            relation.setReportUsername(reportUsername);
            relation.setCreateTime(createTime);

            if (!relation.save()) {
                throw new RuntimeException("Save report-material relation failed");
            }

            relationCount++;
        }

        return relationCount;
    }

    private JSONObject extractMainData(JSONObject jsonObject) {
        JSONObject mainData = jsonObject.getJSONObject("main");
        if (mainData != null) {
            return mainData;
        }

        JSONObject flatData = new JSONObject();
        flatData.putAll(jsonObject);
        flatData.remove("selectmat");
        return flatData;
    }

    private JSONArray extractSelectmatArray(JSONObject jsonObject, JSONObject mainData) {
        JSONArray selectmatArray = parseSelectmatValue(jsonObject.get("selectmat"));
        if ((selectmatArray == null || selectmatArray.isEmpty()) && mainData != null) {
            selectmatArray = parseSelectmatValue(mainData.get("selectmat"));
        }
        return selectmatArray == null ? new JSONArray() : selectmatArray;
    }

    private JSONArray parseSelectmatArray(String selectmatJson) {
        if (StrKit.isBlank(selectmatJson)) {
            return new JSONArray();
        }

        JSONArray selectmatArray = JSON.parseArray(selectmatJson);
        return selectmatArray == null ? new JSONArray() : selectmatArray;
    }

    private JSONArray parseSelectmatValue(Object selectmatValue) {
        if (selectmatValue == null) {
            return null;
        }
        if (selectmatValue instanceof JSONArray) {
            return (JSONArray) selectmatValue;
        }
        if (selectmatValue instanceof String && StrKit.notBlank((String) selectmatValue)) {
            return JSON.parseArray((String) selectmatValue);
        }
        return null;
    }

    private PlReportWorkOrder buildReportWorkOrder(JSONObject mainData) {
        Map<String, Object> attrMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : mainData.entrySet()) {
            String key = entry.getKey();
            if ("selectmat".equals(key)) {
                continue;
            }
            attrMap.put(key, normalizeValue(key, entry.getValue()));
        }

        PlReportWorkOrder order = new PlReportWorkOrder();
        order.put(attrMap);
        return order;
    }

    private Object normalizeValue(String key, Object value) {
        if (value == null) {
            return null;
        }

        if (isDateField(key)) {
            return parseDateValue(value);
        }

        if ("id".equals(key)) {
            return getLongValue(value);
        }

        if ("amount".equals(key) || "reporterId".equals(key) || "reportId".equals(key)) {
            return getIntegerValue(value);
        }

        return value;
    }

    private boolean isDateField(String key) {
        return key != null && (key.endsWith("Time") || key.endsWith("Date"));
    }

    private Date parseDateValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return (Date) value;
        }
        if (value instanceof Long) {
            return new Date((Long) value);
        }
        if (value instanceof String) {
            String str = ((String) value).trim();
            if (StrKit.isBlank(str)) {
                return null;
            }

            String[] patterns = {
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd HH:mm",
                    "yyyy-MM-dd",
                    "yyyy/MM/dd HH:mm:ss",
                    "yyyy/MM/dd HH:mm",
                    "yyyy/MM/dd"
            };

            for (String pattern : patterns) {
                try {
                    return new SimpleDateFormat(pattern).parse(str);
                } catch (ParseException ignored) {
                }
            }
        }
        return null;
    }

    private Integer getIntegerValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String && StrKit.notBlank((String) value)) {
            return Integer.parseInt(((String) value).trim());
        }
        return null;
    }

    private Long getLongValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String && StrKit.notBlank((String) value)) {
            return Long.parseLong(((String) value).trim());
        }
        return null;
    }

    private boolean isCuttingProcess(String processCode) {
        return StrKit.notBlank(processCode) && "10".equals(processCode.trim());
    }
}
