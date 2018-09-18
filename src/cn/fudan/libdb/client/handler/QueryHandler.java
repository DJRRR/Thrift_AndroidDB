package cn.fudan.libdb.client.handler;

import cn.fudan.libdb.client.LibDBArgs;
import cn.fudan.libdb.client.LibDBServiceClient;
import cn.fudan.libdb.util.FileUtil;
import org.apache.thrift.TException;

import static cn.fudan.libdb.client.LibDBArgs.libQueryCheck;
import static cn.fudan.libdb.client.LibDBArgs.repoTypeCheck;

/**
 * @author Dai Jiarun
 * @date 2018/9/18
 */
public class QueryHandler {

    // TODO: 2018/9/17 to collect all handlers separately

    public static void queryHandler(LibDBArgs libDBArgs, LibDBServiceClient client) throws TException {
        if(!(repoTypeCheck(libDBArgs) && libQueryCheck(libDBArgs))) {
            return;
        }
        if(libDBArgs.getRepoType().equals("apk") || libDBArgs.getRepoType().equals("apk-src")){
            throw new RuntimeException("Unsupported Functionality");
        }
        // TODO: 2018/9/17 the param "LIB_REPO" is not needed anymore
        String outputRes = client.queryLibsByGAV(libDBArgs.getGroupName(), libDBArgs.getArtifactId(), libDBArgs.getVersion(),
                "LIB_REPO", libDBArgs.isJsonOutput(), libDBArgs.getLimit());
        if(libDBArgs.outputPathUnset()){
            //command line print
            System.out.println(outputRes);
        }
        else{
            //save to file
            FileUtil.saveStrToFile(outputRes + "\n", libDBArgs.getOutputFilePath());
        }
    }

}
