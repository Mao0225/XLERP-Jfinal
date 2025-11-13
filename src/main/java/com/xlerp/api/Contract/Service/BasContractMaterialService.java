package com.xlerp.api.Contract.Service;

import com.jfinal.plugin.activerecord.Db;

import java.util.List;

public class BasContractMaterialService {
    public List getMaterialList(String contractNo) {


        String select = "SELECT cm.*,i.no as itemNo,i.name as itemName,i.spec as itemSpec";
        String from = "FROM bas_contract_material cm " +
                "LEFT JOIN basitem i ON cm.itemId = i.id " +
                "WHERE cm.contractNo = ? " +
                "ORDER BY cm.id";

        // 拼接完整的SQL语句
        String sql = select + from;
        return Db.find(sql, contractNo);
    }
}
