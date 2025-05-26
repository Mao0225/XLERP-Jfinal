package com.xlerp.common;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.config.*;
import com.jfinal.core.Controller;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;
import com.xlerp.api.Common.FileUploadController;
import com.xlerp.api.Common.JwtInterceptor;
import com.xlerp.api.Common.SQLDruidFilter;
import com.xlerp.api.Contract.Controller.BasContractController;
import com.xlerp.api.HrManagement.Controller.HruserController;
import com.xlerp.api.ItemManagement.Controller.BasItemController;
import com.xlerp.api.System.Controller.*;



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
}