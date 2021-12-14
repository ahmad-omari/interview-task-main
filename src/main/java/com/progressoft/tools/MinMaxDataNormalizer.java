package com.progressoft.tools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MinMaxDataNormalizer implements DataNormalizer{

    @Override
    public List<String> getDataNormalizedList(List<String> data) {
        List<String> normalizedList = new ArrayList<>();
        if (data.isEmpty()){
            return normalizedList;
        }
        DataScoringSummary dataScoringSummary = new DataScoringSummary(data);
        for (String value : data){
            try {
                BigDecimal bigDecimalValue = new BigDecimal(value);
                BigDecimal xMin = dataScoringSummary.min();
                BigDecimal xMax = dataScoringSummary.max();
                BigDecimal normalizedValue = bigDecimalValue.subtract(xMin).divide( xMax.subtract(xMin) , BigDecimal.ROUND_HALF_EVEN);
                normalizedList.add(normalizedValue.toString());
            }catch (NumberFormatException exc){
                exc.printStackTrace();
            }
        }
        return normalizedList;
    }
}
