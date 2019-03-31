# ethop
eshop project source code
The first time you pull this code, you may meet an exception is "can't find com.alipay.sdk jar" at the pom file, this caused by the project used third party jar which can't be found at Maven repository, so need install the jars to local maven repository by manully. 

Below are the steps for how to install jars to local maven repository:
1，Get the jars under ~/resource/alipay/
2, Copy the jars to local folder
3，win+R enter the dos command and execute these two commandsl:
mvn install:install-file "-DgroupId=com.alipay.sdk" "-DartifactId=alipay_sdk" "-Dversion=20161213" "-Dpackaging=jar" "-Dfile=alipay-sdk-java20161213173952.jar" 
mvn install:install-file "-DgroupId=com.alipay.trade" "-DartifactId=alipay_trade" "-Dversion=20161213" "-Dpackaging=jar" "-Dfile=alipay-trade-sdk-20161215.jar"
4,Check maven pom if the jars be added like following
<dependency>
	<groupId>com.alipay.sdk</groupId>
	<artifactId>alipay_sdk</artifactId>
	<version>20161213</version>
</dependency>
<dependency>
	<groupId>com.alipay.trade</groupId>
	<artifactId>alipay_trade</artifactId>
	<version>20161213</version>
</dependency>
