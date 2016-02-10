package com.krishna.filescanner;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Krishna on 2/8/2016.
 */
public class FileUtils {

    //Max file size in android 4 GB : 4,294,967,295B
    //long i = 4294967295L;
    private static BigInteger totalFileSize;
    private static long totalNumOfFiles;
    private static int curFileSize;
    private static ArrayList<File> bigFileList;
    private static Map<String,Long> extCounter;

    public static void initialize(){
        totalFileSize = new BigInteger("0");
        totalNumOfFiles = 0L;
        curFileSize = 0;
        bigFileList = new ArrayList<File>();
        extCounter = new HashMap<String,Long>();
    }

    public static void addToBigFiles(File file) {
        totalNumOfFiles++;
        totalFileSize = totalFileSize.add(BigInteger.valueOf(file.length()));
        String tempExt = getFileExtension(file).toLowerCase();
        if(extCounter.containsKey(tempExt)){
            extCounter.put(tempExt,extCounter.get(tempExt) + 1L);
        }else{
            extCounter.put(tempExt,1L);
        }

        if(curFileSize < 10){
            bigFileList.add(file);
            curFileSize++;
        }else{

            if(file.length() > bigFileList.get(bigFileList.size()-1).length()){
                bigFileList.remove(bigFileList.size()-1);
                bigFileList.add(file);
            }
        }

        Collections.sort(bigFileList, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return Long.valueOf(rhs.length()).compareTo(Long.valueOf(lhs.length()));
            }
        });

    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        if(!name.contains(".")) return "NOEXT";
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "NOEXT";
        }
    }


    public static ArrayList<File> getBigFileList(){
        System.out.println("Big File List.......start.." + bigFileList.size());
        for(File f : bigFileList){
            System.out.println("File: " + f.getAbsolutePath() + " Size: " + String.valueOf(f.length()));
        }
        System.out.println("Big File List.......End");
        return bigFileList;
    }

    public static String getAverageFileSize(){
        return totalFileSize.divide(new BigInteger(String.valueOf(totalNumOfFiles))).toString();
    }

    public static Map<String,Long> getFreqExt(){
        System.out.println("Frequent file exts...");
        Map<String, Long> result = new HashMap<String,Long>();
        List<String> list = new ArrayList<String>(extCounter.keySet());
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return extCounter.get(rhs).compareTo(extCounter.get(lhs));
            }
        });
        int count = 5;
        if(count > list.size()) count = list.size();
        for(int i=0;i<count;i++){
            result.put(list.get(i),extCounter.get(list.get(i)));
            System.out.println("EXT: " + list.get(i) + "  " + String.valueOf(extCounter.get(list.get(i))));

        }
        return result;
    }

}

