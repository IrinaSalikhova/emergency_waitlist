name := """play-java-seed"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.10"

libraryDependencies += guice

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "8.0.31")

import NativePackagerHelper._
mappings in Universal ++= directory("public")