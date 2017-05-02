package org.mflis.transactions.input;

import lombok.AllArgsConstructor;
import org.mflis.transactions.model.Column;
import org.mflis.transactions.model.Summary;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@AllArgsConstructor
public class FileReader implements Reader {

    private Path source;

    @Override
    public Summary prepareSummary(String currency, String type) throws IOException {
        BufferedReader br = Files.newBufferedReader(source);

        Collector<Column, Summary, Summary> summaryCollector = new SummaryCollector();
        return br.lines().map(this::mapColumn)
                .filter(Optional::isPresent).map(Optional::get)
                .filter(x -> x.getCurrency().equals(currency))
                .filter(x -> x.getType().equals(type))
                .collect(summaryCollector);

    }

    private Optional<Column> mapColumn(String line) {
        List<String> columns = Arrays.stream(line.split(",")).map(String::trim).collect(Collectors.toList());
        if (columns.size() != Fields.values().length)
            return Optional.empty();

        int id = Integer.parseInt(columns.get(Fields.id.ordinal()));
        String type = columns.get(Fields.type.ordinal());
        int price = Integer.parseInt(columns.get(Fields.price.ordinal()));
        int commission = Integer.parseInt(columns.get(Fields.commission.ordinal()));
        String currency = columns.get(Fields.currency.ordinal());
        boolean isPaid = Boolean.valueOf(columns.get(Fields.is_paid.ordinal()).trim());

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
