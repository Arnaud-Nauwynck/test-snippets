test for Maven Takari Smart-Builder

see http://takari.io/book/30-team-maven.html#takari-smart-builder


curl -O http://repo1.maven.org/maven2/io/takari/maven/takari-smart-builder/0.4.0/takari-smart-builder-0.4.0.jar
mv takari-smart-builder-0.4.0.jar $M2_HOME/lib/ext


run with
	mvn package --builder smart -T1.0C
	
or in .mvn/maven.config
	--builder smart -T1.0C
	
See in logs:

[INFO]
[INFO] Using the SmartBuilder implementation with a thread count of 8
..


For the demo, there is a maven ant plugin, which sleep 5 seconds in phase "compile"


The project reactor dependencies is:

+ parent
 <- module1 
     <--depends-- module1a
 <- module2 
     <--depends-- module2a
 <- module3 
     <--depends-- module3a

It can be topologically sorted, and executed with 3 threads..


[INFO] test-mvn-takari-smart-builder-parent ............... SUCCESS [  5.723 s]
[INFO] module1a ........................................... SUCCESS [  6.227 s]
[INFO] module1 ............................................ SUCCESS [  5.307 s]
[INFO] module2a ........................................... SUCCESS [  6.228 s]
[INFO] module2 ............................................ SUCCESS [  5.369 s]
[INFO] module3a ........................................... SUCCESS [  6.228 s]
[INFO] module3 ............................................ SUCCESS [  5.309 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  17.512 s (Wall Clock)



Without "--builder smart -T1.0C", the module steps are executed SEQUENTIALLY (mono thread) => 37 seconds instead of 18 !

[INFO] test-mvn-takari-smart-builder-parent ............... SUCCESS [  5.638 s]
[INFO] module1a ........................................... SUCCESS [  6.054 s]
[INFO] module1 ............................................ SUCCESS [  5.187 s]
[INFO] module2a ........................................... SUCCESS [  5.148 s]
[INFO] module2 ............................................ SUCCESS [  5.143 s]
[INFO] module3a ........................................... SUCCESS [  5.157 s]
[INFO] module3 ............................................ SUCCESS [  5.189 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  37.674 s


