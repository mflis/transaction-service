package org.mflis.transactions.controller;

import org.mflis.transactions.input.FileReader;
import org.mflis.transactions.input.Reader;
import org.mflis.transactions.model.Summary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;

@RestController
public class SummaryController {

    private Reader sourceReader;

    SummaryController(@Value("${sourcePath}") Path source) {
        sourceReader = new FileReader(source);
    }

    @RequestMapping("/summary")
    public ResponseEntity<Summary> getSummary(@RequestParam("currency") String currency, @RequestParam("type") String type) {
        if (currency.length() != 3)
            return ResponseEntity.badRequest().build();
        try {
            Summary summary = sourceReader.prepareSummary(currency.toUpperCase(), type);
            if (summary.getCurrency().isEmpty())
                return ResponseEntity.notFound().build();

            return ResponseEntity.ok(summary);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
