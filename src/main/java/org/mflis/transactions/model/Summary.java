package org.mflis.transactions.model;

import lombok.Value;

@Value
public class Summary {
    Long toCharge;
    Long settlementValue;
    String type;
    Integer price;
    Integer commission;
    String currency;
}
