package com.xlerp.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseBasPurchaseOrder<M extends BaseBasPurchaseOrder<M>> extends Model<M> implements IBean {

	/**
	 * 自增主键ID
	 */
	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	/**
	 * 自增主键ID
	 */
	public java.lang.Integer getId() {
		return getInt("id");
	}
	
	/**
	 * 采购订单编码
	 */
	public void setPoNo(java.lang.String poNo) {
		set("poNo", poNo);
	}
	
	/**
	 * 采购订单编码
	 */
	public java.lang.String getPoNo() {
		return getStr("poNo");
	}
	
	/**
	 * 采购订单行项目号
	 */
	public void setPoItemNo(java.lang.String poItemNo) {
		set("poItemNo", poItemNo);
	}
	
	/**
	 * 采购订单行项目号
	 */
	public java.lang.String getPoItemNo() {
		return getStr("poItemNo");
	}
	
	/**
	 * 采购订单行项目id
	 */
	public void setPoItemId(java.lang.String poItemId) {
		set("poItemId", poItemId);
	}
	
	/**
	 * 采购订单行项目id
	 */
	public java.lang.String getPoItemId() {
		return getStr("poItemId");
	}
	
	/**
	 * 种类编码
	 */
	public void setSubclassCode(java.lang.String subclassCode) {
		set("subclassCode", subclassCode);
	}
	
	/**
	 * 种类编码
	 */
	public java.lang.String getSubclassCode() {
		return getStr("subclassCode");
	}
	
	/**
	 * 种类名称
	 */
	public void setSubclassName(java.lang.String subclassName) {
		set("subclassName", subclassName);
	}
	
	/**
	 * 种类名称
	 */
	public java.lang.String getSubclassName() {
		return getStr("subclassName");
	}
	
	/**
	 * 合同序号
	 */
	public void setConCode(java.lang.String conCode) {
		set("conCode", conCode);
	}
	
	/**
	 * 合同序号
	 */
	public java.lang.String getConCode() {
		return getStr("conCode");
	}
	
	/**
	 * 合同名称
	 */
	public void setConName(java.lang.String conName) {
		set("conName", conName);
	}
	
	/**
	 * 合同名称
	 */
	public java.lang.String getConName() {
		return getStr("conName");
	}
	
	/**
	 * 采购方公司名称
	 */
	public void setBuyerName(java.lang.String buyerName) {
		set("buyerName", buyerName);
	}
	
	/**
	 * 采购方公司名称
	 */
	public java.lang.String getBuyerName() {
		return getStr("buyerName");
	}
	
	/**
	 * 采购方公司编码
	 */
	public void setBuyerCode(java.lang.String buyerCode) {
		set("buyerCode", buyerCode);
	}
	
	/**
	 * 采购方公司编码
	 */
	public java.lang.String getBuyerCode() {
		return getStr("buyerCode");
	}
	
	/**
	 * 采购方物料编码
	 */
	public void setMaterialCode(java.lang.String materialCode) {
		set("materialCode", materialCode);
	}
	
	/**
	 * 采购方物料编码
	 */
	public java.lang.String getMaterialCode() {
		return getStr("materialCode");
	}
	
	/**
	 * 采购方物料描述
	 */
	public void setMaterialDesc(java.lang.String materialDesc) {
		set("materialDesc", materialDesc);
	}
	
	/**
	 * 采购方物料描述
	 */
	public java.lang.String getMaterialDesc() {
		return getStr("materialDesc");
	}
	
	/**
	 * 采购数量
	 */
	public void setAmount(java.lang.String amount) {
		set("amount", amount);
	}
	
	/**
	 * 采购数量
	 */
	public java.lang.String getAmount() {
		return getStr("amount");
	}
	
	/**
	 * 合同序号
	 */
	public void setSellerConCode(java.lang.String sellerConCode) {
		set("sellerConCode", sellerConCode);
	}
	
	/**
	 * 合同序号
	 */
	public java.lang.String getSellerConCode() {
		return getStr("sellerConCode");
	}
	
	/**
	 * 技术规范流水号
	 */
	public void setSerialNumber(java.lang.String serialNumber) {
		set("serialNumber", serialNumber);
	}
	
	/**
	 * 技术规范流水号
	 */
	public java.lang.String getSerialNumber() {
		return getStr("serialNumber");
	}
	
	/**
	 * 合同签订日期
	 */
	public void setSellerSignTime(java.lang.String sellerSignTime) {
		set("sellerSignTime", sellerSignTime);
	}
	
	/**
	 * 合同签订日期
	 */
	public java.lang.String getSellerSignTime() {
		return getStr("sellerSignTime");
	}
	
	/**
	 * 合同类型
	 */
	public void setConType(java.lang.Integer conType) {
		set("conType", conType);
	}
	
	/**
	 * 合同类型
	 */
	public java.lang.Integer getConType() {
		return getInt("conType");
	}
	
	/**
	 * 项目序号
	 */
	public void setPrjCode(java.lang.String prjCode) {
		set("prjCode", prjCode);
	}
	
	/**
	 * 项目序号
	 */
	public java.lang.String getPrjCode() {
		return getStr("prjCode");
	}
	
	/**
	 * 工程项目名称
	 */
	public void setPrjName(java.lang.String prjName) {
		set("prjName", prjName);
	}
	
	/**
	 * 工程项目名称
	 */
	public java.lang.String getPrjName() {
		return getStr("prjName");
	}
	
	/**
	 * 物资编码
	 */
	public void setMatCode(java.lang.String matCode) {
		set("matCode", matCode);
	}
	
	/**
	 * 物资编码
	 */
	public java.lang.String getMatCode() {
		return getStr("matCode");
	}
	
	/**
	 * 采购技术固化ID
	 */
	public void setFixedTechId(java.lang.String fixedTechId) {
		set("fixedTechId", fixedTechId);
	}
	
	/**
	 * 采购技术固化ID
	 */
	public java.lang.String getFixedTechId() {
		return getStr("fixedTechId");
	}
	
	/**
	 * 合同包号
	 */
	public void setPkgNo(java.lang.String pkgNo) {
		set("pkgNo", pkgNo);
	}
	
	/**
	 * 合同包号
	 */
	public java.lang.String getPkgNo() {
		return getStr("pkgNo");
	}
	
	/**
	 * 招标批次号
	 */
	public void setBidBatCode(java.lang.String bidBatCode) {
		set("bidBatCode", bidBatCode);
	}
	
	/**
	 * 招标批次号
	 */
	public java.lang.String getBidBatCode() {
		return getStr("bidBatCode");
	}
	
	/**
	 * 固化ID描述
	 */
	public void setExtDes(java.lang.String extDes) {
		set("extDes", extDes);
	}
	
	/**
	 * 固化ID描述
	 */
	public java.lang.String getExtDes() {
		return getStr("extDes");
	}
	
	/**
	 * 物资大类编码
	 */
	public void setMatMaxCode(java.lang.String matMaxCode) {
		set("matMaxCode", matMaxCode);
	}
	
	/**
	 * 物资大类编码
	 */
	public java.lang.String getMatMaxCode() {
		return getStr("matMaxCode");
	}
	
	/**
	 * 物资中类编码
	 */
	public void setMatMedCode(java.lang.String matMedCode) {
		set("matMedCode", matMedCode);
	}
	
	/**
	 * 物资中类编码
	 */
	public java.lang.String getMatMedCode() {
		return getStr("matMedCode");
	}
	
	/**
	 * 物资小类编码
	 */
	public void setMatMinCode(java.lang.String matMinCode) {
		set("matMinCode", matMinCode);
	}
	
	/**
	 * 物资小类编码
	 */
	public java.lang.String getMatMinCode() {
		return getStr("matMinCode");
	}
	
	/**
	 * 物资大类名称
	 */
	public void setMatMaxName(java.lang.String matMaxName) {
		set("matMaxName", matMaxName);
	}
	
	/**
	 * 物资大类名称
	 */
	public java.lang.String getMatMaxName() {
		return getStr("matMaxName");
	}
	
	/**
	 * 物资中类名称
	 */
	public void setMatMedName(java.lang.String matMedName) {
		set("matMedName", matMedName);
	}
	
	/**
	 * 物资中类名称
	 */
	public java.lang.String getMatMedName() {
		return getStr("matMedName");
	}
	
	/**
	 * 物资小类名称
	 */
	public void setMatMinName(java.lang.String matMinName) {
		set("matMinName", matMinName);
	}
	
	/**
	 * 物资小类名称
	 */
	public java.lang.String getMatMinName() {
		return getStr("matMinName");
	}
	
	/**
	 * 更新时间
	 */
	public void setModifyTime(java.lang.String modifyTime) {
		set("modifyTime", modifyTime);
	}
	
	/**
	 * 更新时间
	 */
	public java.lang.String getModifyTime() {
		return getStr("modifyTime");
	}
	
	/**
	 * 创建时间
	 */
	public void setCreateTime(java.util.Date createTime) {
		set("createTime", createTime);
	}
	
	/**
	 * 创建时间
	 */
	public java.util.Date getCreateTime() {
		return getDate("createTime");
	}
	
	/**
	 * 最后更新时间
	 */
	public void setUpdateTime(java.util.Date updateTime) {
		set("updateTime", updateTime);
	}
	
	/**
	 * 最后更新时间
	 */
	public java.util.Date getUpdateTime() {
		return getDate("updateTime");
	}
	
	/**
	 * 删除标记
	 */
	public void setIsdelete(java.lang.Integer isdelete) {
		set("isdelete", isdelete);
	}
	
	/**
	 * 删除标记
	 */
	public java.lang.Integer getIsdelete() {
		return getInt("isdelete");
	}
	
}

