package com.progressoft.tools;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileDataNormalization {
    private DataNormalizer dataNormalizer;
    private Path csvPath;
    private Path destPath;
    private String colToNormalize;
    private DataScoringSummary dataScoringSummary;

    public FileDataNormalization(DataNormalizer dataNormalizer, Path csvPath, Path destPath, String colToNormalize) {
        this.dataNormalizer = dataNormalizer;
        this.csvPath = csvPath;
        this.destPath = destPath;
        this.colToNormalize = colToNormalize;
        applyDataNormalization();
    }

    private void applyDataNormalization(){
        List<String> headerNames = new ArrayList<>();
        Map<String, List<String>>  csvMap = getCsvMapFromPath(csvPath, headerNames);
        if(!csvMap.containsKey(colToNormalize)){
            throw new IllegalArgumentException("column " + colToNormalize + " not found");
        }
        List<String> minMaxScaledList = dataNormalizer.getDataNormalizedList(csvMap.get(colToNormalize));
        if(!minMaxScaledList.isEmpty()) {
            String newColumnName = getNewColumnName(colToNormalize);
            int columnIndex = headerNames.indexOf(colToNormalize);
            headerNames.add(columnIndex+1, newColumnName);
            addNewMapValue(csvMap, newColumnName, minMaxScaledList);
        }

        exportData(csvMap, headerNames, destPath);
        dataScoringSummary = new DataScoringSummary(csvMap.get(colToNormalize));
    }

    private Map<String, List<String>> getCsvMapFromPath(Path csvPath, List<String> headerNames){
        Map<String, List<String>> csvMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath.toFile()))){
            boolean isHeader = true;

            while (reader.ready()){
                String line = reader.readLine();

                if(isHeader){
                    String [] names = line.split(",");
                    for (String name : names){
                        csvMap.put(name, new ArrayList<>());
                        headerNames.add(name);
                    }
                    isHeader = false;
                    continue;
                }

                String [] values = line.split(",");
                for (int i=0 ; i < values.length ; i++){
                    String headerName = headerNames.get(i);
                    if(csvMap.containsKey(headerName)){
                        csvMap.get(headerName).add(values[i]);
                    }
                }

            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("source file not found");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return csvMap;
    }

    private String getNewColumnName(String name){
        if (dataNormalizer instanceof ZScoreDataNormalizer){
            return name + "_z";
        }
        if (dataNormalizer instanceof MinMaxDataNormalizer){
            return name + "_mm";
        }
        return name;
    }

    private void addNewMapValue(Map<String, List<String>> map, String key, List<String> value){
        if (value.isEmpty()){
            return;
        }
        map.put(key, value);
    }

    private void exportData(Map<String, List<String>> csvMap, List<String> headerNames, Path destPath){

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(destPath.toFile()))){
            String headerLine = String.join(",", headerNames);
            bufferedWriter.write(headerLine);
            bufferedWriter.newLine();

            List<List<String>> allColumns = new ArrayList<>();
            int columnSize = 0;
            for (int i = 0 ;i<headerNames.size();i++){
                List<String> columnList = csvMap.get(headerNames.get(i));
                if (columnList.size() > columnSize){
                    columnSize = columnList.size();
                }
                allColumns.add(columnList);
            }

            int rowNumber = 0;
            while (rowNumber < columnSize){
                String line = "";
                for (int i = 0 ;i<headerNames.size();i++){
                    if(!line.isEmpty()){
                        line = line+",";
                    }
                    if(allColumns.get(i).size() <= rowNumber){
                        continue;
                    }
                    line += allColumns.get(i).get(rowNumber);
                }
                rowNumber++;
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public DataScoringSummary getDataScoringSummary(){
        return dataScoringSummary;
    }

}
