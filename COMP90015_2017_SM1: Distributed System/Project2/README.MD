Compile:

mvn clean install'

How to run client:

java -cp EZShare.jar EZShare.Client

How to run server:

java -cp EZShare.jar EZShare.Server

Sample runs:

Servers
java -cp EZShare.jar EZShare.Server -secret 2os41f58vkd9e1q4ua6ov5emlv -port 50185 -connectionintervallimit 0 -exchangeinterval 60000 -debug
java -cp EZShare.jar EZShare.Server -secret 2os41f58vkd9e1q4ua6ov5emlv -port 50001 -connectionintervallimit 0 -exchangeinterval 60000 -debug
java -cp EZShare.jar EZShare.Server -secret 2os41f58vkd9e1q4ua6ov5emlv -port 50002 -connectionintervallimit 0 -exchangeinterval 60000 -debug
java -cp EZShare.jar EZShare.Server -secret 2os41f58vkd9e1q4ua6ov5emlv -port 50185 -connectionintervallimit 0 -exchangeinterval 60000 -debug -secure -sport 50001

exchange
java -cp EZShare.jar EZShare.Client -exchange -servers localhost:50001,localhost:50002,localhost:50185 -debug

publish 1
java -cp EZShare.jar EZShare.Client -publish -name "Unimelb website" -description "The main page for the University of Melbourne" -uri http://www.unimelb.edu.au -tags web,html -debug -channel ymca

publish 2
java -cp EZShare.jar EZShare.Client -publish -name "Google website" -description "The main page for the Googl" -uri http://www.google.com -tags web,html -debug -channel ymca

query
java -cp EZShare.jar EZShare.Client -query -channel ymca -debug

query no owner
java -cp EZShare.jar EZShare.Client -query -debug

share
java -cp EZShare.jar EZShare.Client -share -uri file:/home/ubuntu/ok.png -name "OK" -description "Success!" -tags jar -channel ymca -owner ymca -secret 2os41f58vkd9e1q4ua6ov5emlv -debug -secret 2os41f58vkd9e1q4ua6ov5emlv

fetch
java -cp EZShare.jar EZShare.Client -fetch -channel ymca -uri file:/home/ubuntu/ok.png -debug

subscribe
java -cp EZShare.jar EZShare.Client -subscribe -debug

-------------

Keystore: keystore.jks
Password: password
Alias(es): selfsigned / selfsigned_client
Signed aliases: signed / signed_client 