#!/bin/bash

pushd `dirname $0` > /dev/null
SCRIPTPATH=`pwd`
popd > /dev/null

ROOTPATH=`dirname ${SCRIPTPATH}`

JARPATH="${ROOTPATH}/target/scala-2.11/regressr.jar"

CWD=`pwd`

if [ ! -f ${JARPATH} ] || [ "$1" == "-r" ]; then
    cd ${ROOTPATH}
    sbt test assembly
    if [[   $?  !=  0   ]]; then
        echo "SBT Assembly failed. Maybe there is a compilation/test case failure. Please take a look."
        exit $?
    fi
    if [ "$1" == "-r" ]; then
        shift 1
    fi
    cd ${CWD}
fi

if [ "$1" == "record" ]; then
    ABS_DIR_OF_STRATEGY_FILE=$(cd "$(dirname "$3")"; pwd)
    STRATEGY_FILE_NAME=$(basename $3)
    FULL_PATH=${ABS_DIR_OF_STRATEGY_FILE}/${STRATEGY_FILE_NAME}
    set -- "${@:1:2}" ${FULL_PATH}
fi

cd ${ROOTPATH}

java -jar ${JARPATH} "$@"
