#!/bin/bash

targetpath="backend/target"
jarfile=`ls $targetpath/agenspop*.jar`
#echo $jarfile
cfgfile=`ls $targetpath/*.yml`
cfgname="${cfgfile%.yml}"
#echo $cfgfile : $cfgname

if [ ! -f $jarfile ]; then
  echo "ERROR: not exist agenspop jar file in ./target/ \nTry build and start again.." >&2;
  exit 1;
fi

echo "Run target jar: $jarfile ($cfgname)"
nohup java -Xms2g -Xmx2g -jar $jarfile --spring.config.name=$cfgname > $targetpath/agenspop.log 2>&1 &
sleep 2


cd frontend/src/main/frontend
# frontend: background running (node process)
forever start node_modules/@angular/cli/bin/ng serve
sleep 1
cd ../../../..

echo ""
echo "open browser, ex) http://localhost:28082/workspace/D67109666"
echo "[STOP]  $ forever stopall"
echo ""
