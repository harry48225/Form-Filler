@echo off
javac -cp  ".;jcommon-1.0.0.jar;jfreechart-1.0.1.jar;LGoodDatePicker-10.3.1.jar;google-maps-services-java-0.9.1-0.9.1.jar" *.java 
cd components 
javac -cp  ".;jcommon-1.0.0.jar;jfreechart-1.0.1.jar;LGoodDatePicker-10.3.1.jar;google-maps-services-java-0.9.1-0.9.1.jar" *.java  
cd ..
 pause