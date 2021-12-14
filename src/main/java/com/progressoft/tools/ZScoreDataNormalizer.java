package com.progressoft.tools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ZScoreDataNormalizer implements DataNormalizer{

    @Override
    public List<String> getDataNormalizedList(List<String> data) {
        List<String> standardizedList = new ArrayList<>();
        if (data.isEmpty()){
            return standardizedList;
        }
        DataScoringSummary dataScoringSummary = new DataScoringSummary(data);
        for (String value : data){
            try {
                BigDecimal bigDecimalValue = new BigDecimal(value);
                BigDecimal mean = dataScoringSummary.mean();
                BigDecimal standardDeviation = dataScoringSummary.standardDeviation();
                BigDecimal normalizedValue = bigDecimalValue.subtract(mean).divide( standardDeviation, BigDecimal.ROUND_HALF_EVEN);
                standardizedList.add(normalizedValue.toString());
            }catch (NumberFormatException exc){
                exc.printStackTrace();
            }
        }
        return standardizedList;
    }
}
