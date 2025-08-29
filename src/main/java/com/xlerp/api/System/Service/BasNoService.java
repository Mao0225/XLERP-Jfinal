package com.xlerp.api.System.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Db;
import com.xlerp.common.model.Basno;

import java.util.List;
import java.util.stream.Collectors;

public class BasNoService {
    private static final Basno dao = new Basno();

    public Page<Basno> paginate(int pageNumber, int pageSize, String basname,String memo) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from basno");
        if (StrKit.notBlank(basname)) {
            from.append(" where basname like '%").append(basname).append("%'");
        }
        if (StrKit.notBlank(memo)) {
            from.append(" where memo like '%").append(memo).append("%'");
        }
        from.append(" order by id desc");
        return dao.paginate(pageNumber, pageSize, select, from.toString());
    }

    public Basno findById(int id) {
        return dao.findFirst("select * from basno where id = ?", id);
    }



    /**
     * 根据编号简称生成新的完整编号
     * @param basname 编号简称(如: pcjh)
     * @return 生成的完整编号(格式: 简称+当前期次+5位序号，如: pcjh202300001)，失败返回null
     */
    public String getNewNoNyName(String basname) {
        // 使用数组包装String，以便在lambda表达式中修改值
        final String[] fullNoNyName = {null}; // 用于存储生成的完整编号

        // 开启事务处理
        boolean success = Db.tx(() -> {
            // 1. 查询并锁定要修改的记录(使用FOR UPDATE保证并发安全)
            Basno basno = dao.findFirst("SELECT * FROM basno WHERE basname = ? FOR UPDATE", basname);

            // 2. 验证查询结果的有效性
            if (basno == null || basno.getBasnum() == null
                    || basno.getBasname() == null || basno.getCurrentterm() == null) {
                return false; // 事务失败(记录不存在或必要字段为null)
            }

            // 3. 生成5位序号(当前basnum+1，不足5位前面补0)
            String formattedBasnum = String.format("%05d", basno.getBasnum() + 1);

            // 4. 递增序号(准备更新到数据库)
            basno.setBasnum(basno.getBasnum() + 1);

            // 5. 拼接完整编号(格式: 简称+期次+序号)
            fullNoNyName[0] = basno.getBasname() + basno.getCurrentterm() + formattedBasnum;

            // 6. 更新数据库并返回结果(true:成功, false:失败)
            return basno.update();
        });

        // 返回处理结果: 事务成功返回生成的编号，失败返回null
        return success ? fullNoNyName[0] : null;
    }

    public boolean save(Basno Basno) {
        return Basno.save();
    }

    public boolean update(Basno Basno) {
        return Basno.update();
    }

    public boolean DeleteById(int id) {
        return dao.deleteById(id);
    }

}