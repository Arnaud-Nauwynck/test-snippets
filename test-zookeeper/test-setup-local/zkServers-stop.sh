#!/bin/bash

for i in 1 2 3 ; do
	node${i}/bin/zkServer.sh stop
done

