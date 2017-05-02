package org.mflis.transactions.input;

import org.mflis.transactions.model.Summary;

import java.io.IOException;

public interface Reader {
    Summary prepareSummary(String currency, String type) throws IOException;
}
