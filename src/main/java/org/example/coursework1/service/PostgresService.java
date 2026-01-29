package org.example.coursework1.service;

import org.example.coursework1.util.PostgresTableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PostgresService {

    @Autowired
    private PostgresTableUtil postgresTableUtil;

    @Autowired
    private IlpService ilpService;
    /**
     * 获取表中所有数据并返回响应对象
     */
    public ResponseEntity<List<Map<String, Object>>> getAllDataFromTableResponse(String tableName) {
        // 验证表名合法性
        // 检查表是否存在
        if (!isValidTableName(tableName)||!postgresTableUtil.isTableExists(tableName)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 表名不合法应返回 400
        }
        // 查询数据
        try {
            List<Map<String, Object>> data = postgresTableUtil.getAllTableData(tableName);
            return new ResponseEntity<>(data, HttpStatus.OK); // 成功返回 200 和数据
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 数据库错误返回 500
        }
    }
    /*从ILP获取数据并 插入到Postgres数据库中*/
    public ResponseEntity<List<Map<String, Object>>> insertDataFromILPToPostgres(String tableName,String urlPath) {
        try {
            List<Map<String, Object>> drones = ilpService.fetchDronesPlain(urlPath);

            // 插入数据到数据库
            boolean success = postgresTableUtil.insertDataIntoTable(tableName, drones);

            if (success) {
                return new ResponseEntity<>(drones, HttpStatus.OK); // 成功返回 200 和数据
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 数据库错误返回 500
        }

    }
    private boolean isValidTableName(String tableName) {
        return tableName != null && tableName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$");
    }
}