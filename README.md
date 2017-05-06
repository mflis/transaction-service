# transaction-service

This is coding exercise using Spring Boot and Java 8.

## Input

- `.csv` file containing transactions details
- Columns: `id, type, price, commission,currency, is_paid`

Example file:

```
1,trip,20,5,EUR,true 
2,ticket,10,2,EUR,true 
3,trip,80,20,PLN,false 
4,transfer,100,0,PLN,true 
5,trip,50,18,EUR,true 
6,trip,120,5,PLN,true
```
## Output

- Summary of information grouped by currency and type.

|currency | type | price | commission | to_charge_value | settlement_value|
|--- | --- | --- |--- | --- | ---|
|EUR | trip | 70 | 23 | 0 | 47|
|EUR | ticket | 10 | 2 | 0 | 8|
|PLN | trip | 200 | 25 | 80 | 95|
|PLN | transfer | 100 | 0 | 0 | 100|

Columns explanation:
-  `price` -   a sum of  `price` columns with matching `currency` and `type`
- `commission`  - analogously to `price` column
- `to charge value` - sum of  `price` columns from transactions where   `is_paid == false` 
- `settlement value` -  `settlement value = price - commission - to charge value`

## Access to output

Output should accessible via RESTful api. Parameters for query are: `currency` and `type`

Example: `http://localhost:8080/summary?currency=EUR&type=trip`

This query should be responded with information from one row of summary with matching `currency` and `type`

## Notes about current implementation
- Project uses [lombok](https://projectlombok.org/) for getter/setter/etc generation. Ino order to let IDE know about generated metehds [plugin](https://github.com/mplushnikov/lombok-intellij-plugin#installation) is needed

### `application.properties`
 - `sourcePath` - location of input file
 - `strictFileStructure` - config option telling about validity of input file
   - `true` - input has to have exactly 6 colums as in specs and any error in validation proces causes error response to client
   - `false` - when input has more than 6 colums it is processed as normal. When it has less columns or validation error occurs, this line is skipped in summary generation process
