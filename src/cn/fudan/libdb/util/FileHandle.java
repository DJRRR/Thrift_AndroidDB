package cn.fudan.libdb.util;

import com.sun.xml.internal.bind.v2.TODO;
import sun.plugin2.message.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Dai Jiarun
 * @date 2018/9/13
 */
public class FileHandle {
    public static final String JAR_SUFFIX = ".jar";
    public static final String DEX_SUFFIX = ".dex";
    public static final String AAR_SUFFIX = ".aar";
    public static final String APKLIB_SUFFIX = ".apklib";


    byte[] content;
    File tmpFile;
    // TODO: 2018/9/13 to support source-code fetch 
    String fileSuffix;//.jar or .dex
    

    public FileHandle(byte[] content) {this.content = content;}

    public byte[] getContent() {return content;}

    public String getContentHashSHA256(){
        try{
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(content);
            byte[] b = sha256.digest();
            String dexsha256 = "";
            for (int i = 0; i < b.length; i++) {
                dexsha256 += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
            }
            return dexsha256;
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return null;
    }



    public String getContentHashMD5(){
        try{
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(content);
            byte[] b = md5.digest();
            String jarMD5 = "";
            for (int i = 0; i < b.length; i++) {
                jarMD5 += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
            }
            return jarMD5;

        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return null;
    }

    public File getJarFile() {
        if (tmpFile != null)
            return tmpFile;
        else {
            try {
                tmpFile = File.createTempFile("jarrepo_", ".jar");
                tmpFile.deleteOnExit();
                FileOutputStream out = new FileOutputStream(tmpFile);
                out.write(content);
                out.flush();
                out.close();
                return tmpFile;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public File getDexFile() {
        if (tmpFile != null)
            return tmpFile;
        else {
            try {
                tmpFile = File.createTempFile("dexrepo_", ".dex");
                tmpFile.deleteOnExit();
                FileOutputStream out = new FileOutputStream(tmpFile);
                out.write(content);
                out.flush();
                out.close();
                return tmpFile;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void deleteFile() {
        if (tmpFile != null) {
            tmpFile.delete();
        }
    }

    public void writeFile(String path) throws FileNotFoundException, IOException {
        FileOutputStream writer = new FileOutputStream(path);
        writer.write(content);
        writer.close();
    }

    @Override
    public void finalize() {
        if (tmpFile != null) {
            tmpFile.delete();
            tmpFile = null;
        }
    }
}
