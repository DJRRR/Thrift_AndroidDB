namespace java service.demo

typedef i32 int
typedef i64 long

service LibDBService{
    /*
    * test if server is alive
    */
    int ping(1:int test)

   /*
   * query lib info by groupName, artifactId, version
   * repoType : (1)General, (2)Only Dex Available, (3)Only Jar available
   * jsonOutput : True or False
   * limit : Integer
   */
   string queryLibsByGAV(1:string groupName 2:string artifactId 3:string version 4:string repoType 5:bool jsonOutput 6:int limit)

   /*
   * download a lib package from server(dex or jar(including aar & apklib))
   */
   binary fetch(1:string hash)

}