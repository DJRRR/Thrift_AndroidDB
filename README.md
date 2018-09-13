# Thrift_LibDB
RPC. Third-party Libraries query & fetch
Dai Jiarun.
Fudan University. Whitzard Team

### Quick Start
all settings in `libdb.properties`

### Server
To start the server:
```
java -cp LibDB.jar cn.fudan.libdb.server.LibDBServiceServer
```

### Query(-q)
Query Third-party libraries by groupName, artifactId, version(at least one of these three labels is specified):
groupName: -g
artifactID: -a
version: -v
```
java -jar .\LibDB.jar  -q -g com.github.castorflex.smoothprogressbar
```

JSON format: -j
```
java -jar .\LibDB.jar  -q -j -g com.github.castorflex.smoothprogressbar
```

Output query res to file: -o
```
java -jar .\LibDB.jar  -q -j -g com.github.castorflex.smoothprogressbar -o ./queryres.txt
```

### Fetch(-f)
Fetch Third-party libraries by hash of .dex or .jar(treat .aar / .apklib as .jar temporarily)
hash key: -k
```
java -cp .\LibDB.jar cn.fudan.libdb.client.LibDBServiceClient -f -k 1ddc4f3804cdf219ae7feaf4647a5e1d79bfc1863208fac98cba54bf4b282994
```

for large amounts of fetching, create a .txt file to store all the hash values of the packages to be downloaded.
With --hashlist(-hl) option, LibDB provides multi-threading execution.
In this circumstance, the ----hashListType(-hlt) option is also needed to specify the type of the file(dex or jar)
Here jar stands for .jar, .aar and also .apklib. Even the file on server is .aar or .apklib, LibDB will rename it to .jar when executing.
Note that .dex file is hashed by SHA-256(64-bytes), while .jar(.aar/.apklib) file is hashed by MD5(32-byte).
hash values of some other length would not be accepted.

```
java -jar .\LibDB.jar  -f -hl .\hashlist.txt -hlt jar -o E:/testLIBDB/
```
An example of hashlist.txt, a hash key one per line.
```
1e04f3fb93ba00b59cdfa9228cffa59a
36145fee38e79b81035787f1be296a52
22202cc29a4e49e6642cbf06189186c6
a8627c801e0d16169ef9ca83cf89861a
```

### Other
For more information:
```
java -jar .\LibDB.jar  -h
```
Any questions, please email me : 18212010005@fudan.edu.cn




