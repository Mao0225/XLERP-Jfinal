package com.xlerp.api.PlInspectionController.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.api.PlInspectionController.dto.CreateStdDTO;
import com.xlerp.common.model.PlInspStd;
import com.xlerp.common.model.PlInspStdItem;

import java.util.ArrayList;
import java.util.List;

public class InspStandardService {
    private static final PlInspStd stdDao = new PlInspStd().dao();
    private static final PlInspStdItem itemDao = new PlInspStdItem().dao();

    /**
     * 分页查询检验标准列表
     * 支持按标准编号、适用材料、材料牌号模糊搜索
     * @param pageNumber 当前页码
     * @param pageSize   每页条数
     * @param param      搜索关键词
     * @return 分页结果
     */
    public Page<PlInspStd> paginate(int pageNumber, int pageSize, String param) {
        String select = "select *";
        StringBuilder sql = new StringBuilder("from pl_insp_std where 1=1 ");
        List<Object> params = new ArrayList<>();
        if (StrKit.notBlank(param)) {
            sql.append("and (standardNo like ? or materials like ? or matNo like ?)");
            params.add("%" + param + "%");
            params.add("%" + param + "%");
            params.add("%" + param + "%");
        }
        sql.append(" order by id desc");
        return stdDao.paginate(pageNumber, pageSize, select, sql.toString(), params.toArray());
    }

    /**
     * 根据ID查询检验标准主表信息
     * @param id 主键ID
     * @return 标准对象，不存在返回 null
     */
    public PlInspStd findById(long id) {
        return stdDao.findById(id);
    }

    /**
     * 检查标准编号是否已存在（用于新增时唯一性校验）
     * @param no 标准编号
     * @return 存在返回 true
     */
    public boolean existsByNo(String no) {
        return Db.queryLong("select count(*) from pl_insp_std where standardNo = ?", no) > 0;
    }

    /**
     * 删除检验标准及其所有明细项目（级联删除）
     * @param stdId 标准ID
     * @return 删除成功返回 true
     */
    public boolean deleteWithItems(long stdId) {
        Db.delete("delete from pl_insp_std_item where standardId = ?", stdId);
        return new PlInspStd().deleteById(stdId);
    }

    /**
     * 一键套用标准到指定检验单
     * 自动为每个标准明细生成一条空的检验结果记录（待录入）
     * @param orderId    检验单ID
     * @param standardId 标准ID
     * @return 套用成功返回 true
     */
    public boolean applyStandard(long orderId, long standardId) {
        List<PlInspStdItem> items = new PlInspStdItem().dao().find(
                "select * from pl_insp_std_item where standardId = ?", standardId);
        if (items.isEmpty()) return false;

        List<Record> results = new ArrayList<>();
        for (PlInspStdItem item : items) {
            Record r = new Record()
                    .set("inspOrderId", orderId)
                    .set("inspItemId", item.getInspItemId())
                    .set("actualValue", "")
                    .set("testIndex", 1)
                    .set("isPass", null);
            results.add(r);
        }
        int[] counts = Db.batchSave("pl_insp_result", results, results.size());
        return counts.length > 0;
    }


    public boolean createWithItems(CreateStdDTO dto) {
        return Db.tx(() -> {
            PlInspStd std = dto.getStd();

            // 保存主表
            if (!std.save()) {
                throw new RuntimeException("保存标准失败");
            }
            Long stdId = std.getLong("id");

            // 保存明细
            List<PlInspStdItem> items = dto.getItems();
            if (items != null && !items.isEmpty()) {
                for (PlInspStdItem item : items) {
                    item.set("standardId", stdId).set("flag", 0);
                }
                int[] results = Db.batchSave(items, items.size());
                for (int r : results) {
                    if (r <= 0) throw new RuntimeException("明细保存失败");
                }
            }

            return true;  // 成功
        });
    }

    public CreateStdDTO getStdAndItem(long id) {
        PlInspStd std = stdDao.findById(id);
        List<PlInspStdItem> items = itemDao.find("select si.*,i.inspItemCode,i.inspItemName,i.dataType,i.unit,i.category from pl_insp_std_item si " +
                "left join pl_insp_item i on si.inspItemId = i.id" +
                " where standardId = ?", id);
        CreateStdDTO dto = new CreateStdDTO(std, items);
        return dto;
    }
}