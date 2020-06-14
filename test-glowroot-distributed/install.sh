
set -x

cassandraVersion=3.11.6
glowrootVersion=0.13.6

echo "see https://github.com/glowroot/glowroot/wiki/Central-Collector-Installation"

( 
echo "installing cassandra"
mkdir -p cassandra
cd cassandra
if [ ! -e cassandra-bin ]; then
    if [ ! -e cassandra-bin.tar.gz ]; then
        wget https://downloads.apache.org/cassandra/${cassandraVersion}/apache-cassandra-${cassandraVersion}-bin.tar.gz cassandra-bin.tar.gz
    fi
    tar zxf cassandra-bin.tar.gz
    mv apache-cassandra-${cassandraVersion} cassandra-bin
fi
)


( 
echo "installing glowroot-central"
mkdir -p glowroot-central
cd glowroot-central
if [ ! -e glowroot-central ]; then
    if [ ! -e glowroot-central-dist.zip ]; then
        wget https://github.com/glowroot/glowroot/releases/download/v${glowrootVersion}/glowroot-central-${glowrootVersion}-dist.zip glowroot-central-dist.zip
    fi
    unzip glowroot-central-dist.zip
fi
    
)


(
echo "installing glowroot-jvmagent"
mkdir -p glowroot-jvmagent
cd glowroot-jvmagent
if [ ! -e glowroot ]; then
    if [ ! -e glowroot-dist.zip ]; then
        wget https://github.com/glowroot/glowroot/releases/download/v${glowrootVersion}/glowroot-${glowrootVersion}-dist.zip glowroot-dist.zip
    fi
    unzip glowroot-dist.zip
fi
)
