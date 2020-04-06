
echo "http://brooklyn.apache.org/#get-started"

curl -SL --output apache-brooklyn-0.12.0-bin.tar.gz "https://www.apache.org/dyn/closer.lua?action=download&filename=brooklyn/apache-brooklyn-0.12.0/apache-brooklyn-0.12.0-bin.tar.gz"
tar xvf apache-brooklyn-0.12.0-bin.tar.gz

cd apache-brooklyn-0.12.0-bin

./bin/start
