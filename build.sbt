name := """play-java-seed"""
organization := "my-play-app"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.18"

libraryDependencies += guice
libraryDependencies += "com.google.protobuf" % "protobuf-java" % "4.34.0"
