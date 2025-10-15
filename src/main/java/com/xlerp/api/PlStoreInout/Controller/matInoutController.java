package com.xlerp.api.PlStoreInout.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PlStoreInout.Service.matInoutService;
import com.xlerp.common.model.PlMatInoutDoc;
import com.xlerp.common.model.PlMatInoutItem;

@Before(HttpMethodInterceptor.class)
public class matInoutController extends Controller {
    private final matInoutService matService = new matInoutService();

    @ActionKey("/pl_mat_inout/getpage")
    @HttpMethod("GET")
    public void getpage() {
        // 获取分页参数
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String inOutType = getPara("inOutType");
        // 获取查询条件参数
        String status = getPara("status");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            // 调用修改后的分页查询方法，传入所有查询条件
            Page page = matService.paginate(pageNum, pageSz,status,inOutType);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }


    @ActionKey("/pl_mat_inout/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
            return;
        }

        try {
            PlMatInoutDoc pl_mat_inout = matService.findById (Integer.parseInt (id));
            if (pl_mat_inout != null ) {
                renderJson (Result.success ("查询记录成功").putData ("matDocList", pl_mat_inout));
            } else {
                renderJson (Result.notFound ("记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("记录 ID 格式错误"));
        }
    }

    @ActionKey ("/pl_mat_inout/save")
    @HttpMethod ("POST")
    public void save (PlMatInoutDoc pl_mat_inout) {
        try {
            boolean success = matService.save (pl_mat_inout);
            if (success) {
                renderJson (Result.success ("记录保存成功").putData ("recordId", pl_mat_inout.getId ()));
            } else {
                renderJson (Result.serverError ("保存记录失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("保存记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey ("/pl_mat_inout/update")
    @HttpMethod ("PUT")
    public void update (PlMatInoutDoc pl_mat_inout) {
        try {
            boolean success = matService.update (pl_mat_inout);
            if (success) {
                renderJson (Result.success ("记录更新成功"));
            } else {
                renderJson (Result.serverError ("更新记录失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("更新记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/pl_mat_inout/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
            return;
        }

        try {
            boolean success = matService.LogicDeleteById (Integer.parseInt (id.trim ()));
            if (success) {
                renderJson (Result.success ("记录删除成功"));
            } else {
                renderJson (Result.notFound ("记录不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("记录 ID 格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("删除记录时发生错误:" + e.getMessage ()));
        }
    }

    //确认原材料出入库
    @ActionKey("/pl_mat_inout/updateStatus")
    @HttpMethod("GET")
    public void updateStatus() {
        String id = getPara("id");
        String status = getPara("status");
        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
        }
        try {
            boolean success = matService.updateStatus(id,status);
            if (success) {
                renderJson(Result.success("状态更新成功"));
            } else {
                renderJson(Result.serverError("更新状态失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("记录 ID 格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("确认原材料出入库时发生错误:" + e.getMessage ()));
        }
    }




    //原材料出库明显表
    @ActionKey("/pl_mat_inout/item/getpage")
    @HttpMethod("GET")
    public void getItemPage() {
        // 获取分页参数
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String docNo = getPara("docNo");
        String materialCode = getPara("materialCode");
        String materialName = getPara("materialName");
        String materialSpec = getPara("materialSpec");
        String inOutType = getPara("inOutType");
        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            // 调用修改后的分页查询方法，传入所有查询条件
            Page page = matService.itemPaginate(pageNum, pageSz, docNo, materialCode, materialName, materialSpec,inOutType);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/pl_mat_inout/item/get")
    @HttpMethod("GET")
    public void getItem() {
        String id = getPara("id");
        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("记录ID不能为空"));
            return;
        }

        try {
            PlMatInoutItem item = matService.findItemById(Integer.parseInt(id));
            if (item != null) {
                renderJson(Result.success("查询成功").putData("item", item));
            } else {
                renderJson(Result.notFound("记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("记录ID格式错误"));
        }
    }

    @ActionKey("/pl_mat_inout/item/save")
    @HttpMethod("POST")
    public void saveItem(PlMatInoutItem item) {
        try {
            boolean success = matService.saveItem(item);
            if (success) {
                renderJson(Result.success("记录保存成功").putData("itemId", item.getId()));
            } else {
                renderJson(Result.serverError("保存记录失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("保存记录时发生错误:" + e.getMessage()));
        }
    }

    @ActionKey("/pl_mat_inout/item/update")
    @HttpMethod("PUT")
    public void updateItem(PlMatInoutItem item) {
        try {
            boolean success = matService.updateItem(item);
            if (success) {
                renderJson(Result.success("记录更新成功"));
            } else {
                renderJson(Result.serverError("更新记录失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("更新记录时发生错误:" + e.getMessage()));
        }
    }

    @ActionKey("/pl_mat_inout/item/delete")
    @HttpMethod("DELETE")
    public void deleteItem() {
        String id = getPara("id");
        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("记录ID不能为空"));
            return;
        }

        try {
            boolean success = matService.deleteItemById(Integer.parseInt(id));
            if (success) {
                renderJson(Result.success("记录删除成功"));
            } else {
                renderJson(Result.notFound("记录不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("记录ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除记录时发生错误:" + e.getMessage()));
        }
    }
    
}