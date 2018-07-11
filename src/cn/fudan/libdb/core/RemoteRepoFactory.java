package cn.fudan.libdb.core;

/**
 * @author Dai Jiarun
 * @date 2018/7/5
 */
public class RemoteRepoFactory {
    public static final String GENERAL_REPO = "GENERAL_REPO";
    public static final String JAR_REPO = "JAR_REPO";
    public static final String DEX_REPO = "DEX_REPO";

    public static RemoteRepo getRepo(String repoType) {
        switch (repoType) {
            case GENERAL_REPO:
                return GeneralRemoteRepo.getInstance();
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
