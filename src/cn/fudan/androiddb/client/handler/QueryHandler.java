package cn.fudan.androiddb.client.handler;

import cn.fudan.androiddb.client.AndroidDBServiceClient;
import cn.fudan.androiddb.client.AndoidDBArgs;
import cn.fudan.androiddb.util.FileUtil;
import org.apache.thrift.TException;

import static cn.fudan.androiddb.client.AndoidDBArgs.libQueryCheck;
import static cn.fudan.androiddb.client.AndoidDBArgs.repoTypeCheck;

/**
 * @author Dai Jiarun
 * @date 2018/9/18
 */
public class QueryHandler {

    // TODO: 2018/9/17 to collect all handlers separately

    public static void queryHandler(AndoidDBArgs andoidDBArgs, AndroidDBServiceClient client) throws TException {
        if(!(repoTypeCheck(andoidDBArgs) && libQueryCheck(andoidDBArgs))) {
            return;
        }
        if(andoidDBArgs.getRepoType().equals("apk") || andoidDBArgs.getRepoType().equals("apk-src")){
            throw new RuntimeException("Unsupported Functionality");
        }
        // TODO: 2018/9/17 the param "LIB_REPO" is not needed anymore
        String outputRes = client.queryLibsByGAV(andoidDBArgs.getGroupName(), andoidDBArgs.getArtifactId(), andoidDBArgs.getVersion(),
                "LIB_REPO", andoidDBArgs.isJsonOutput(), andoidDBArgs.getLimit());
        if(andoidDBArgs.outputPathUnset()){
            //command line print
            System.out.println(outputRes);
        }
        else{
            //save to file
            FileUtil.saveStrToFile(outputRes + "\n", andoidDBArgs.getOutputFilePath());
        }
    }

}
