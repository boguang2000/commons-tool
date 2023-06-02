package cn.aotcloud.crypto.sm;

/**
 * 获得SM的密钥
 * 
 * @author xkxu
 *
 */
public interface SmKeyLoader {

	public String getSm2PrivateKey(String prefix);
	
	public String getSm4Key(String prefix);

	public String getSm4CbcIv(String prefix);

}
