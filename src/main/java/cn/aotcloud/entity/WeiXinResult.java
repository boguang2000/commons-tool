package cn.aotcloud.entity;

import java.io.Serializable;

/**
 * @author xkxu
 */
public class WeiXinResult implements Serializable {
	
	private static final long serialVersionUID = 1L;

    /**
     * 错误码以errcode是否为0判断是否调用成功
     */
    private Integer errcode;

    /**
     * 错误说明
     */
    private String errmsg;
    
    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
    
	public String localErrMsg() {
		return localErrMsg(this.errmsg);
	}
	
	public String localErrMsg(String errMsg) {
		WexinErrorCode ec = WexinErrorCode.valueOfCode(errcode);
		return ec == null ? errMsg : ec.errMsg();
	}

	/**
     * 
     * @return
     */
    public static WeiXinResult successResult(){
    	WeiXinResult result = new WeiXinResult();
    	result.setErrcode(0);
    	result.setErrmsg("ok");
    	return result;
    }

    /**
     * 判断请求是否成功
     *
     *
     * @return
     */
   public boolean isSuccess() {
        //if ("0".equals(errcode)) {
        if (errcode == 0) {
            return true;
        } else {
            return false;
        }
    }
   
   /**
    * 企信消息事件类型
    */
   public enum WexinErrorCode {
   	//在原企信错误的吗的基础上加上字符串E
   	
   	E_1(-1,"系统繁忙"),
   	E0(0,"请求成功"),
   	E40001(40001,"不合法的secret参数"),
   	E40056(40056,"不合法的agentid"),
   	E40057(40057,"不合法的callbackurl或者callbackurl验证失败"),
   	E46003(46003,"菜单未设置"),
   	E94000(94000,"应用未开启工作台自定义模式");

   	private final String errMsg;
   	
   	private final Integer code;

   	private WexinErrorCode(Integer code,String message) {
   		this.code = code;
   		this.errMsg = message;
   	}
   	
   	public String errMsg() {
   		return this.errMsg;
   	}
   	
   	public int errCode() {
   		return this.code;
   	}
   	
   	public static WexinErrorCode valueOfCode(Integer code) {
   		String ecode = "E"+String.valueOf(code).replace("-","_");
   		return WexinErrorCode.valueOf(ecode);
   	}
   	
   }

}


