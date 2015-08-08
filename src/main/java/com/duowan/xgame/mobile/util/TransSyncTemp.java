package com.duowan.xgame.mobile.util;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TransSyncTemp implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer seq;
	@Expose
	@SerializedName(value = "exorderno")
	private String exorderno;
	@Expose
	@SerializedName(value = "transid")
	private String transid;
	@Expose
	@SerializedName(value = "waresid")
	private String waresid;
	@Expose
	@SerializedName(value = "chargepoint")
	private Integer chargepoint;
	@Expose
	@SerializedName(value = "feetype")
	private Integer feetype;
	@Expose
	@SerializedName(value = "money")
	private Integer money;
	@Expose
	@SerializedName(value = "count")
	private Integer count;
	@Expose
	@SerializedName(value = "result")
	private Integer result;
	@Expose
	@SerializedName(value = "transtype")
	private Integer transtype;
	@Expose
	@SerializedName(value = "transtime")
	private String transtime;
	@Expose
	@SerializedName(value = "sign")
	private String sign;
	
	public TransSyncTemp() {
	}

	/**
	 * 新增渠道商channel信息
	 * @param cpOrderID
	 * @param transID
	 * @param waresId
	 * @param chargePoint
	 * @param feeType
	 * @param price
	 * @param result
	 * @param payAccount
	 * @param url
	 * @param finishDate
	 * @param quantity
	 * @param cpkey
	 * @return
	 */
	public TransSyncTemp(String cpOrderID, String transID, String waresId, Integer chargePoint, int feeType,
			Integer price, int result, Integer payAccount, String url, Date finishDate, int quantity, String cpkey) {
		setChargepoint(chargePoint);
		setExorderno(cpOrderID);
		setTransid(transID);
		setFeetype(feeType);
		setMoney(price);
		setResult(result);
		setTranstime(DateUtil.dateToString(finishDate, DateUtil.STRING_DATE_FORMAT));
		setTranstype(payAccount);
		setWaresid(waresId);
		setCount(quantity);
		/**
		 * sign
		 */
		StringBuilder sb = new StringBuilder();
		sb.append(null2EmptyString(getExorderno()));
		sb.append(null2EmptyString(getTransid()));
		sb.append(null2EmptyString(getWaresid()));
		sb.append(getChargepoint() == null ? "" : getChargepoint().intValue());
		sb.append(getFeetype() == null ? "" : getFeetype().intValue());
		sb.append(getMoney() == null ? "" : getMoney().intValue());
		sb.append(getCount() == null ? "" : getCount().intValue());
		sb.append(getResult() == null ? "" : getResult().intValue());
		sb.append(getTranstype() == null ? "" : getTranstype().intValue());
		sb.append(null2EmptyString(getTranstime()));
		sb.append(null2EmptyString(cpkey));
		setSign(MD5.md5Digest(sb.toString()));
	}

	private String null2EmptyString(String str) {
		if(str ==null){
			return "";
		}
		return str.trim();
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getExorderno() {
		return exorderno;
	}

	public void setExorderno(String exorderno) {
		this.exorderno = exorderno;
	}

	public String getTransid() {
		return transid;
	}

	public void setTransid(String transid) {
		this.transid = transid;
	}

	public String getWaresid() {
		return waresid;
	}

	public void setWaresid(String waresid) {
		this.waresid = waresid;
	}

	public Integer getChargepoint() {
		return chargepoint;
	}

	public void setChargepoint(Integer chargepoint) {
		this.chargepoint = chargepoint;
	}

	public Integer getFeetype() {
		return feetype;
	}

	public void setFeetype(Integer feetype) {
		this.feetype = feetype;
	}

	public Integer getMoney() {
		return money;
	}

	public void setMoney(Integer money) {
		this.money = money;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public Integer getTranstype() {
		return transtype;
	}

	public void setTranstype(Integer transtype) {
		this.transtype = transtype;
	}

	public String getTranstime() {
		return transtime;
	}

	public void setTranstime(String transtime) {
		this.transtime = transtime;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

}
