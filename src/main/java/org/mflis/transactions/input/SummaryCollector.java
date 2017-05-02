package org.mflis.transactions.input;

import org.mflis.transactions.model.Column;
import org.mflis.transactions.model.Summary;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class SummaryCollector implements Collector<Column, Summary, Summary> {
    @Override
    public Supplier<Summary> supplier() {
        return () -> Summary.builder().build();
    }

    @Override
    public BiConsumer<Summary, Column> accumulator() {
        return this::addColumn;
    }

    @Override
    public BinaryOperator<Summary> combiner() {
        return (s1, s2) -> {
            throw new UnsupportedOperationException();
        };
    }

    @Override
    public Function<Summary, Summary> finisher() {
        return summary -> summary;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.UNORDERED);

    }

    private void addColumn(Summary summary, Column column) {
        assert summary.getCurrency().equals("") ||
                summary.getCurrency().equals(column.getCurrency()) : "different currencies combined";
        assert summary.getType().equals("") ||
                summary.getType().equals(column.getType()) : "different types combined";

        int price = summary.getPrice() + column.getPrice();
        int commision = summary.getCommission() + column.getCommission();
        long toCharge = column.isPaid() ? summary.getToCharge() : summary.getToCharge() + column.getPrice();
        long settlemnt = price - commision - toCharge;

        summary.setCurrency(column.getCurrency());
        summary.setType(column.getType());
        summary.setPrice(price);
        summary.setCommission(commision);
        summary.setToCharge(toCharge);
        summary.setSettlementValue(settlemnt);
    }

}
