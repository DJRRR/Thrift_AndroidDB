package cn.fudan.androiddb.client;

import com.beust.jcommander.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dai Jiarun
 * @date 2018/7/6
 */
public class AndoidDBArgs {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = {"--limit", "-l"}, description = "Max Size of Return List")
    private int limit = Integer.MAX_VALUE;

    @Parameter(names = {"--group","-g"}, description = "Group Name of Lib Package")
    private String groupName;

    @Parameter(names = {"--artifact","-a"}, description = "Artifact ID of Lib Package")
    private String artifactId;

    @Parameter(names = {"--version","-v"}, description = "Version Number of Lib Package")
    private String version;

    @Parameter(names = {"--output","-o"}, description = "Output File(Dir)")
    private String outputFilePath;

    @Parameter(names = {"--json", "-j"}, description = "Json")
    private boolean jsonOutput = false;

    @Parameter(names = {"--help", "-h"}, help = true)
    private boolean help = false;

    @Parameter(names = {"--query","-q"}, description = "Search in the libdb")
    private boolean query = false;

    @Parameter(names = {"--fetch", "-f"}, description = "Download lib(jar/dex)")
    private boolean fetch = false;

    @Parameter(names = {"--key","-k"}, description = "Hash key of lib package")
    private String hashKey;

    @Parameter(names = {"--hashList", "-hl"}, description = "Sync get file for each line of hash value in the file")
    private String hashListFilePath;

    @Parameter(names = {"--repo", "-r"}, description = "Repo Type : lib, apk")
    private String repoType;

    @Parameter(names = {"--package", "-p"}, description = "Package Name of Apk")
    private String packageName;

    @Parameter(names = {"--creator","-c"},description = "crawler creator")
    private String creator;

    @Parameter(names = {"--crawler"}, description = "crawler")
    private boolean crawler = false;

    @Parameter(names = {"--market","-m"},description = "market name")
    private String market;

    @Parameter(names = {"--scope","-s"},description = "top or all")
    private String scope;

    public int getLimit() {
        return limit;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getHashKey() {
        return hashKey;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public String getHashListFilePath(){
        return hashListFilePath;
    }

    public String getRepoType(){
        return repoType;
    }

    public String getPackageName(){
        return packageName;
    }

    public String getCreator(){
        return creator;
    }

    public String getMarket(){
        return market;
    }

    public String getScope(){
        return scope;
    }

    public boolean isJsonOutput(){
        return jsonOutput;
    }

    public boolean isHelp() {
        return help;
    }

    public boolean isQuery() {
        return query;
    }

    public boolean isFetch() {
        return fetch;
    }

    public boolean isCrawler(){
        return crawler;
    }

    public boolean versionUnset(){
        return version == null;
    }

    public boolean groupUnset(){
        return groupName == null;
    }

    public boolean artifactUnset(){
        return artifactId == null;
    }

    public boolean outputPathUnset(){
        return outputFilePath == null;
    }

    public boolean hashKeyUnset(){
        return hashKey == null;
    }

    public boolean hashListFilePathUnset(){
        return hashListFilePath == null;
    }

    public boolean repoTypeUnset(){
        return repoType == null;
    }

    public boolean packageNameUnset(){
        return packageName == null;
    }



    // TODO: 2018/9/17 to place all check funcs here
    public static boolean repoTypeCheck(AndoidDBArgs andoidDBArgs){
        if(andoidDBArgs.repoTypeUnset()){
            System.err.println("Set type for file fetching(-r lib, apk or apk-src)");
            return false;
        }
        String repoType = andoidDBArgs.getRepoType();
        if(!repoType.equals("lib") && !repoType.equals("apk") && !repoType.equals("apk-src")){
            System.err.println("Error repo type, only support lib, apk, apk-src");
            return false;
        }

        return true;
    }

    public static boolean libQueryCheck(AndoidDBArgs andoidDBArgs){
        if(andoidDBArgs.groupUnset() && andoidDBArgs.artifactUnset() && andoidDBArgs.versionUnset()){
            System.out.println("Please set -g or -a or -v for a query!");
            System.err.println("-h for more information");
            return false;
        }
        return true;
    }

    public static String getDirFromArgs(AndoidDBArgs andoidDBArgs){
        String dirPath = null;
        if(andoidDBArgs.outputPathUnset()){
            //write to current folder
            dirPath = "./";
        }
        else{
            //write to specified folder
            dirPath = andoidDBArgs.getOutputFilePath();
            File checkDir = new File(dirPath);
            if((checkDir.exists() && !checkDir.isDirectory()) || (!checkDir.exists())){
                try {
                    checkDir.mkdir();
                }catch (Exception e){
                    e.printStackTrace();
                    System.err.println("Fail to create dir " + dirPath);
                    return null;
                }
            }
            if(!dirPath.endsWith("/")){
                dirPath += "/";
            }
        }
        return dirPath;
    }

}
