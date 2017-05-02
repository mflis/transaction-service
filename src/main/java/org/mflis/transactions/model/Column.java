package org.mflis.transactions.model;

import lombok.Value;

@Value
public class Column {
    int id;
    String type;
    int price;
    int commission;
    String currency;
    boolean isPaid;
}
