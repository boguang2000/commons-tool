package cn.aotcloud.crypto.sm;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

/**
 * @author xkxu
 */
public class JdbcSmKeyLoader implements SmKeyLoader {
	
	private JdbcTemplate jdbcTemplate;
	

	public JdbcSmKeyLoader(JdbcTemplate jdbcTemplate) {
		Assert.notNull(jdbcTemplate, "jdbcTemplate is null.");
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public String getSm2PrivateKey(String prefix) {
		String suffix = jdbcTemplate.queryForObject("SELECT VALUE FROM AC_SM_VALUE WHERE CODE = 'sm2'", String.class);
		return prefix + suffix;
	}

	@Override
	public String getSm4Key(String prefix) {
		String suffix = jdbcTemplate.queryForObject("SELECT VALUE FROM AC_SM_VALUE WHERE CODE = 'sm4'", String.class);
		return prefix + suffix;
	}

	@Override
	public String getSm4CbcIv(String prefix) {
		String suffix = jdbcTemplate.queryForObject("SELECT VALUE FROM AC_SM_VALUE WHERE CODE = 'sm4CbcIv'", String.class);
		return prefix + suffix;
	}

}
