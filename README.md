# TorrentAD

## About

설정파일을 기반으로 토렌트 사이트에서 원하는 토렌트 파일을 자동으로 다운로드 받고, 이를 Transmission으로 전달해주는 프로그램

## Troubleshooting

1. Java PKIX path building failed: 에러 해결

    * 개요

    Java 에서 HTTPS 로 다른 사이트에 연결할 경우 다음과 같이 "ValidatorException: PKIX path building failed" 에러를 만나는 경우가 있습니다.

    ```java
    Caused by: javax.naming.CommunicationException: simple bind failed: <server-name>
    [Root exception is javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed:
    sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target\
    ```

    대부분의 원인은 원격 사이트에서 사용하는 SSL 인증서 정보가 현재 JVM 의 신뢰하는 인증기관 인증서 목록에 등록되어 있지 않기 때문이며 JVM의 keystore에 인증서를 넣어주고 재구동하면 해결됩니다.

    * 처리

    먼저 github에 올려 놓은 소스를 다운로드 받습니다.
    ```bash
    curl -O https://gist.githubusercontent.com/lesstif/cd26f57b7cfd2cd55241b20e05b5cd93/raw/InstallCert.java
    ```
    다운받은 소스를 컴파일합니다.
    ```bash
    javac InstallCert.java
    ```
    installCert 를 연결하려는 사이트 이름(예: torrentsome4.com)과 함께 실행합니다.
    ```bash
    java -cp ./ InstallCert torrentsome4.com
    ```
    서버가 2 개의 인증서를 전송했는데 보통 아래에 있는게 CA 의 인증서이며 이 경우 2번째가 Let's Encrypt 의 CA 인증서이므로 2번을 선택해서 저장해야 합니다.
    ```bash
    No errors, certificate is already trusted

    Server sent 2 certificate(s):

     1 Subject CN=torrentsome4.com
       Issuer  CN=Let's Encrypt Authority X3, O=Let's Encrypt, C=US
       sha1    c6 9e d1 7a ed 37 45 04 98 36 43 07 dd e1 77 cd 8f a3 51 0e 
       md5     bc f8 da 1f f9 38 a0 c4 45 49 0e 33 32 ca 61 53 

     2 Subject CN=Let's Encrypt Authority X3, O=Let's Encrypt, C=US
       Issuer  CN=DST Root CA X3, O=Digital Signature Trust Co.
       sha1    e6 a3 b4 5b 06 2d 50 9b 33 82 28 2d 19 6e fe 97 d5 95 6c cb 
       md5     b1 54 09 27 4f 54 ad 8f 02 3d 3b 85 a5 ec ec 5d 

    Enter certificate to add to trusted keystore or 'q' to quit: [1]
    2
    ```
    다음과 같은 메시지가 나오고 저장되는데 keystore(jssecacerts) 명과 alias(torrentsome4.com-2) 명을 기억합니다.
    ```bash
    Added certificate to keystore 'jssecacerts' using alias 'torrentsome4.com-2'
    ```
    이제 생성된 keystore 파일 jssecacerts 에 있는 인증서를 output.cert 라는 파일로 저장합니다. -alias 옵션 뒤에는 위에서 표시한 alias 이름을 주어야 합니다.
    ```bash
    keytool -exportcert -keystore jssecacerts -storepass changeit -file output.cert -alias torrentsome4.com-2
    ```
    아래 명령어로 JVM 의 keystore 에 CA 인증서를 추가합니다.
    ```bash
    sudo  keytool -importcert -keystore /usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/jre/lib/security/cacerts -storepass changeit -file output.cert -alias letsencrypt
    ```
    만약 해당 label 이 있다면 다음 에러가 발생합니다.
    ```java
    keytool error: java.lang.Exception: Certificate not imported, alias <letsencrypt> already exists
    ```
    이 경우 아래 명령어로 기존 label 을 삭제합니다..
    ```bash
    sudo keytool -delete  -keystore /usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/jre/lib/security/cacerts -storepass changeit  -alias letsencrypt
    ```


