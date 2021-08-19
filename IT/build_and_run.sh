##
#  Copyright © 2021 Oscar Gómez (ogomezso0@gmail.com)
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#

#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

## Build the connector copy jar file to the host volume
cd "${DIR}/.." || exit
mvn clean install || exit

rm -rf ${DIR}/host-volumes/connectors && mkdir ${DIR}/host-volumes/connectors
cp ${DIR}/../target/*.tar.gz ${DIR}/host-volumes/connectors
cd ${DIR}/host-volumes/connectors/ || exit
tar -xf *.tar.gz || exit

## Run Compose
cd ${DIR} && docker compose up -d

