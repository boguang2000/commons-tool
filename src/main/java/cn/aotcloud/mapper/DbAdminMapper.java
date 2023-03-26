package cn.aotcloud.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

@Repository
public interface DbAdminMapper {

	@Select("${sql}")
	List<Map<String, Object>> query(@Param("sql") String sql);
	
	@Select("${sql}")
	IPage<Map<String, Object>> queryPage(IPage<String> page, @Param("sql") String sql);
	
	@Insert("${sql}")
	int insert(@Param("sql") String sql);
	
	@Update("${sql}")
	int update(@Param("sql") String sql);
	
	@Delete("${sql}")
	int delete(@Param("sql") String sql);
    
}