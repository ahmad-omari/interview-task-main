package com.progressoft.tools;

import java.nio.file.Path;

public class DataNormalization implements  Normalizer{

    @Override
    public ScoringSummary zscore(Path csvPath, Path destPath, String colToStandardize) {
        DataNormalizer dataNormalizer = new ZScoreDataNormalizer();
        FileDataNormalization fileDataNormalization = new FileDataNormalization(dataNormalizer,csvPath,destPath,colToStandardize);
        return fileDataNormalization.getDataScoringSummary();
    }

    @Override
    public ScoringSummary minMaxScaling(Path csvPath, Path destPath, String colToNormalize) {
        DataNormalizer dataNormalizer = new MinMaxDataNormalizer();
        FileDataNormalization fileDataNormalization = new FileDataNormalization(dataNormalizer,csvPath,destPath,colToNormalize);
        return fileDataNormalization.getDataScoringSummary();
    }

}
