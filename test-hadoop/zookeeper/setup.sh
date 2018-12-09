

downloadMirrorSite=http://apache.mediamirrors.org/zookeeper

if [ ! -e stable.html ]
then
	echo ".. getting html download page /stable/"
	curl -ss -L ${downloadMirrorSite}/stable/ -o stable.html
fi


echo ".. extracting stable version from html page"
stableVersionFile=$( xmllint --html stable.html --xpath 'string(/html/body/pre/a[last()]/@href)' )
stableVersion="${stableVersionFile/.tar.gz/}"
echo ".. detected stableVersion:${stableVersion}"

downloadUrl="${downloadMirrorSite}/stable/${stableVersionFile}"
if [ ! -e "${stableVersion}.tar.gz" ]
then
	echo ".. downloading url: ${downloadUrl}"
	wget -q stableVersionFile ${downloadUrl} 
else
	echo ".. skip already downloaded ${downloadUrl}"
fi

if [ ! -e "${stableVersion}" ]
then
	echo ".. extracting file ${stableVersion}.tar.gz"
	tar zxf "${stableVersion}.tar.gz"
	if [ ! -e zookeeper-stable ]; then ln -s ${stableVersion} zookeeper-stable; fi
else
	echo ".. skip already extracted file ${stableVersion}.tar.gz"
fi


cd zookeeper-stable

echo "creating zookeeper data dir"
dataDir="$(pwd)/data"
mkdir -p data

cat conf/zoo_sample.cfg \
	| sed "s|dataDir=/tmp/zookeeper|dataDir=${dataDir}|g" \
	> conf/zoo.cfg

bin/zkServer.sh start

echo "test if running:"
bin/zkServer.sh status

# log => 
# ZooKeeper JMX enabled by default
# Using config: .../# bin/../conf/zoo.cfg
# Mode: standalone

# echo $?
# 0

# if not started...
# log => 
# ZooKeeper JMX enabled by default
# Using config: .../bin/../conf/zoo.cfg
# Error contacting service. It is probably not running.

# echo $?
# 1

bin/zkServer.sh status > /dev/null 2>&1; runningStatus=$?
if [ "$runningStatus" -eq 0 ]; then
	echo "running"
else
	echo "not running"
fi


echo ".. forinteractive test, type:"
echo "bin/zkCli.sh "

echo "for stop.. type:"
echo "bin/zkServer.sh stop"


