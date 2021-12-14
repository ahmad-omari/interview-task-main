package com.progressoft.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataScoringSummary implements ScoringSummary {
    private List<BigDecimal> bigDecimalList;
    private BigDecimal sum;
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal mean;
    private BigDecimal median;
    private BigDecimal variance;
    private BigDecimal standardDeviation;

    public DataScoringSummary(List<String> columnValues) {
        bigDecimalList = new ArrayList<>();
        min = new BigDecimal(Double.MAX_VALUE);
        max = new BigDecimal(Double.MIN_VALUE);
        sum = BigDecimal.ZERO;
        mean = null;
        median = null;
        variance = null;
        standardDeviation = null;
        calculateMandatoryValues(columnValues);
    }

    private void calculateMandatoryValues(List<String> columnValues) {
        for (String value : columnValues) {
            try {
                BigDecimal decimalValue = new BigDecimal(value);
                min = decimalValue.min(min);
                max = decimalValue.max(max);
                sum = decimalValue.add(sum);
                bigDecimalList.add(decimalValue);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public BigDecimal mean() {
        if (mean != null) {
            return mean;
        }
        mean = sum.divide(new BigDecimal(bigDecimalList.size()), RoundingMode.HALF_EVEN);
        mean = mean.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return mean;
    }

    @Override
    public BigDecimal standardDeviation() {
        if (standardDeviation != null) {
            return standardDeviation;
        }
        standardDeviation = sqrt(variance());
     //   standardDeviation = standardDeviation.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return standardDeviation;//new BigDecimal("16.73");
    }

    private BigDecimal sqrt(BigDecimal number){
        BigDecimal sqrtroot=number.divide(new BigDecimal(2), RoundingMode.HALF_DOWN);
        BigDecimal temp;

        do
        {
            temp=sqrtroot;
            sqrtroot = ( temp.add( number.divide(temp, RoundingMode.HALF_DOWN)) ).divide(new BigDecimal(2), RoundingMode.HALF_DOWN);

        }
        while((temp.subtract(sqrtroot)).compareTo(BigDecimal.ZERO) != 0);
        return sqrtroot;
    }

    @Override
    public BigDecimal variance() {
        if (variance != null){
            return variance;
        }
        int n = bigDecimalList.size();
        if (n == 0) {
            return BigDecimal.valueOf(Double.NaN);
        } else if (n == 1) {
            return BigDecimal.ZERO;
        }
        BigDecimal mean = mean();
        List<BigDecimal> squares = new ArrayList<BigDecimal>();

        for (BigDecimal number : bigDecimalList) {
            BigDecimal XminMean = number.subtract(mean);
            squares.add(XminMean.pow(2));
        }
        BigDecimal sum = sum(squares);
        variance = sum.divide(new BigDecimal(bigDecimalList.size() ), RoundingMode.HALF_EVEN);
        variance = variance.setScale(0, RoundingMode.HALF_UP);
        variance = variance.setScale(2, RoundingMode.HALF_EVEN);
        return variance;
    }

    private BigDecimal sum(List<BigDecimal> numbers) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal bigDecimal : numbers) {
            sum = sum.add(bigDecimal);
        }
        return sum;
    }

    @Override
    public BigDecimal median() {
        if (median != null){
            return median;
        }

        int size = bigDecimalList.size();
        if (size == 0){
            return null;
        }
        if(size == 1){
            median = bigDecimalList.get(0);
            median = median.setScale(2, BigDecimal.ROUND_HALF_EVEN);
            return median;
        }

        Collections.sort(bigDecimalList);
        if(size % 2 == 0){
            median = bigDecimalList.get(size/2);
            median = median.setScale(2, BigDecimal.ROUND_HALF_EVEN);
            return median;
        }
        BigDecimal leftHalf = bigDecimalList.get(size/2);
        BigDecimal rightHalf = bigDecimalList.get(bigDecimalList.size()/2 + 1);
        median = (leftHalf.add(rightHalf)).divide(new BigDecimal(2), BigDecimal.ROUND_HALF_EVEN);
        median = median.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return median;
    }

    @Override
    public BigDecimal min() {
        min = min.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return min;
    }

    @Override
    public BigDecimal max() {
        max = max.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return max;
    }
}
