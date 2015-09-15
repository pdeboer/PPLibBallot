name := "PPLibBallot"

version := "1.0"

lazy val `pplibballot` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.34",
  "org.scalikejdbc" %% "scalikejdbc"       % "2.2.2",
  "com.h2database"  %  "h2"                % "1.4.184",
  "org.scalikejdbc" %% "scalikejdbc-config"  % "2.2.2")

libraryDependencies ++= Seq( jdbc , anorm , cache , ws )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

