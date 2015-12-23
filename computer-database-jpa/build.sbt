import play.Project._

name := "computer-database-jpa"

version := "1.0"

libraryDependencies ++= Seq(
  "org.hibernate" % "hibernate-entitymanager" % "4.1.9.Final",
  "org.hibernate" % "hibernate-core" % "4.1.9.Final",
  "org.hibernate.javax.persistence" % "hibernate-jpa-2.0-api" % "1.0.1.Final",
  "org.activiti" % "activiti-engine" % "5.14",
  "org.activiti" % "activiti-spring" % "5.14",
  "org.activiti" % "activiti-cdi" % "5.14",
  "org.activiti" % "activiti-json-converter" % "5.14",
  "commons-io" % "commons-io" % "1.3.2",
  "org.springframework" % "spring-context" % "3.1.2.RELEASE",
  "org.springframework" % "spring-orm" % "3.1.2.RELEASE",
  "org.springframework" % "spring-jdbc" % "3.1.2.RELEASE",
  "org.springframework" % "spring-tx" % "3.1.2.RELEASE",
  "com.h2database" % "h2" % "1.3.175"
  )

playJavaSettings
				
