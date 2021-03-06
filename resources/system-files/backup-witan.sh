#!/bin/sh

# necessary for java location
export PATH=$PATH:/opt/mesosphere/bin
CASSANDRA_DIR=$(ps -A -o command | grep CassandraDaemon | grep '/var/lib' | cut -d' ' -f1 | sed -e 's#/jre1.*##')
CASSANDRA_DATA_DIR=/var/lib/cassandra/data
NODETOOL=$(find $CASSANDRA_DIR -name nodetool -type f -perm /u+x | grep bin/nodetool)
AWS=$(find /opt/mesosphere -name aws -type f -perm /u+x | grep bin/aws)
directory=$($NODETOOL snapshot witan | awk '/directory/{print $3}')

# We assume it's worked if it tells us the directory
if [ -z "$$$${directory}" ]; then
   echo "Problem getting snapshot directory"
   exit 1;
fi

# We only want the snapshots dirs
# For filters rationale see http://docs.aws.amazon.com/cli/latest/reference/s3/index.html#use-of-exclude-and-include-filters
sudo -u core $AWS s3 sync --exclude '*' --include '*/snapshots/*' $CASSANDRA_DATA_DIR s3://witan-cassandra-backup/$(hostname)/ --region eu-central-1
