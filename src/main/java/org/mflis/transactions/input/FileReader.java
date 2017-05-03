package org.mflis.transactions.input;

import com.codepoetics.protonpack.StreamUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mflis.transactions.model.Column;
import org.mflis.transactions.model.Summary;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class FileReader implements Reader {

    private static Logger log = Logger.getLogger(FileReader.class.getName());
    private Path source;

    private boolean strictFileStructure;

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

        return validateNrOfColumns(line)
                .flatMap(this::validateFieldsPatterns)
                .flatMap(this::transformToColumn);
    }


    private Optional<Column> transformToColumn(List<String> columns) {
        int id = Integer.parseInt(columns.get(Fields.id.ordinal()));
        String type = columns.get(Fields.type.ordinal());
        int price = Integer.parseInt(columns.get(Fields.price.ordinal()));
        int commission = Integer.parseInt(columns.get(Fields.commission.ordinal()));
        String currency = columns.get(Fields.currency.ordinal());
        boolean isPaid = Boolean.valueOf(columns.get(Fields.is_paid.ordinal()).trim());

        return Optional.of(new Column(id, type, price, commission, currency, isPaid));

    }

    private Optional<List<String>> validateNrOfColumns(String line) {
        List<String> columns = Arrays.stream(line.split(",")).map(String::trim).collect(Collectors.toList());

        int expectedNrOfColums = Fields.values().length;
        int diffColumns = columns.size() - expectedNrOfColums;
        if (diffColumns != 0) {
            if (strictFileStructure)
                throw new FileProcessingException(errorMsg(expectedNrOfColums, columns.size(), line));
            else {
                log.warning(errorMsg(expectedNrOfColums, columns.size(), line));
                if (diffColumns < 0)
                    return Optional.empty();
            }
        }
        return Optional.of(columns);
    }


    private Optional<List<String>> validateFieldsPatterns(List<String> fields) {

        Stream<String> patternStream = Arrays.stream(Fields.values()).map(Fields::getFieldFormat);
        Stream<String> fieldStream = fields.stream();
        boolean areFieldsValid = StreamUtils.zip(fieldStream, patternStream, String::matches).allMatch(x -> x);
        if (areFieldsValid) return Optional.of(fields);

        if (strictFileStructure) throw new FileProcessingException(fieldValidationMsg(fields));
        else log.warning(fieldValidationMsg(fields));

        return Optional.empty();
    }


    private String errorMsg(int expectedSize, int actualSize, String line) {
        return "wrong number of columns, expected: "
                + expectedSize + "but got: "
                + actualSize + ". \nMalformed line: " + line;
    }

    private String fieldValidationMsg(List<String> fields) {
        return "field validation failed with: " + fields.toString();
    }


    @AllArgsConstructor
    @Getter
    private enum Fields {
        id("\\d+"),
        type("\\w+"),
        price("\\d+"),
        commission("\\d+"),
        currency("[A-Z]{3}"),
        is_paid("true|false");

        private String fieldFormat;
    }
}