/*
This project is a simple example of how to use the Databricks Connect Scala client to run on
serverless or on a Databricks cluster.
 */
package com.examples

import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets

import com.databricks.connect.DatabricksSession
import org.apache.spark.sql.{SparkSession, functions => F}
import org.apache.spark.sql.functions.udf

object Main {
  private def addArtifactsFromClasspathFile(spark: SparkSession, classpathFile: String): Unit = {
    val path = Paths.get(classpathFile)
    if (Files.exists(path)) {
      val classpath = new String(Files.readAllBytes(path)).trim
      classpath.split(":").foreach { jar =>
        val f = new java.io.File(jar)
        if (f.exists() && f.getName.endsWith(".jar")) {
          println(s"Adding artifact: $jar")
          try {
            spark.addArtifact(jar)
          } catch {
            case e: Exception =>
              println(s"Failed to add $jar: ${e.getMessage}")
          }
        }
      }
    } else {
      println(s"Classpath file not found: $classpathFile")
    }
  }

  def main(args: Array[String]): Unit = {
    println("Hello, World!")

    val spark = getSession()
//    addArtifactsFromClasspathFile(spark, "classpath.txt")
    println("Showing range ...")
    spark.range(3).show()

    println("Showing nyctaxi trips ...")
    val nycTaxi = new NycTaxi(spark)
    val df = nycTaxi.trips()
    df.show()
  }

  def getSession(): SparkSession = {
    // Get DATABRICKS_RUNTIME_VERSION environment variable
    if (sys.env.contains("DATABRICKS_RUNTIME_VERSION")) {
      println("Running in a Databricks cluster")
      SparkSession.active
    } else {
      println("Running outside Databricks")
      DatabricksSession.builder()
        .serverless()
//        .clusterId("0610-080818-ys2al2qw")
        .validateSession(false)
        .addCompiledArtifacts(Main.getClass.getProtectionDomain.getCodeSource.getLocation.toURI)
        .getOrCreate()
    }
  }
}
