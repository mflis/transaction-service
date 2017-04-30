package org.mflis.transactions.model;

import lombok.Value;

@Value
public class Summary {
    String currency;
    String type;
    Integer price;
    Integer commission;
    Long toCharge;
    Long settlementValue;
}
