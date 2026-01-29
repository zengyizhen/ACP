package org.example.coursework1.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PostgresTableUtil {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 检查表是否存在
    public boolean isTableExists(String tableName) {
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
        return count != null && count > 0;
    }

    // 获取指定表的所有数据
    public List<Map<String, Object>> getAllTableData(String tableName) {
        String sql = "SELECT * FROM \"" + tableName + "\"";
        return jdbcTemplate.queryForList(sql);
    }

    // 获取指定表的所有列名
    public List<String> getTableColumns(String tableName) {
        String sql = "SELECT column_name FROM information_schema.columns WHERE table_name = ?";
        return jdbcTemplate.queryForList(sql, String.class, tableName);
    }
}