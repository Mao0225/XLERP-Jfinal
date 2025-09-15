package com.xlerp.api.Tongzhi.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.Tongzhi.Service.TongzhiService;
import com.xlerp.common.model.Bascontractitem;
import com.xlerp.common.model.Bastuzhi;
import com.xlerp.common.model.Plbeiliaojihua;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Before(HttpMethodInterceptor.class)
public class TongzhiController extends Controller {
    private final TongzhiService tongzhiService = new TongzhiService();

    @ActionKey("/tongzhi/getpage")
    @HttpMethod("GET")
    public void getpage() {
        //制定通知之前，获取活通列表
        //测试接口：http://localhost:8099/tongzhi/getpage
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String term = getPara("term");
        String contractNo = getPara("contractNo");
        String projectName = getPara("projectName");
        String salesmanNo = getPara("salesmanNo");
        String rule = getPara("rule");
        String owenr = getPara("owenr");
        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Record> page = tongzhiService.getContractList(pageNum, pageSz, term, contractNo, projectName, salesmanNo, rule, owenr);
            renderJson(Result.success("查询合同列表成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/tongzhi/gettuzhilist")
    @HttpMethod("GET")
    public void gettuzhilist() {
        //获取图纸列表，这个是指定通知功能，获取图纸列表的接口，刘国奇
        //测试接口 ：http://localhost:8099/tongzhi/gettuzhilist
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String tuzhibianhao = getPara("tuzhibianhao");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Bastuzhi> page = tongzhiService.gettuzhilist(pageNum, pageSz, tuzhibianhao);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/tongzhi/updateitem")
    @HttpMethod("PUT")
//修改单条通知信息
    public void updateitem(Bascontractitem bascontractitem) {
        // 调试日志：方法开始执行
        System.out.println("===== updateitem 方法开始执行 =====");

        // 打印接收的参数信息
        System.out.println("接收的Bascontractitem对象: " + bascontractitem);
        if (bascontractitem != null) {
            System.out.println("物料ID(主键): " + bascontractitem.getId());
            System.out.println("通知编号: " + bascontractitem.getNoticeid());
            System.out.println("图纸编号: " + bascontractitem.getNoticedrawno());
        } else {
            System.out.println("警告：接收的Bascontractitem对象为null");
        }

        try {
            // 验证主键是否存在且格式正确
            if (bascontractitem == null) {
                System.out.println("错误：Bascontractitem对象为null，无法执行更新");
                renderJson(Result.badRequest("物料信息不能为空"));
                return;
            }

            Integer itemId = Math.toIntExact(bascontractitem.getId());
            System.out.println("准备验证物料ID: " + itemId);
            if (itemId == null) {
                System.out.println("错误：物料ID为null");
                renderJson(Result.badRequest("物料ID不能为空"));
                return;
            }
            if (itemId <= 0) {
                System.out.println("错误：物料ID为非正数，值为: " + itemId);
                renderJson(Result.badRequest("物料ID必须为正整数"));
                return;
            }

            // 执行更新操作
            System.out.println("开始执行更新操作，物料ID: " + itemId);
            boolean success = tongzhiService.updateitem(bascontractitem);

            if (success) {
                System.out.println("更新成功，物料ID: " + itemId);
                renderJson(Result.success("物料更新成功"));

                // 处理备料计划数据
                String noticeid = bascontractitem.getNoticeid();
                String noticedrawno = bascontractitem.getNoticedrawno();
                System.out.println("更新成功后，准备处理备料计划: noticeid=" + noticeid + ", noticedrawno=" + noticedrawno);

                if (noticeid != null && !noticeid.trim().isEmpty() &&
                        noticedrawno != null && !noticedrawno.trim().isEmpty()) {

                    BeiliaojihuaController beiliaoController = new BeiliaojihuaController();
                    boolean exists = beiliaoController.checkExists(noticeid, noticedrawno);
                    System.out.println("备料计划数据是否已存在: " + (exists ? "是" : "否"));

                    if (!exists) {
                        List<Record> resultList = tongzhiService.getBeiliaoData(noticeid, noticedrawno);
                        System.out.println("从数据库查询到的备料计划数据条数: " + (resultList != null ? resultList.size() : 0));

                        for (Record record : resultList) {
                            Plbeiliaojihua beiliaojihua = convertToPlbeiliaojihua(record);
                            beiliaoController.save(beiliaojihua);
                            System.out.println("已保存备料计划数据: " + beiliaojihua);
                        }
                    }
                } else {
                    System.out.println("noticeid或noticedrawno为空，不执行备料计划处理");
                }
            } else {
                System.out.println("更新失败，数据库操作未成功执行，物料ID: " + itemId);
                renderJson(Result.serverError("更新物料失败"));
            }
        } catch (NumberFormatException e) {
            // 捕获数字格式异常，打印详细信息
            System.out.println("===== 发生NumberFormatException =====");
            System.out.println("错误描述: " + e.getMessage());
            System.out.println("错误位置: ");
            e.printStackTrace();
            System.out.println("触发异常的物料ID: " + (bascontractitem != null ? bascontractitem.getId() : "null"));
            renderJson(Result.badRequest("物料ID格式错误: " + e.getMessage()));
        } catch (Exception e) {
            // 捕获其他所有异常
            System.out.println("===== 发生未知异常 =====");
            System.out.println("错误描述: " + e.getMessage());
            System.out.println("错误位置: ");
            e.printStackTrace();
            System.out.println("异常发生时的物料信息: " + bascontractitem);
            renderJson(Result.serverError("更新物料时发生错误: " + e.getMessage()));
        } finally {
            System.out.println("===== updateitem 方法执行结束 =====");
        }
    }

    // 将查询结果转换为Plbeiliaojihua对象
    private Plbeiliaojihua convertToPlbeiliaojihua(Record record) {
        Plbeiliaojihua beiliaojihua = new Plbeiliaojihua();

        beiliaojihua.setContractno(record.getStr("contractno"));
        beiliaojihua.setXiaoshouitemid(record.getInt("xiaoshouitemid"));
        beiliaojihua.setContractname(record.getStr("contractname"));
        beiliaojihua.setDaiyongxinghao(record.getStr("daiyongxinghao"));
        beiliaojihua.setDinghuotaoshu(record.getStr("dinghuotaoshu"));
        beiliaojihua.setItemno(record.getStr("itemno"));
        beiliaojihua.setNoticedrawno(record.getStr("noticedrawno"));
        beiliaojihua.setNoticeid(record.getStr("noticeid"));
        beiliaojihua.setSxclshuliang(record.getStr("sxclshuliang"));
        beiliaojihua.setSxclitemno(record.getStr("sxclitemno"));

        // 设置其他必要字段


        return beiliaojihua;
    }

    //按通知生产提料单开始
    @ActionKey("/tongzhi/gettongzhipage")
    @HttpMethod("GET")
    public void gettongzhipage() {
        // 获取参数
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String noticeid = getPara("noticeid");
        String noticename = getPara("noticename");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Record> page = tongzhiService.gettongzhipage(pageNum, pageSz, noticeid, noticename);
            renderJson(Result.success("查询通知列表成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }



    @ActionKey("/tongzhi/getshenhetongzhipage")
    @HttpMethod("GET")
    public void getshenhetongzhipage() {
        // 获取参数
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String noticeid = getPara("noticeid");
        String noticename = getPara("noticename");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Record> page = tongzhiService.getshenhetongzhipage(pageNum, pageSz, noticeid, noticename);
            renderJson(Result.success("查询通知列表成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }
    //按通知生产提料单开始
    @ActionKey("/tongzhi/getshenhehoutongzhipage")
    @HttpMethod("GET")
    public void getshenhehoutongzhipage() {
        // 获取参数
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String noticeid = getPara("noticeid");
        String noticename = getPara("noticename");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Record> page = tongzhiService.getshenhehoutongzhipage(pageNum, pageSz, noticeid, noticename);
            renderJson(Result.success("查询通知列表成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }
    //按通知生产提料单结束

    //按照通知编号查询所有的通知内容
    @ActionKey("/tongzhi/gettongzhibyid")
    @HttpMethod("GET")
    public void gettongzhibyid() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String noticeid = getPara("noticeid");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            if (noticeid == null || noticeid.trim().isEmpty()) {
                renderJson(Result.badRequest("noticeid 不能为空"));
                return;
            }

            Page<Record> page = tongzhiService.gettongzhibyid(pageNum, pageSz, noticeid);
            renderJson(Result.success("查询通知信息成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }


// 确认通知接口
    @ActionKey("/tongzhi/querentongzhi")
    @HttpMethod("GET")
    public void querentongzhi() {
        // 测试接口：http://localhost:8099/tongzhi/querentongzhi
        String noticeid = getPara("noticeid");
        System.out.println("noticeid:"+noticeid);
        if (noticeid == null || noticeid.trim().isEmpty()) {
            renderJson(Result.badRequest("noticeid 不能为空"));
            return;
        }
        boolean success = tongzhiService.querentongzhi(noticeid);
        if (success) {
            renderJson(Result.success("确认通知成功"));
        } else {
            renderJson(Result.serverError("确认通知失败"));
        }
    }

    // 反确认通知接口
    @ActionKey("/tongzhi/fanquerentongzhi")
    @HttpMethod("GET")
    public void fanquerentongzhi() {
        // 测试接口：http://localhost:8099/tongzhi/fanquerentongzih
        String noticeid = getPara("noticeid");
        if (noticeid == null || noticeid.trim().isEmpty()) {
            renderJson(Result.badRequest("noticeid 不能为空"));
            return;
        }
        boolean success = tongzhiService.fanquerentongzih(noticeid);
        if (success) {
            renderJson(Result.success("反确认通知成功"));
        } else {
            renderJson(Result.serverError("反确认通知失败"));
        }
    }

    // 校验通知接口
    @ActionKey("/tongzhi/jiaoyantongzhi")
    @HttpMethod("GET")
    public void jiaoyantongzhi() {
        String noticedeliver = getPara("noticedeliver");
        String noticeid = getPara("noticeid");
        if (noticeid == null || noticeid.trim().isEmpty()) {
            renderJson(Result.badRequest("noticeid 不能为空"));
            return;
        }
        boolean success = tongzhiService.jiaoyantongzhi(noticedeliver, noticeid);
        if (success) {
            renderJson(Result.success("校验通知成功"));
        } else {
            renderJson(Result.serverError("校验通知失败"));
        }
    }

    // 反校验通知接口
    @ActionKey("/tongzhi/fanjiaoyantongzhi")
    @HttpMethod("GET")
    public void fanjiaoyantongzhi() {
        String noticeid = getPara("noticeid");
        if (noticeid == null || noticeid.trim().isEmpty()) {
            renderJson(Result.badRequest("noticeid 不能为空"));
            return;
        }
        boolean success = tongzhiService.fanjiaoyantongzhi(noticeid);
        if (success) {
            renderJson(Result.success("反校验通知成功"));
        } else {
            renderJson(Result.serverError("反校验通知失败"));
        }
    }

    // 审核通知接口
    @ActionKey("/tongzhi/shenhetongzhi")
    @HttpMethod("GET")
    public void shenhetongzhi() {
        // 测试接口：http://localhost:8099/tongzhi/shenhetongzhi
        String noticeshenhe = getPara("noticeshenhe");
        String noticeid = getPara("noticeid");
        if (noticeid == null || noticeid.trim().isEmpty()) {
            renderJson(Result.badRequest("noticeid 不能为空"));
            return;
        }
        boolean success = tongzhiService.shenhetongzhi(noticeshenhe, noticeid);
        if (success) {
            renderJson(Result.success("审核通知成功"));
        } else {
            renderJson(Result.serverError("审核通知失败"));
        }
    }

    // 反审核通知接口
    @ActionKey("/tongzhi/fanshenhetongzhi")
    @HttpMethod("GET")
    public void fanshenhtongzhi() {
        String noticeid = getPara("noticeid");
        if (noticeid == null || noticeid.trim().isEmpty()) {
            renderJson(Result.badRequest("noticeid 不能为空"));
            return;
        }
        boolean success = tongzhiService.fanshenhtongzhi(noticeid);
        if (success) {
            renderJson(Result.success("反审核通知成功"));
        } else {
            renderJson(Result.serverError("反审核通知失败"));
        }
    }


}