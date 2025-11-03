package com.xlerp.api.PlInspectionController.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.PlInspResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InspResultService {

    /**
     * 批量保存检验结果（支持平行试验）
     * @param orderId     检验单ID
     * @param resultsJson JSON 数组，格式：[{"inspItemId":1,"actualValue":"520","testIndex":1},...]
     * @return 保存成功返回 true
     */
    public boolean batchSave(long orderId, String resultsJson) {
        try {
            List<PlInspResult> results = JSON.parseObject(resultsJson,
                    new TypeReference<List<PlInspResult>>() {});
            for (PlInspResult r : results) {
                r.setInspOrderId(orderId);
            }
            int[] counts = Db.batchSave(results, results.size());
            return counts.length == results.size();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 自动判定检验结果是否合格
     * 逻辑：数字对比 min/max，文本匹配 standardValue
     * @param orderId 检验单ID
     * @return 判定完成返回 true
     */
    public boolean autoJudge(long orderId) {
        String sql = """
            SELECT r.id, r.actualValue, si.minValue, si.maxValue, si.standardValue
            FROM pl_insp_result r
            LEFT JOIN pl_insp_std_item si ON r.inspItemId = si.inspItemId
            WHERE r.inspOrderId = ? AND si.standardId = (
                SELECT standardId FROM pl_insp_order WHERE id = ?
            )
            """;
        List<Record> records = Db.find(sql, orderId, orderId);
        List<Record> updates = new ArrayList<>();

        for (Record r : records) {
            String actual = r.getStr("actualValue");
            BigDecimal min = r.getBigDecimal("minValue");
            BigDecimal max = r.getBigDecimal("maxValue");
            String stdText = r.getStr("standardValue");

            boolean pass = false;
            if (isNumeric(actual)) {
                BigDecimal val = new BigDecimal(actual);
                pass = (min == null || val.compareTo(min) >= 0) &&
                        (max == null || val.compareTo(max) <= 0);
            } else if (stdText != null) {
                pass = stdText.contains(actual.trim());
            }
            updates.add(new Record().set("id", r.getLong("id")).set("isPass", pass ? 1 : 0));
        }
        return Db.batchUpdate("pl_insp_result", "id", updates, updates.size()).length > 0;
    }

    private boolean isNumeric(String str) {
        try { new BigDecimal(str); return true; } catch (Exception e) { return false; }
    }
}