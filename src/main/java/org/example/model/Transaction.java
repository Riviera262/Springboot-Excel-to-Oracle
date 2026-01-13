package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private String trace;
    private String fromAcc;
    private LocalDateTime tranxTime;
    private BigDecimal amount;
    private String toAcc;
    private String remark;
    private String tranxType;
}