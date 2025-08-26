package com.xlerp.api.PlManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PlManagement.Service.PlpaichanjihuaService;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.Tongzhi.Service.BeiliaojihuaService;
import com.xlerp.common.model.Plbeiliaojihua;
import com.xlerp.common.model.Plpaichanjihua;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
public class PlpaichanjihuaController extends Controller {
    private final PlpaichanjihuaService plpaichanjihuaService = new PlpaichanjihuaService();

    @ActionKey("/plpaichanjihua/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String contractNo = getPara("contractNo");
        String woNo = getPara("woNo");
        String ipoNo = getPara("ipoNo");
        String scheduleCode = getPara("scheduleCode");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson (Result.badRequest ("页码或每页大小必须为正整数"));
                return;
            }

            Page page = plpaichanjihuaService.paginate (pageNum, pageSz, contractNo, woNo, ipoNo, scheduleCode);
            renderJson (Result.success ("查询成功").putData ("page", page));
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/plpaichanjihua/getpageByDepNo")//根据登录用户的部门进行查询
    @HttpMethod("GET")
    public void getpageByDepNo() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String contractNo = getPara("contractNo");
        String woNo = getPara("woNo");
        String ipoNo = getPara("ipoNo");
        String scheduleCode = getPara("scheduleCode");
        String depNo = getPara("depNo");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson (Result.badRequest ("页码或每页大小必须为正整数"));
                return;
            }

            Page page = plpaichanjihuaService.paginateByDepNo (pageNum, pageSz, contractNo, woNo, ipoNo, scheduleCode, depNo);
            renderJson (Result.success ("查询成功").putData ("page", page));
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/plpaichanjihua/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
            return;
        }

        try {
            Plpaichanjihua plpaichanjihua = plpaichanjihuaService.findById (Integer.parseInt (id));
            if (plpaichanjihua != null && plpaichanjihua.getIsdelete () == 0) {
                renderJson (Result.success ("查询记录成功").putData ("plpaichanjihua", plpaichanjihua));
            } else {
                renderJson (Result.notFound ("记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("记录 ID 格式错误"));
        }
    }



    @ActionKey ("/plpaichanjihua/save")
    @HttpMethod ("POST")
    public void save (Plpaichanjihua plpaichanjihua) {
        try {
            plpaichanjihua.setIsdelete (0); // 设置为正常状态
            boolean success = plpaichanjihuaService.save (plpaichanjihua);
            if (success) {
                renderJson (Result.success ("记录保存成功").putData ("recordId", plpaichanjihua.getId ()));
            } else {
                renderJson (Result.serverError ("保存记录失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("保存记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey ("/plpaichanjihua/update")
    @HttpMethod ("PUT")
    public void update (Plpaichanjihua plpaichanjihua) {
        try {
            boolean success = plpaichanjihuaService.update (plpaichanjihua);
            if (success) {
                renderJson (Result.success ("记录更新成功"));
            } else {
                renderJson (Result.serverError ("更新记录失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("更新记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/plpaichanjihua/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
            return;
        }

        try {
            boolean success = plpaichanjihuaService.logicalDeleteById (Integer.parseInt (id.trim ()));
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

    @ActionKey("/plpaichanjihua/batchdelete")
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

            boolean success = plpaichanjihuaService.batchLogicalDelete (idList);
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




    @ActionKey("/plpaichanjihua/getbeiliaojihuapage")
    @HttpMethod("GET")
    public void getbeiliaojihuapage() {
        //通过通知编号，物料编号，获取备料计划单，这个是在制定备料计划表的时候试用

        String gdItemId = getPara("gdItemId");

        try {

            List<Plbeiliaojihua> List = plpaichanjihuaService.beiliaojihuaList(gdItemId);
            renderJson(Result.success("查询成功").putData("List", List));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("错误"));
        }
    }
}