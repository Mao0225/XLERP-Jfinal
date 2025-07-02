package com.xlerp.api.Tongzhi.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.Tongzhi.Service.BeiliaojihuaService;
import com.xlerp.common.model.Plbeiliaojihua;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

@Before(HttpMethodInterceptor.class)
public class BeiliaojihuaController extends Controller {
    private final BeiliaojihuaService beiliaojihuaService = new BeiliaojihuaService();

    @ActionKey("/beiliaojihua/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String noticeid = getPara("noticeid");
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Plbeiliaojihua> page = beiliaojihuaService.paginate(pageNum, pageSz, noticeid);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/beiliaojihua/getbeiliaojihuapage")
    @HttpMethod("GET")
    public void getbeiliaojihuapage() {
        //通过通知编号，物料编号，获取备料计划单，这个是在制定备料计划表的时候试用
        String noticeid = getPara("noticeid");
        String noticedrawno = getPara("noticedrawno");
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Plbeiliaojihua> page = beiliaojihuaService.beiliaojihuapaginate(pageNum, pageSz, noticeid, noticedrawno);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/beiliaojihua/getbeiliaojihuabynoticepage")
    @HttpMethod("GET")
    public void getbeiliaojihuabynoticepage() {
        //通过通知编号，获取备料计划单，这个是在制定备料计划表的时候试用，这个是在查看一个通知的所有备料计划的时候试用
        String noticeid = getPara("noticeid");
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Plbeiliaojihua> page = beiliaojihuaService.beiliaojihuabynoticepaginate(pageNum, pageSz, noticeid);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }
    @ActionKey("/beiliaojihua/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("备料计划ID不能为空"));
            return;
        }

        try {
            Plbeiliaojihua beiliaojihua = beiliaojihuaService.findById(Integer.parseInt(id));
            if (beiliaojihua != null) {
                renderJson(Result.success("查询备料计划成功").putData("beiliaojihua", beiliaojihua));
            } else {
                renderJson(Result.notFound("备料计划未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("备料计划ID格式错误"));
        }
    }
    /**
     * 检查是否存在相同的noticeid和noticedrawno的数据
     */
    public boolean checkExists(String noticeid, String noticedrawno) {
        String sql = "SELECT COUNT(*) FROM plbeiliaojihua WHERE noticeid = ? AND noticedrawno = ?";
        int count = Db.queryInt(sql, noticeid, noticedrawno);
        return count > 0;
    }
    @ActionKey("/beiliaojihua/save")
    @HttpMethod("POST")
    public void save(Plbeiliaojihua beiliaojihua) {
        try {
            if (beiliaojihua == null || beiliaojihua.getContractno() == null || beiliaojihua.getContractno().trim().isEmpty() ||
                    beiliaojihua.getContractname() == null || beiliaojihua.getContractname().trim().isEmpty()) {
                renderJson(Result.badRequest("备料计划信息不能为空且合同编号和工程名称必须填写"));
                return;
            }
            boolean success = beiliaojihuaService.save(beiliaojihua);
            if (success) {
                renderJson(Result.success("备料计划保存成功").putData("beiliaojihuaId", beiliaojihua.getId()));
            } else {
                renderJson(Result.serverError("保存备料计划失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("保存备料计划时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/beiliaojihua/update")
    @HttpMethod("PUT")
    public void update(Plbeiliaojihua beiliaojihua) {
        try {
            if (beiliaojihua == null || beiliaojihua.getId() == null) {
                renderJson(Result.badRequest("备料计划ID不能为空"));
                return;
            }
            boolean success = beiliaojihuaService.update(beiliaojihua);
            if (success) {
                renderJson(Result.success("备料计划更新成功"));
            } else {
                renderJson(Result.serverError("更新备料计划失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("备料计划ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新备料计划时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/beiliaojihua/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("备料计划ID不能为空"));
            return;
        }

        try {
            boolean success = beiliaojihuaService.deleteById(Integer.parseInt(id));
            if (success) {
                renderJson(Result.success("备料计划删除成功"));
            } else {
                renderJson(Result.notFound("备料计划不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("备料计划ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除备料计划时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/beiliaojihua/getoption")
    @HttpMethod("GET")
    public void getoption() {
        try {
            List<Record> options = beiliaojihuaService.getOptions();
            renderJson(Result.success("查询选项成功").putData("options", options));
        } catch (Exception e) {
            renderJson(Result.serverError("查询选项时发生错误: " + e.getMessage()));
        }
    }
}