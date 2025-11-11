package com.xlerp.api.PlInspectionController.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PlInspectionController.Service.InspStandardService;
import com.xlerp.api.PlInspectionController.Dto.CreateStdDTO;
import com.xlerp.common.model.PlInspStd;

@Before(HttpMethodInterceptor.class)
public class InspStandardController extends Controller {
    private final InspStandardService service = new InspStandardService();

    /**
     * 分页查询检验标准列表
     * 支持按标准编号、材料、牌号模糊搜索
     */
    @ActionKey("/insp_std/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String param = getPara("param");
        try {
            int pageNum = parseInt(pageNumber, 1);
            int pageSz = parseInt(pageSize, 10);
            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("分页参数错误"));
                return;
            }
            Page<PlInspStd> page = service.paginate(pageNum, pageSz, param);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (Exception e) {
            renderJson(Result.badRequest("参数错误"));
        }
    }

    /**
     * 根据ID查询检验标准详情（不含明细）
     */
    @ActionKey("/insp_std/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");
        if (isBlank(id)) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }
        PlInspStd std = service.findById(parseLong(id));
        if (std != null) {
            renderJson(Result.success("查询成功").putData("standard", std));
        } else {
            renderJson(Result.notFound("标准未找到"));
        }
    }

    /**
     * 新增检验标准
     * 校验 standardNo 唯一性
     */
    @ActionKey("/insp_std/save")
    @HttpMethod("POST")
    public void save(PlInspStd std) {
        if (isBlank(std.getStandardNo())) {
            renderJson(Result.badRequest("标准编号不能为空"));
            return;
        }
        if (service.existsByNo(std.getStandardNo())) {
            renderJson(Result.badRequest("标准编号已存在"));
            return;
        }
        boolean success = std.save();
        renderJson(success ? Result.success("保存成功").putData("id", std.getId())
                : Result.serverError("保存失败"));
    }

    /**
     * 更新检验标准信息
     */
    @ActionKey("/insp_std/update")
    @HttpMethod("PUT")
    public void update(PlInspStd std) {
        if (std.getId() == null) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }
        boolean success = std.update();
        renderJson(success ? Result.success("更新成功") : Result.serverError("更新失败"));
    }

    /**
     * 删除检验标准及所有明细
     */
    @ActionKey("/insp_std/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");
        if (isBlank(id)) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }
        boolean success = service.deleteWithItems(parseLong(id));
        renderJson(success ? Result.success("删除成功") : Result.serverError("删除失败"));
    }

    /**
     * 一键套用标准到检验单
     * 自动生成 pl_insp_result 空记录（带标准值）
     */
    @ActionKey("/insp_std/apply")
    @HttpMethod("POST")
    public void apply() {
        String orderId = getPara("orderId");
        String standardId = getPara("standardId");
        if (isBlank(orderId) || isBlank(standardId)) {
            renderJson(Result.badRequest("orderId 和 standardId 不能为空"));
            return;
        }
        boolean success = service.applyStandard(parseLong(orderId), parseLong(standardId));
        renderJson(success ? Result.success("套用成功") : Result.serverError("套用失败"));
    }

    /***
     * 创建标准及标准明细
     */
    @ActionKey("/insp_std/createStdAndItem")
    @HttpMethod("POST")
    public void createStdAndItem(CreateStdDTO dto) {
        Boolean success = service.createWithItems(dto);
        renderJson(success ? Result.success("保存成功") : Result.serverError("保存成功"));
    }

    /**
     * 获取标准以及明细根据标准id
     */
    @ActionKey("/insp_std/getStdAndItem")
    @HttpMethod("GET")
    public void getStdAndItem() {
        String id = getPara("stdId");
        CreateStdDTO std = service.getStdAndItem(parseLong(id));
        renderJson(std != null ? Result.success("查询成功").putData("record", std) : Result.notFound("标准未找到"));
    }

    // 工具方法
    private int parseInt(String s, int def) {
        return (s != null && !s.trim().isEmpty()) ? Integer.parseInt(s) : def;
    }
    private long parseLong(String s) {
        return Long.parseLong(s.trim());
    }
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}