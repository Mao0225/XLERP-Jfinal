package com.xlerp.api.PlProductionOrder.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.api.System.Service.BasNoService;
import com.xlerp.common.model.PlProductionOrder;

import java.util.ArrayList;
import java.util.List;

public class PlProductionOrderService {
    private static final PlProductionOrder dao = new PlProductionOrder();
    private static final BasNoService basNoService = new BasNoService();

    public Page<PlProductionOrder> paginate(int pageNumber, int pageSize,
                                            String contractNo, String contractName,
                                            String scheduleCode,
                                            String status) {
        // 构建查询字段
        String select = "select p.*,bc.no as contractNo,bc.name as contractName,bci.itemnum as contractAmount, bi.name as itemName, " +
                "bi.spec as itemSpec,bci.itemunit as itemUnit,bi.id as basItemId ," +//basItemId就是basitem表的id用于工单分解半成品用
                "(select COALESCE(sum(pwo.amount), 0) from pl_work_order pwo where pwo.ipoNo = p.ipoNo and pwo.materialsCode = p.materialsCode ) as allocatedAmount";

        // 构建FROM子句和基础WHERE条件
        StringBuilder from = new StringBuilder("from pl_production_order p ");
        from.append("left join bascontractitem bci on p.poItemId = bci.id ");
        from.append("left join bascontract bc on bc.no = bci.no ");
        from.append("left join basitem bi on bci.itemid = bi.id ");
        from.append("where 1 = 1 "); // 基础条件，简化后续拼接

        // 构建查询参数
        List<Object> params = new ArrayList<>();

        // 动态添加查询条件
        if (contractNo != null && !contractNo.isEmpty()) {
            from.append("and bci.no like ? ");
            params.add("%" + contractNo + "%");
        }

        if (contractName != null && !contractName.isEmpty()) {
            from.append("and bc.name like ? ");
            params.add("%" + contractName + "%");
        }

        if (scheduleCode != null && !scheduleCode.isEmpty()) {
            from.append("and p.scheduleCode like ? ");
            params.add("%" + scheduleCode + "%");
        }
        if (status != null && !status.isEmpty()) {
            from.append("and p.status = ? ");
            params.add(status);
        }
        // 添加排序
        from.append("order by p.id desc");

        // 执行分页查询
        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public PlProductionOrder findById(int id) {
        return dao.findFirst("select * from pl_production_order where id = ? ", id);
    }

    public boolean save(PlProductionOrder pl_production_order) {
        if (pl_production_order.getIpoNo() == null || pl_production_order.getIpoNo().isEmpty()) {
            pl_production_order.setIpoNo(basNoService.getNewNoNyName("scdd"));
        }
        return pl_production_order.save();
    }

    public boolean update(PlProductionOrder pl_production_order) {
        return pl_production_order.update();
    }

    public boolean DeleteById(int id) {
        return dao.deleteById( id);
    }

    public boolean batchDelete(List<Integer> ids) {

        return dao.deleteByIds(ids);
    }


    public boolean updateStatus(String id, String status) {
        return Db.update("update pl_production_order set status = ? where id = ? ", status, id) > 0;
    }

    public List<PlProductionOrder> findByScheduleCode(String scheduleCode) {
        return dao.find("select * from pl_production_order where scheduleCode = ? ", scheduleCode);
    }

    public Page<Record> paginateBatchSummary(int pageNumber, int pageSize,
                                             String contractNo, String contractName,
                                             String scheduleCode,
                                             String status) {

        // ===== 主查询SQL =====
        String baseSql = """
        from pl_production_order p
        left join bascontractitem bci on p.poItemId = bci.id
        left join bascontract bc on bc.no = bci.no
        left join basitem bi on bci.itemid = bi.id
        where 1=1
        """;

        List<Object> params = new ArrayList<>();

        if (contractNo != null && !contractNo.isEmpty()) {
            baseSql += " and bci.no like ? ";
            params.add("%" + contractNo + "%");
        }
        if (contractName != null && !contractName.isEmpty()) {
            baseSql += " and bc.name like ? ";
            params.add("%" + contractName + "%");
        }
        if (scheduleCode != null && !scheduleCode.isEmpty()) {
            baseSql += " and p.scheduleCode like ? ";
            params.add("%" + scheduleCode + "%");
        }
        if (status != null && !status.isEmpty()) {
            baseSql += " and p.status = ? ";
            params.add(status);
        }

        // ===== 1️⃣ 计算总数：分组后计数 =====
        String countSql = "select count(*) as total from (select p.ipoBatchNo " + baseSql +
                " group by p.ipoBatchNo, bc.no, bc.name, p.writer) t";
        long totalRow = Db.queryLong(countSql, params.toArray());

        // ===== 2️⃣ 分页查询实际数据 =====
        String querySql = """
        select 
            p.ipoBatchNo,
            bc.no as contractNo,
            bc.name as contractName,
            p.writer,
            p.ipoType,
            max(p.createdTime) as createdTime,
            sum(case when p.status = '10' then 1 else 0 end) as status10Count,
            sum(case when p.status = '20' then 1 else 0 end) as status20Count,
            sum(case when p.status = '30' then 1 else 0 end) as status30Count,
            listagg(distinct p.materialsName, ',') within group(order by p.materialsName) as materialsNames
        """ + baseSql +
                " group by p.ipoBatchNo, bc.no, bc.name, p.writer " +
                " order by max(p.createdTime) desc";

        // 达梦不支持 LIMIT，分页用 ROWNUM 包裹
        long offset = (pageNumber - 1L) * pageSize;
        String pagedSql = "select * from (" +
                "select t.*, rownum as rn from (" + querySql + ") t where rownum <= ?" +
                ") where rn > ?";

        params.add(pageNumber * pageSize);
        params.add(offset);

        List<Record> list = Db.find(pagedSql, params.toArray());

        // ===== 构建 Page<Record> 返回 =====
        return new Page<>(list, pageNumber, pageSize, (int) Math.ceil((double) totalRow / pageSize), (int) totalRow);
    }


    public List<Record> getAllList(String contractNo, String scheduleCode, String ipoBatchNo) {
        String sql = "select p.*, bc.no as contractNo, bc.name as contractName, " +
                "bci.itemnum as contractAmount, bi.name as itemName, bi.spec as itemSpec, bci.itemunit as itemUnit, " +
                "(select COALESCE(sum(pwo.amount), 0) from pl_work_order pwo where pwo.ipoNo = p.ipoNo) as workOrderallocatedAmount " +
                "from pl_production_order p " +
                "left join bascontractitem bci on p.poItemId = bci.id " +
                "left join bascontract bc on bc.no = bci.no " +
                "left join basitem bi on bci.itemid = bi.id " +
                "where 1=1 ";
        //workOrderallocatedAmount该订单已分配的工单数量
        List<Object> params = new ArrayList<>();

        if (contractNo != null && !contractNo.trim().isEmpty()) {
            sql += "and bci.no like ? ";
            params.add("%" + contractNo.trim() + "%");
        }
        if (ipoBatchNo != null && !ipoBatchNo.trim().isEmpty()) {
            sql += "and p.ipoBatchNo like ? ";
            params.add("%" + ipoBatchNo.trim() + "%");
        }
        if (scheduleCode != null && !scheduleCode.trim().isEmpty()) {
            sql += "and p.scheduleCode like ? ";
            params.add("%" + scheduleCode.trim() + "%");
        }

        sql += "order by p.id desc";

        return params.isEmpty() ? Db.find(sql) : Db.find(sql, params.toArray());
    }


    //根据ipoNo查找唯一记录订单
    public Record getByipoNo(String ipoNo) {
        String sql = "select p.*, bc.no as contractNo, bc.name as contractName, " +
                "bci.itemnum as contractAmount, bi.name as itemName, bi.spec as itemSpec, bci.itemunit as itemUnit, " +
                "(select COALESCE(sum(pwo.amount), 0) from pl_work_order pwo where pwo.ipoNo = p.ipoNo) as workOrderallocatedAmount " +
                "from pl_production_order p " +
                "left join bascontractitem bci on p.poItemId = bci.id " +
                "left join bascontract bc on bc.no = bci.no " +
                "left join basitem bi on bci.itemid = bi.id " +
                "where 1=1 ";
        //workOrderallocatedAmount该订单已分配的工单数量
        List<Object> params = new ArrayList<>();

        if (ipoNo != null && !ipoNo.trim().isEmpty()) {
            sql += "and p.ipoNo like ? ";
            params.add("%" + ipoNo.trim() + "%");
        }
        sql += "order by p.id desc";

        return Db.findFirst(sql, params.toArray());
    }



}