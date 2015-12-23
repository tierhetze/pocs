import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "dpostserver"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "org.eclipse.jetty" % "jetty-http" % "9.0.0.M3",
    "org.eclipse.jetty" % "jetty-io" % "9.0.0.M3",
    "org.eclipse.jetty" % "jetty-server" % "9.0.0.M3" exclude("org.eclipse.jetty.orbit", "javax.servlet"),
    "org.eclipse.jetty" % "jetty-util" % "9.0.0.M3",
    "junit" % "junit" % "4.11" % "test",
    "com.sun.mail" % "javax.mail" % "1.4.5",
    "com.sun.mail" % "smtp" % "1.4.5",
    "javax.servlet" % "javax.servlet-api" % "3.0.1", 
    "commons-dbcp" % "commons-dbcp" % "1.4",
    "com.googlecode.mapperdao" % "mapperdao" % "1.0.0.rc21-2.10.1",
    "postgresql" % "postgresql" % "9.1-901.jdbc4"
    
  )



  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += "javax-servlet-api-repo" at "http://repo1.maven.org/maven2",
    resolvers += "sonatype.releases" at "http://oss.sonatype.org/content/repositories/releases"
  )

}
