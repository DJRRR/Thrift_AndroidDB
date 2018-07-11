package cn.fudan.libdb.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Dai Jiarun
 * @date 2018/7/10
 */
public class FileUtils {
    public static boolean saveStrToFile(String content, String filepath){
        File file = new File(filepath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWritter = new FileWriter(file.getAbsolutePath(), true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(content);
            bufferWritter.close();
            fileWritter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }
}
