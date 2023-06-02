package cn.aotcloud.exception;

public class ErrorCode {

    public static final ErrorCode ILLEGAL_ARGUMENT = new ErrorCode("appcenter-core-001", "非法参数。");

    public static final ErrorCode DATA_ACCESS_ERROR = new ErrorCode("appcenter-core-002", "数据访问异常。");

    public static final ErrorCode OBJECT_EXISTS = new ErrorCode("appcenter-core-003", "对象已存在。");

    public static final ErrorCode SYSTEM_ERROR = new ErrorCode("appcenter-core-004", "系统错误。");

    public static final ErrorCode ILLEGAL_PRIMARY_KEY = new ErrorCode("appcenter-core-005", "主键为空。");

    public static final ErrorCode DATA_DESTORY = new ErrorCode("appcenter-core-006", "数据完整性被破坏。");

    public static final ErrorCode CONFIG_ERROR = new ErrorCode("appcenter-core-007", "配置错误。");

    public static final ErrorCode NON_SUPPORT = new ErrorCode("appcenter-core-008", "不支持的操作。");

    /**
     * 异常编码，一般采用 “appcenter-user-001”这种格式。
     * <p>
     * appcenter是项目名称，user是功能模块名称，001是异常的序列号。
     */
    private final String code;

    /**
     * 异常编码的可视化描述信息
     */
    private final String message;

    /**
     * @param code    异常编码
     * @param message 异常编码的可视化描述信息
     */
    public ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}