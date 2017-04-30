package org.mflis.transactions.input

import org.mflis.transactions.model.Summary
import spock.lang.Specification


class FileReaderTest extends Specification {
    def "test prepareSummary"() {

        given:
        def transactions = new File('transactions.csv')
        transactions.text = '''1,trip,20,5,EUR,true 
2,ticket,10,2,EUR,true 
3,trip,80,20,PLN,false 
4,transfer,100,0,PLN,true 
5,trip,50,18,EUR,true 
6,trip,120,5,PLN,false'''

        def transactionsPath = transactions.toPath()
        transactions.deleteOnExit()
        def reader = new FileReader()

        expect:
        def sum = new Summary(currency, type, price, commission, toCharge, settlement)
        reader.prepareSummary(currency, type, transactionsPath) == sum

        where:
        currency | type       | price | commission | toCharge | settlement
        "EUR"    | "trip"     | 70    | 23         | 0        | 47
        "EUR"    | "ticket"   | 10    | 2          | 0        | 8
        "PLN"    | "trip"     | 200   | 25         | 200      | -25
        "PLN"    | "transfer" | 100   | 0          | 0        | 100
    }

}

