package cn.fudan.libdb.client;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dai Jiarun
 * @date 2018/7/6
 */
public class LibDBArgs {
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

}
