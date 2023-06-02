package cn.aotcloud.crypto.sm.jni;

/**
 * @author xkxu
 */
public class SMNativeLicenseInit {

	/**
	 * 添加国密c语言实现版本license证书
	 */
	public static void setSMNativeLicenseProperties() {
		if (!System.getProperties().containsKey("license_mac")) {
			System.setProperty("license_mac", "00:00:00:00:00:00");
		}
		if (!System.getProperties().containsKey("license_ip")) {
			System.setProperty("license_ip",  "0.0.0.0");
		}
		if (!System.getProperties().containsKey("license_data")) {
			System.setProperty("license_data","3081AB022002F6A2DBE9E5DD3089614D40A63C714CDFCA2639E4485F8C436332776411169C022100C1986C2E0637FB58216CA40563A2E13960AAA285C5619ED6F8744C8E0ADAC53B042097F913A3B1F2B9E9D94E200FA36ACE821B2ED77A8D00F9CE25AC42A946BD69EF04423669673C5677702132A9CBFFF16478956198E76304D02ECFB8BA4C1618BEB3399E56C5A7C6C0D962A6A1E7B005D32FA08B8C9ACAEF8E4A6B1FDB8F046E4DB3C8AFE3");
		}
	}
}
