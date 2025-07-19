package com.xlerp.api.PlManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PlManagement.Service.PlshengchangongdanService;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.Plgongdanitem;
import com.xlerp.common.model.Plshengchangongdan;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
//生产工单
public class PlshengchangongdanController extends Controller {
    private final PlshengchangongdanService plshengchangongdanService = new PlshengchangongdanService();

    @ActionKey("/plshengchangongdan/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String woNo = getPara("woNo");//生产工单编码
        String contractNo = getPara("contractNo");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson (Result.badRequest ("页码或每页大小必须为正整数"));
                return;
            }

            Page page = plshengchangongdanService.paginate (pageNum, pageSz ,woNo, contractNo);
            renderJson (Result.success ("查询成功").putData ("page", page));
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/plshengchangongdan/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
            return;
        }

        try {
            Plshengchangongdan plshengchangongdan = plshengchangongdanService.findById (Integer.parseInt (id));
            if (plshengchangongdan != null && plshengchangongdan.getIsdelete () == 0) {
                renderJson (Result.success ("查询记录成功").putData ("plshengchangongdan", plshengchangongdan));
            } else {
                renderJson (Result.notFound ("记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("记录 ID 格式错误"));
        }
    }

    @ActionKey ("/plshengchangongdan/save")
    @HttpMethod ("POST")
    public void save (Plshengchangongdan plshengchangongdan) {
        try {
            plshengchangongdan.setIsdelete(0); // 设置为正常状态
            System.out.println("controller保存工单信息"+plshengchangongdan);
            boolean success = plshengchangongdanService.save (plshengchangongdan);
            if (success) {
                renderJson (Result.success ("记录保存成功").putData ("recordId", plshengchangongdan.getId ()));
            } else {
                renderJson (Result.serverError ("保存记录失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("保存记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey ("/plshengchangongdan/update")
    @HttpMethod ("PUT")
    public void update (Plshengchangongdan plshengchangongdan) {
        try {
            boolean success = plshengchangongdanService.update (plshengchangongdan);
            if (success) {
                renderJson (Result.success ("记录更新成功"));
            } else {
                renderJson (Result.serverError ("更新记录失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("更新记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/plshengchangongdan/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
            return;
        }

        try {
            boolean success = plshengchangongdanService.logicalDeleteById (Integer.parseInt (id.trim ()));
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

    @ActionKey("/plshengchangongdan/batchdelete")
    @HttpMethod("DELETE")
    public void batchDelete() {
        String ids = getPara("ids");

        if (ids == null || ids.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 列表不能为空"));
            return;
        }

        try {
            List<Integer> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            if (idList.isEmpty ()) {
                renderJson (Result.badRequest ("记录 ID 列表不能为空"));
                return;
            }

            boolean success = plshengchangongdanService.batchLogicalDelete (idList);
            if (success) {
                renderJson (Result.success ("批量删除记录成功"));
            } else {
                renderJson (Result.serverError ("批量删除记录失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("记录 ID 格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("批量删除记录时发生错误:" + e.getMessage ()));
        }
    }

    //生产订单关联的产品操作
    @ActionKey("/plshengchangongdan/item/getList")
    @HttpMethod("GET")
    public void getGongdanItemList() {
        String woNo = getPara("woNo");//生产工单号
        if (woNo == null || woNo.trim().isEmpty()) {
            renderJson(Result.badRequest("订单号不能为空"));
        }
        try {
            List<Record> itemList = plshengchangongdanService.getGongdanItemByNo(woNo);
            renderJson(Result.success("查询物料列表成功").putData("itemList", itemList));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/plshengchangongdan/item/save")
    @HttpMethod("POST")
    public void saveGongdanItem(Plgongdanitem Item) {
        System.out.println("保存物料1"+ Item);
        try {
            boolean success = plshengchangongdanService.saveGongdanItem(Item);
            if (success) {
                renderJson(Result.success("保存物料成功"));
            } else {
                renderJson(Result.serverError("保存物料失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("保存物料时发生错误:" + e.getMessage()));
        }
    }

    //修改
    @ActionKey("/plshengchangongdan/item/update")
    @HttpMethod("PUT")
    public void updateGongdanItem(Plgongdanitem Item) {
        try {
            boolean success = plshengchangongdanService.updateGongdanItem(Item);
            if (success) {
                renderJson(Result.success("修改物料成功"));
            } else {
                renderJson(Result.serverError("修改物料失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("修改物料时发生错误:" + e.getMessage()));
        }
    }

    //删除
    @ActionKey("/plshengchangongdan/item/delete")
    @HttpMethod("DELETE")
    public void deleteGongdanItem() {
        String id = getPara("id");
        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("物料ID不能为空"));
            return;
        }
        try {
            boolean success = plshengchangongdanService.deleteGongdanItem(Integer.parseInt(id.trim()));
            if (success) {
                renderJson(Result.success("删除物料成功"));
            } else {
                renderJson(Result.serverError("删除物料失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("物料ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除物料时发生错误:" + e.getMessage()));
        }
    }

}