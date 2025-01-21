/**
 *
 *  @author Wieczorek Tomasz S27161
 *
 */

package zad1;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Calc {
    public String doCalc(String cmd) {

        String[] tab = cmd.split("\\s+");
        Map<Character, BiFunction<BigDecimal, BigDecimal, BigDecimal>> operations = new HashMap<>();
        operations.put('+', BigDecimal::add);
        operations.put('-', BigDecimal::subtract);
        operations.put('*', BigDecimal::multiply);
        operations.put('/', (n1, n2) -> n1.divide(n2, 7, RoundingMode.HALF_UP).stripTrailingZeros());

        return applyOperation(tab, operations);
    }

    private String applyOperation(String[] tab,
                                  Map<Character, BiFunction<BigDecimal, BigDecimal, BigDecimal>> operations){

        try{
            BigDecimal num1 = new BigDecimal(tab[0]);
            char operand = tab[1].charAt(0);
            BigDecimal num2 = new BigDecimal(tab[2]);

            BiFunction<BigDecimal, BigDecimal, BigDecimal> operation = operations.get(operand);
            return operation.apply(num1, num2).toPlainString();
        }catch (Exception exc){
            return "Invalid command to calc";
        }
    }
}
