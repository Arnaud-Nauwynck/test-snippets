#!/bin/bash
set -x

zkversion=3.6.1

cmdline="wget https://downloads.apache.org/zookeeper/zookeeper-${zkversion}/apache-zookeeper-${zkversion}-bin.tar.gz"
if [ ! -f apache-zookeeper-${zkversion}-bin.tar.gz ]; then
	echo "# .. ${cmdline}"
	${cmdline}
else
	echo "# .. skip ${cmdline}"
fi

if [ ! -d apache-zookeeper-bin ]; then
	tar zxf apache-zookeeper-${zkversion}-bin.tar.gz
	mv apache-zookeeper-${zkversion}-bin apache-zookeeper-bin
else
	echo "# skip tar zxf apache-zookeeper-${zkversion}-bin.tar.gz"
fi

zkHomeDir=$(pwd)/apache-zookeeper-bin

for i in 1 2 3; do
	if [ ! -d node${i} ]; then
		echo "configure noe${i}"
		mkdir "node${i}"
		( cd "node${i}"
		
		#ln -s ${zkHomeDir}/bin bin
		cp -rf ${zkHomeDir}/bin bin
		chmod u+x bin/*.sh
		rm bin/*.cmd
		
		ln -s ${zkHomeDir}/lib lib
		
		mkdir data
		mkdir conf
		cat <<EOF > conf/zoo.cfg
tickTime=2000
dataDir=$(pwd)/data
clientPort=$(( 2880 + ${i} ))
initLimit=5
syncLimit=2
server.1=localhost:2888:3888
server.2=localhost:2889:3889
server.3=localhost:2890:3890

EOF

		echo "${i}" > data/myid

		cp ${zkHomeDir}/conf/log4j.properties conf/log4j.properties
		)
	fi
done

	

