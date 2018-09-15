# Thrift_LibDB
RPC. Third-party Libraries query & fetch

Author: **Dai Jiarun**.

Organization: **Fudan University. Whitzard Team**

### Quick Start
### Configurations
* all configuration details stored in `libdb.properties`.

### Server

* To start the server:

```
java -cp LibDB.jar cn.fudan.libdb.server.LibDBServiceServer
```

### Repo Type: `-r`

* Third-party libraries: `-r lib`
* Android applications: `-r apk`   **Unsupported yet**

### Query: `-q`

* Query Third-party libraries by groupName, artifactId, version(at least one of these three labels is specified):

* groupName: `-g`

* artifactID: `-a`

* version: `-v`

```
java -jar .\LibDB.jar -r lib -q -g com.github.castorflex.smoothprogressbar
```

* JSON format: `-j`

```
java -jar .\LibDB.jar -r lib -q -j -g com.github.castorflex.smoothprogressbar
```

* Output query res to file: `-o`

```
java -jar .\LibDB.jar -r lib -q -j -g com.github.castorflex.smoothprogressbar -o ./queryres.txt
```

### Fetch: `-f`

* Fetch Third-party libraries by hash of .dex, .jar, .aar, .apklib

* hash key: `-k`

* Note that `.dex` file is hashed by `SHA-256(64-bytes)`, while `.jar`(`.aar` / `.apklib`) file is hashed by `MD5(32-byte)`.

Hash values of some other length would not be accepted.

```
java -jar .\LibDB.jar -r lib -f -k 1ddc4f3804cdf219ae7feaf4647a5e1d79bfc1863208fac98cba54bf4b282994
```

For large amounts of fetching, create a `.txt` file to store all the hash values of the packages to be downloaded.

With `--hashlist(-hl)` option set, LibDB provides multi-threading execution.

```
java -jar .\LibDB.jar  -f -hl .\hashlist.txt -hlt jar -o E:/testLIBDB/
```

* An example of `hashlist.txt`, a hash key one per line.

```
1e04f3fb93ba00b59cdfa9228cffa59a
36145fee38e79b81035787f1be296a52
22202cc29a4e49e6642cbf06189186c6
a8627c801e0d16169ef9ca83cf89861a
54e9eea1a0eb38f23bb5cccfc7d846e8e6917fcb3cbe85f5c0e36c2ea59430f5
267d5bf786e180b95e8a27c8e40cf2dea846ea85cdad8cff56e25cef45b36c54
dce309c9c43165d712be82dc4dfa1666a2f52c9811645a0862944c28408fc6c3
73e982c68c45411ce68be83f90133ce3cace9a0f696d29880924cac17e87c035
d67a36c42c44b74dac43fd487c2800cb7675275d1d375e9176639b534017eb7a
faabee4c188fb1893d42aebe60860be34695a25a41eec805482bc4a1889d4e4a
fbfaf15d83a842bec8c7ab2e9b5ec69bfd616a57cf602e29e09f63f628ae19b8
```

### Furture Development

* (1) Add Apk Repo
* (2) Add crawlers for both libs and apks(Need to be authorized)
* (3) file upload(Need to be authorized)
* (4) libs analysis
* (5) …………

### Other

* For more information:

```
java -jar .\LibDB.jar  -h
```

* Any questions, please email me : 18212010005@fudan.edu.cn




