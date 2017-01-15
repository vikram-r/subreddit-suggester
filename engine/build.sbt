name := "engine"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.1",
  "com.typesafe.akka" %% "akka-http" % "10.0.1",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.4",
  "junit" % "junit" % "4.12"
)


// "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test

//com.vikram.subredditsuggester.client.id=G7_Lv9YNO8oIDA
//com.vikram.subredditsuggester.client_secret=nicetry
//com.vikram.subredditsuggester.redirect_uri=https://127.0.0.1:65010/authorize_callback