package org.mflis.transactions.input;

import org.mflis.transactions.model.Column;
import org.mflis.transactions.model.Summary;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collector;

public class FileReader implements Reader {

    @Override
    public Summary prepareSummary(String currency, String type, Path source) throws IOException {
        BufferedReader br = Files.newBufferedReader(source);

        Collector<Column, Summary, Summary> summaryCollector = new SummaryCollector();
        return br.lines().map(this::mapColumn)
                .filter(Optional::isPresent).map(Optional::get)
                .filter(x -> x.getCurrency().equals(currency))
                .filter(x -> x.getType().equals(type))
                .collect(summaryCollector);

    }

    private Optional<Column> mapColumn(String line) {
        String[] columns = line.split(",");
        if (columns.length != Fields.values().length)
            return Optional.empty();

        int id = Integer.parseInt(columns[Fields.id.ordinal()]);
        String type = columns[Fields.type.ordinal()];
        int price = Integer.parseInt(columns[Fields.price.ordinal()]);
        int commission = Integer.parseInt(columns[Fields.commission.ordinal()]);
        String currency = columns[Fields.currency.ordinal()];
        boolean isPaid = Boolean.valueOf(columns[Fields.is_paid.ordinal()].trim());

        return Optional.of(new Column(id, type, price, commission, currency, isPaid));
    }

    private enum Fields {
        id,
        type,
        price,
        commission,
        currency,
        is_paid
    }

}
