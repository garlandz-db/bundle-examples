mkdir lib
pushd lib
databricks fs cp --profile azdf dbfs:/Volumes/main/garland/scala_job_test/dbconnect-scala-17.0.0_2.13-dist.tar.gz .
tar -xvzf dbconnect-scala-17.0.0_2.13-dist.tar.gz
popd

sbt 'export Compile/fullClasspath' | grep -o '/[^ ]*\.jar' | tr '\n' ':' | sed 's/:$//'  | grep -o '/[^ ]*\.jar' > classpath.txt

