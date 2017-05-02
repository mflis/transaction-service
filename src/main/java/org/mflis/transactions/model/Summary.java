package org.mflis.transactions.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Summary {
    @Builder.Default
    String currency = "";

    @Builder.Default
    String type = "";


    @Builder.Default
    int price = 0;

    @Builder.Default
    int commission = 0;

    @Builder.Default
    long toCharge = 0;

    @Builder.Default
    long settlementValue = 0;
}
