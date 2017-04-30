package org.mflis.transactions.input;

import org.mflis.transactions.model.Summary;

import java.nio.file.Path;

public class FileReader implements Reader {

    @Override
    public Summary prepareSummary(String currency, String type, Path Source) {
        return null;
    }
}
