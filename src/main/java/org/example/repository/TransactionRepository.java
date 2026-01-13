package org.example.repository;

import org.example.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TransactionRepository{

    @Autowired
    private JdbcTemplate jdbc;

    public int batchInsert(List<Transaction> transactions){
        if (transactions == null || transactions.isEmpty()){
            return 0;
        }
        String sql = "MERGE INTO mb_transaction t " +
                     "USING (SELECT ? AS trace FROM dual) s " +
                     "ON (t.trace = s.trace)" +
                     "WHEN NOT MATCHED THEN " +
                     "INSERT (trace, from_acc, tranx_time, amount, to_acc, remark, tranx_type) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        int[] results = jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Transaction txn = transactions.get(i);
                //Checking the condition if trace matches any trace in DB
                ps.setString(1, txn.getTrace());

                //Trace for insert
                ps.setString(2, txn.getTrace());
                ps.setString(3, txn.getFromAcc());
                ps.setObject(4, txn.getTranxTime());
                ps.setBigDecimal(5, txn.getAmount());
                ps.setString(6, txn.getToAcc());
                ps.setString(7, txn.getRemark());
                ps.setString(8, txn.getTranxType());
            }

            @Override
            public int getBatchSize() {
                return transactions.size();
            }
        });

        int actualInserts = 0;
        for (int result : results){
            if (result > 0){
                actualInserts++;
            }
        }
        return actualInserts;
    }

    //Delete all data in Oracle
    public void deleteAll(){
        String sql = "DELETE FROM mb_transaction";
        jdbc.update(sql);
    }

    public void findAllTrace(){

    }

    //Just for learning:P
    //insert 1 object(normal insert)
    public void insert(Transaction txn){
        String sql = "INSERT INTO mb_transaction(trace, from_acc, tranx_time, amount, to_acc, remark, tranx_type) VALUES (?,?,?,?,?,?,?)";
        jdbc.update(sql,
                txn.getTrace(),
                txn.getFromAcc(),
                txn.getTranxTime(),
                txn.getAmount(),
                txn.getToAcc(),
                txn.getRemark(),
                txn.getTranxType());
    }

    //Testing connection to oracle
    public boolean testConnection(){
        try {
            String sql = "SELECT 1 FROM DUAL";
            Integer result = jdbc.queryForObject(sql, Integer.class);
            return result != null && result == 1;
        } catch (Exception e){
            System.err.println("Database connection failed" + e.getMessage());
            return false;
        }
    }

    //Testing how many rows in oracle(if there are too much data in oracle)
    public long count(){
        String sql = "SELECT COUNT(*) FROM mb_transaction";
        long result = jdbc.queryForObject(sql, long.class);
        return result;
    }

}