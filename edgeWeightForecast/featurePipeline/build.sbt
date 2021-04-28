name := "EdgeFeatureGen"
version := "0.1"
scalaVersion := "2.11.12"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" %"2.1.0",
  "org.apache.spark" %% "spark-sql" % "2.4.0",
  "com.databricks" %% "spark-csv" % "1.5.0"
)