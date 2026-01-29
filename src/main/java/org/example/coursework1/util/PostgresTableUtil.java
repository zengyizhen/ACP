package org.example.coursework1.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    // 插入数据到指定表
    public boolean insertDataIntoTable(String tableName, List<Map<String, Object>> drones) {
        if (drones == null || drones.isEmpty()) {
            return false;
        }

        try {
            for (Map<String, Object> drone : drones) {
                if (drone.containsKey("name")) {
                    String name = (String) drone.get("name");

                    // 检查name是否已存在
                    String checkSql = "SELECT COUNT(*) FROM " + tableName + " WHERE name = ?";
                    Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, name);

                    if (count != null && count > 0) {
                        // 更新现有记录
                        updateExistingRecord(tableName, drone, name);
                    } else {
                        // 插入新记录
                        insertNewRecord(tableName, drone);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 插入新记录
    private void insertNewRecord(String tableName, Map<String, Object> drone) {
        List<String> columns = new ArrayList<>(drone.keySet());
        String columnsStr = String.join(", ", columns);
        String placeholders = columns.stream().map(col -> "?").collect(Collectors.joining(", "));

        String sql = "INSERT INTO " + tableName + " (" + columnsStr + ") VALUES (" + placeholders + ")";
        Object[] params = columns.stream().map(drone::get).toArray();

        jdbcTemplate.update(sql, params);
    }

    // 更新现有记录
    private void updateExistingRecord(String tableName, Map<String, Object> drone, String name) {
        List<String> columns = drone.keySet().stream()
                .filter(col -> !"name".equals(col)) // 排除主键name用于WHERE条件
                .collect(Collectors.toList());

        String setClause = columns.stream()
                .map(col -> col + " = ?")
                .collect(Collectors.joining(", "));

        String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE name = ?";
        Object[] params = columns.stream().map(drone::get).toArray();
        Object[] allParams = new Object[params.length + 1];
        System.arraycopy(params, 0, allParams, 0, params.length);
        allParams[params.length] = name;

        jdbcTemplate.update(sql, allParams);
    }
}
