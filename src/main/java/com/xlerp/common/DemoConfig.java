package com.xlerp.common;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.config.*;
import com.jfinal.core.Controller;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.AnsiSqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;
import com.xlerp.api.ClManagement.Controller.*;
import com.xlerp.api.Common.FileUploadController;
import com.xlerp.api.Common.SQLDruidFilter;
import com.xlerp.api.Contract.Controller.BasContractController;
import com.xlerp.api.Contract.Controller.BasPurchaseOrderController;
import com.xlerp.api.HrManagement.Controller.HruserController;
import com.xlerp.api.ItemManagement.Controller.BasItemClassController;
import com.xlerp.api.ItemManagement.Controller.BasItemController;
import com.xlerp.api.PLchuchangchoujian.Controller.PlchuchangchoujianController;
import com.xlerp.api.PlInspectionController.Controller.InspItemController;
import com.xlerp.api.PlInspectionController.Controller.InspStandardController;
import com.xlerp.api.PlInspectionController.Controller.InspStdItemController;
import com.xlerp.api.PlManagement.Controller.PlentityIdController;
import com.xlerp.api.PlProductionOrder.Controller.PlProductionOrderController;
import com.xlerp.api.PlReportWorkOrder.Controller.PlReportWorkOrderController;
import com.xlerp.api.PlSchedulePlan.Controller.PlSchedulePlanController;
import com.xlerp.api.PlStoreInout.Controller.matInoutController;
import com.xlerp.api.PlWorkOrder.Controller.PlWorkOrderController;
import com.xlerp.api.System.Controller.*;
import com.xlerp.api.Tongzhi.Controller.BeiliaojihuaController;
import com.xlerp.api.Tongzhi.Controller.TongzhiController;
import com.xlerp.api.Tuzhi.Controller.TuzhiController;
import com.xlerp.api.Tuzhi.Controller.TuzhicailiaoController;
import com.xlerp.common.model._MappingKit;

public class DemoConfig extends JFinalConfig {

	static Prop p;

	/**
	 * 启动入口，运行此 main 方法可以启动项目，此 main 方法可以放置在任意的 Class 类定义中，不一定要放于此
	 */
	public static void main(String[] args) {
		UndertowServer.start(DemoConfig.class);
	}

	/**
	 * PropKit.useFirstFound(...) 使用参数中从左到右最先被找到的配置文件
	 * 从左到右依次去找配置，找到则立即加载并立即返回，后续配置将被忽略
	 */
	static void loadConfig() {
		if (p == null) {
			p = PropKit.useFirstFound("demo-config-pro.txt", "demo-config-dev.txt");
		}
	}

	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		loadConfig();

		me.setDevMode(p.getBoolean("devMode", false));
//		com.jfinal.plugin.activerecord.DbPro.;
		/**
		 * 支持 Controller、Interceptor、Validator 之中使用 @Inject 注入业务层，并且自动实现 AOP
		 * 注入动作支持任意深度并自动处理循环注入
		 */
		me.setInjectDependency(true);

		// 配置对超类中的属性进行注入
		me.setInjectSuperClass(true);
		me.setResolveJsonRequest(true);
		// 配置上传文件大小限制，设置为100MB
		me.setMaxPostSize(100 * 1024 * 1024);  // 100MB
		// 或者如果需要更大的限制，比如500MB
		// me.setMaxPostSize(500 * 1024 * 1024);  // 500MB
	}

	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		// SPA 路由
		me.add("/erp", IndexController.class); // 核心修正：controllerKey 为 /erp，无多余参数


		// 使用 jfinal 4.9.03 新增的路由扫描功能
		//接口
		me.add("/user", UserController.class);

//		me.add("/role", RoleController.class);
		me.add("/menu", MenuController.class);
		me.add("/usermenu", UserMenuController.class);
		me.add("/api/upload", FileUploadController.class);

		me.add("/login", LoginController.class);
		me.add("/basorg", BasOrgController.class);
		me.add("/basitem",  BasItemController.class);
		me.add("/basdepartment", BasDepartmentController.class);
		me.add("/bascontract", BasContractController.class);
		me.add("/term", TermController.class);
		me.add("/hruser", HruserController.class);
		me.add("/baspurchaseorder", BasPurchaseOrderController.class);
		me.add("/bastuzhi", TuzhiController.class);  //刘国奇，基本图纸管理
		me.add("/bastuzhicailiao", TuzhicailiaoController.class);  //刘国奇，基本图纸材料管理，根据图纸 id，获取这个图纸所有的材料信息
		me.add("/tongzhi", TongzhiController.class);  //刘国奇，通知管理，这个是第一个功能，获取合同列表，但是是确认状态以上的合同
		me.add("/beiliaojihua", BeiliaojihuaController.class);  //刘国奇，通知管理，这个是第一个功能，获取合同列表，但是是确认状态以上的合同
		me.add("/plentityid", PlentityIdController.class);//实物ID

		me.add("/pl_schedule_plan", PlSchedulePlanController.class);//毛文斌，排产计划管理
		me.add("/pl_production_order", PlProductionOrderController.class);//毛文斌，生产订单管理
		me.add("/pl_work_order", PlWorkOrderController.class);//毛文斌，生产工单管理
		me.add("/pl_report_work_order", PlReportWorkOrderController.class);//毛文斌，报工单管理
		me.add("/pl_mat_inout", matInoutController.class);





		me.add("/clproductiondata", ClProductionDataController.class);

		me.add("/basno", BasNoController.class);//编号管理


		me.add("/plchuchangjianyan", PlchuchangchoujianController.class);

 		me.add("/cl_ld", LdController.class);//铝锭材料检验


		me.add("/cl_wfg", WfgController.class);//无缝钢板--倪佳琪
		me.add("/cl_gb", GbController.class);//钢板材料检验--谭请赢
		me.add("/cl_yg", YgController.class);//圆钢--谭请赢
		me.add("/cl_xj", XjController.class);//刘国奇，增加，橡胶检测功能
		me.add("/cl_bkx", BkxController.class);//闭口销--谭请赢

		me.add("/cl_lhjx", LhjxController.class);//铝合金线--蒙千惠
		me.add("/cl_tb", TbController.class);//张凌佳 --铜板
		me.add("/cl_dxgjx", DxgjxController.class);//镀锌钢绞线--那怀月
		me.add("/cl_lb", LbController.class);//铝板--孙元芯
		me.add("/cl_dxls", DxlsController.class);//镀锌螺栓--蒙千惠
		me.add("/cl_ct", CtController.class);//锤头--张凌佳
		me.add("/cl_lg", LgController.class);//铝管--那怀月
		me.add("/cl_bxgls", BxglsController.class);//不锈钢螺栓--蒙千惠
		me.add("/uploadLog", UploadLogController.class);//上传日志--毛文斌
		me.add("/basitemclass", BasItemClassController.class); //物料分类管理
		me.add("/insp_item", InspItemController.class);//检验项目管理--毛文斌
		me.add("/insp_std", InspStandardController.class);//检验标准管理--毛文斌
		me.add("/insp_std_item", InspStdItemController.class);//检验标准明细管理--毛文斌



	}

	public void configEngine(Engine me) {
		//me.addSharedFunction("/common/_layout.html");
		//me.addSharedFunction("/common/_paginate.html");
	}

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		// 配置 druid 数据库连接池插件
		DruidPlugin druidPlugin = createDruidPlugin();
		me.add(druidPlugin);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
		arp.setDialect(new AnsiSqlDialect());//设置达梦的方言
		druidPlugin.addFilter(new SQLDruidFilter());//完整显示SQL语句
		_MappingKit.mapping(arp);
		me.add(arp);
	}


	public static DruidPlugin createDruidPlugin() {
		loadConfig();

		return new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password"));
	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		// 保留现有的 CorsInterceptor
		me.addGlobalActionInterceptor(new CorsInterceptor());
//		me.add(new JwtInterceptor());//是否开启token验证，注释掉就不要token验证了
		// 添加新的 LogInterceptor
//		me.addGlobalActionInterceptor(new LogInterceptor());

	}

	public class CorsInterceptor implements Interceptor {
		@Override
		public void intercept(Invocation inv) {
			Controller controller = inv.getController();
			controller.getResponse().setHeader("Access-Control-Allow-Origin", "*");
			controller.getResponse().setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
			controller.getResponse().setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
			controller.getResponse().setHeader("Access-Control-Allow-Credentials", "true");
			controller.getResponse().setHeader("Access-Control-Max-Age", "3600");

			if ("OPTIONS".equalsIgnoreCase(controller.getRequest().getMethod())) {
				controller.renderNull();
				return;
			}
			inv.invoke();
		}
	}

	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {


	}

	public static class IndexController extends Controller {
		public void index() {
			System.out.println("跳转");
			render("/erp/index.html");
		}
	}
}