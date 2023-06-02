package cn.aotcloud.crypto.sm;

import cn.aotcloud.crypto.DefaultRandomStringGenerator;
import cn.aotcloud.crypto.EncryptionProperties;

import cn.aotcloud.smcrypto.Sm2Utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 配合前端JS加密传输功能，先SM3消息摘要，再采用SM2加密。
 *
 * @author xkxu
 */
public class SM2ToSM3TextEncryptor extends SM2TextEncryptor {

    /**
     * 控制解密后是否发送SM3消息摘要字符串，如果为false，则返回明文。
     */
    protected boolean toSM3 = true;

    protected SM3TextEncryptor sm3TextEncryptor = new SM3TextEncryptor();

    protected DefaultRandomStringGenerator randomStringGenerator = new DefaultRandomStringGenerator(8);

    /**
     * 该构造方法已过期，请修改
     *
     * @param sm2Utils
     * @param encryptionProperties
     */
    @Deprecated
    public SM2ToSM3TextEncryptor(Sm2Utils sm2Utils, EncryptionProperties encryptionProperties) {
        this(sm2Utils, encryptionProperties, true);
    }

    /**
     * 该构造方法已过期，请修改
     *
     * @param sm2Utils
     * @param encryptionProperties
     * @param toSM3
     */
    @Deprecated
    public SM2ToSM3TextEncryptor(Sm2Utils sm2Utils, EncryptionProperties encryptionProperties, boolean toSM3) {
        super(sm2Utils, encryptionProperties);
        this.toSM3 = toSM3;
    }

    /**
     * 该构造方法已过期，请修改
     *
     * @param sm2Utils
     * @param pubKeyHex
     * @param prvKeyHex
     */
    @Deprecated
    public SM2ToSM3TextEncryptor(Sm2Utils sm2Utils, String pubKeyHex, String prvKeyHex) {
        super(sm2Utils, pubKeyHex, prvKeyHex);
    }

    /**
     * 该构造方法已过期，请修改
     *
     * @param sm2Utils
     * @param pubKeyHex
     * @param prvKeyHex
     * @param toSM3
     */
    @Deprecated
    public SM2ToSM3TextEncryptor(Sm2Utils sm2Utils, String pubKeyHex, String prvKeyHex, boolean toSM3) {
        super(sm2Utils, pubKeyHex, prvKeyHex);
        this.toSM3 = toSM3;
    }

    public SM2ToSM3TextEncryptor(EncryptionProperties encryptionProperties) {
        this(encryptionProperties, true);
    }

    public SM2ToSM3TextEncryptor(EncryptionProperties encryptionProperties, boolean toSM3) {
        super(encryptionProperties);
        this.toSM3 = toSM3;
    }

    public SM2ToSM3TextEncryptor(String pubKeyHex, String prvKeyHex) {
        super(pubKeyHex, prvKeyHex);
    }

    public SM2ToSM3TextEncryptor(String pubKeyHex, String prvKeyHex, boolean toSM3) {
        super(pubKeyHex, prvKeyHex);
        this.toSM3 = toSM3;
    }

    @Override
    public String encrypt(String text) {
        return super.encrypt(encryptInternal(text));
    }

    /**
     * @param text 明文
     * @return SM3(明文) + | + 8位随机字符串 + | + 明文
     */
    protected String encryptInternal(String text) {
        return sm3Encrypt(text) + "|" + randomStringGenerator.getNewString() + "|" + text;
    }

    @Override
    public String decrypt(String encryptedText) {
        return sm2DecryptAndSm3VerifyRP(encryptedText);
    }

    /**
     * SM2算法解密
     *
     * @param encryptedText SM2解密后的密文
     * @return 解密后的字符串
     */
    public String decryptWithSM2(String encryptedText) {
        return super.decrypt(encryptedText);
    }

    /**
     * 通过自定义防篡改协议密文串校验并获取原始SM3签名串
     *
     * @param cipherText    自定义防篡改加密串: SM2(SM3(明文) + | + 8位随机字符串 + | + 明文)
     * @param privateKeyHex 私钥16进制字符串
     * @return 原始密码的SM3加密串
     */
    public String sm2DecryptAndSm3VerifyRP(String cipherText) {
        return sm2DecryptAndSm3VerifyRP(cipherText, "\\|");
    }

    /**
     * 通过自定义防篡改协议密文串校验并获取原始SM3签名串
     *
     * @param cipherText    自定义防篡改加密串: SM2(SM3(明文) + 分隔符 + 8位随机字符串 + 分隔符 + 明文)
     * @param privateKeyHex 私钥16进制字符串
     * @param split         分隔符
     * @return 原始密码的SM3加密串
     */
    public String sm2DecryptAndSm3VerifyRP(String cipherText, String split) {
        if (StringUtils.isBlank(cipherText) || StringUtils.isBlank(split)) {
            return null;
        }
        String sec_p_sm3_random_plain = decryptWithSM2(cipherText);
        int sec_p_sm3_index = sec_p_sm3_random_plain.indexOf("|", 1);
        int sec_p_random_index = sec_p_sm3_random_plain.indexOf("|", sec_p_sm3_index + 1);
        String sec_p_sm3 = sec_p_sm3_random_plain.substring(0, sec_p_sm3_index);
        String sec_p_random = sec_p_sm3_random_plain.substring(sec_p_sm3_index + 1, sec_p_random_index);
        String sec_p_plain = sec_p_sm3_random_plain.substring(sec_p_random_index + 1, sec_p_sm3_random_plain.length());
//		旧的方式，统一配置处带有 | 的yml 解密会出错
//		String[] sec_p_array = sec_p_sm3_random_plain.split(split);
//		String sec_p_sm3 = sec_p_array[0];
//		String sec_p_random = sec_p_array[1];
//		String sec_p_plain = sec_p_array[2];
        String plainToSm3 = null;
        // 判空
        if (StringUtils.isNotEmpty(sec_p_sm3) && StringUtils.isNotEmpty(sec_p_plain)
                && StringUtils.isNotEmpty(sec_p_random)) {
            plainToSm3 = sm3Encrypt(sec_p_plain);
            // 防篡改验证及
            if (!sec_p_sm3.equalsIgnoreCase(plainToSm3)) {
                plainToSm3 = null;
            } else {
                if (toSM3) {
                    return plainToSm3;
                } else {
                    return sec_p_plain;
                }
            }
        }
        return null;
    }

    protected String sm3Encrypt(String text) {
        return sm3TextEncryptor.encrypt(text);
    }
}
