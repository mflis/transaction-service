package org.mflis.transactions.input;

import org.mflis.transactions.model.Summary;

import java.nio.file.Path;

public interface Reader {
    Summary prepareSummary(String currency, String type, Path Source);
}
