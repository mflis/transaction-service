package org.mflis.transactions.input

import org.mflis.transactions.model.Summary
import spock.lang.Specification

import java.util.logging.Logger


class FileReaderTest extends Specification {

    def "processing valid file yields correct results"() {

        given:
        def inputText = '''1,trip,20,5,EUR,true 
2,ticket,10,2,EUR,true 
3,trip,80,20,PLN,false 
4,transfer,100,0,PLN,true 
5,trip,50,18,EUR,true 
6,trip,120,5,PLN,true'''

        def reader = prepareReader(inputText, true)

        expect:
        def sum = new Summary(currency, type, price, commission, toCharge, settlement)
        reader.prepareSummary(currency, type) == sum

        where:
        currency | type       | price | commission | toCharge | settlement
        "EUR"    | "trip"     | 70    | 23         | 0        | 47
        "EUR"    | "ticket"   | 10    | 2          | 0        | 8
        "PLN"    | "trip"     | 200   | 25         | 80       | 95
        "PLN"    | "transfer" | 100   | 0          | 0        | 100
    }

    def "too few columns in line causes  exception when strictFileStructure=true"() {

        given: "last column is missing in  2'nd line "
        def inputText = '''1,trip,20,5,EUR,true 
2,ticket,10,2,EUR
3,trip,80,20,PLN,false'''
        def reader = prepareReader(inputText, true)

        when:
        reader.prepareSummary("EUR", "trip")

        then:
        thrown(FileProcessingException)
    }

    def "too many columns in line causes exception when strictFileStructure=true"() {

        given: "2'nd line has one extra column"
        def inputText = '''1,trip,20,5,EUR,true 
2,ticket,10,2,EUR,true,123
3,trip,80,20,PLN,false'''

        def reader = prepareReader(inputText, true)

        when:
        reader.prepareSummary("EUR", "trip")

        then:
        thrown(FileProcessingException)
    }


    def "too many columns in line causes  logging waring, and  normal processing when strictFileStructure=false"() {

        given: "each line has one extra column (the last column)"
        def validText = '''1,trip,20,5,EUR,true ,123
2,ticket,10,2,EUR,true ,123
3,trip,80,20,PLN,false ,123
4,transfer,100,0,PLN,true ,123
5,trip,50,18,EUR,true ,123
6,trip,120,5,PLN,true,123'''

        FileReader reader = prepareReader(validText, false)

        when:
        def summary = reader.prepareSummary(currency, type)


        then:
        summary == new Summary(currency, type, price, commission, toCharge, settlement)
        6 * reader.log.warning(_)

        where:
        currency | type       | price | commission | toCharge | settlement
        "EUR"    | "trip"     | 70    | 23         | 0        | 47
        "EUR"    | "ticket"   | 10    | 2          | 0        | 8
        "PLN"    | "trip"     | 200   | 25         | 80       | 95
        "PLN"    | "transfer" | 100   | 0          | 0        | 100
    }

    def "too few columns in line causes  logging waring, and  skipping this line  in processing when strictFileStructure=false"() {

        given: "1'st line has removed price column"
        def inputText = '''1,trip,5,EUR,true
2,ticket,10,2,EUR,true 
3,trip,80,20,PLN,false 
4,transfer,100,0,PLN,true 
5,trip,50,18,EUR,true 
6,trip,120,5,PLN,true'''

        FileReader reader = prepareReader(inputText, false)

        when:
        def summary = reader.prepareSummary(currency, type)


        then:
        summary == new Summary(currency, type, price, commission, toCharge, settlement)
        1 * reader.log.warning(_)

        where:
        currency | type       | price | commission | toCharge | settlement
        "EUR"    | "trip"     | 50    | 18         | 0        | 32
        "EUR"    | "ticket"   | 10    | 2          | 0        | 8
        "PLN"    | "trip"     | 200   | 25         | 80       | 95
        "PLN"    | "transfer" | 100   | 0          | 0        | 100
    }


    def "pattern validation failure causes skipping line logging warning when strictFileStructure=false"() {

        given: "currency in 1'st line not matching pattern (4 letters)"

        def inputText = '''1,trip,20,5,EURO,true
2,ticket,10,2,EUR,true 
3,trip,80,20,PLN,false 
4,transfer,100,0,PLN,true 
5,trip,50,18,EUR,true 
6,trip,120,5,PLN,true'''
        FileReader reader = prepareReader(inputText, false)

        when:
        def summary = reader.prepareSummary(currency, type)

        then:
        summary == new Summary(currency, type, price, commission, toCharge, settlement)
        1 * reader.log.warning(_)

        where:
        currency | type       | price | commission | toCharge | settlement
        "EUR"    | "trip"     | 50    | 18         | 0        | 32
        "EUR"    | "ticket"   | 10    | 2          | 0        | 8
        "PLN"    | "trip"     | 200   | 25         | 80       | 95
        "PLN"    | "transfer" | 100   | 0          | 0        | 100
    }

    def "pattern validation failure causes throwing exception when strictFileStructure=true"() {

        given: "currency in 1'st line not matching pattern (4 letters)"
        def validText = '''1,trip,20,5,EURO,true
2,ticket,10,2,EUR,true 
6,trip,120,5,PLN,true'''
        FileReader reader = prepareReader(validText, true)


        when:
        reader.prepareSummary("EUR", "trip")


        then:
        thrown(FileProcessingException)
    }

    FileReader prepareReader(String text, boolean strictFileStructure) {
        def transactions = new File('transactions.csv')
        transactions.text = text
        def transactionsPath = transactions.toPath()
        transactions.deleteOnExit()
        def reader = new FileReader(transactionsPath, strictFileStructure)
        def logger = Mock(Logger)
        reader.log = logger

        return reader
    }
}

