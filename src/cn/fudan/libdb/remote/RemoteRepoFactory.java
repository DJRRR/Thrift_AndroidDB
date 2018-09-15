package cn.fudan.libdb.remote;

import static cn.fudan.libdb.util.Constants.DEX_REPO;
import static cn.fudan.libdb.util.Constants.JAR_REPO;
import static cn.fudan.libdb.util.Constants.LIB_REPO;

/**
 * @author Dai Jiarun
 * @date 2018/7/5
 */
public class RemoteRepoFactory {

    public static RemoteRepo getRepo(String repoType) {
        switch (repoType) {
            case LIB_REPO:
                return LibRemoteRepo.getInstance();
            case JAR_REPO:
                return JarRemoteRepo.getInstance();
            case DEX_REPO:
                return DexRemoteRepo.getInstance();
            default:
                System.err.println("Error Repo Type!");
                return null;

        }
    }
}
