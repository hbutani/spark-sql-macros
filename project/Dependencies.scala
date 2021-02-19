/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import sbt.{ModuleID, _}

object Dependencies {
  import Versions._

  object scala {
    val dependencies = Seq(
      "org.scala-lang.modules" %% "scala-xml" % scalaXMLVersion % "provided",
      "org.scala-lang" % "scala-compiler" % scalaVersion % "provided",
      "org.scala-lang" % "scala-reflect" % scalaVersion % "provided",
      "org.scala-lang.modules" %% "scala-parser-combinators" % scalaParseCombVersion % "provided")
  }

  object spark {
    val dependencies = Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-hive" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-hive" % sparkVersion % "test" classifier "tests",
      "org.apache.spark" %% "spark-hive-thriftserver" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-repl" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-unsafe" % sparkVersion % "provided")
  }

  object utils {
    val dependencies = Seq(
      "org.json4s" %% "json4s-jackson" % json4sVersion % "provided",
      "org.slf4j" % "slf4j-api" % slf4jVersion % "provided",
      "org.slf4j" % "slf4j-log4j12" % slf4jVersion % "provided",
      "org.slf4j" % "jul-to-slf4j" % slf4jVersion % "provided",
      "org.slf4j" % "jcl-over-slf4j" % slf4jVersion % "provided",
      "log4j" % "log4j" % log4jVersion % "provided")
  }

  object test_infra {
    val dependencies = Seq(
      "org.scalatest" %% "scalatest" % scalatestVersion % "test",
      "org.apache.hadoop" % "hadoop-client" % hadoopVersion % "test",
      "org.apache.derby" % "derby" % derbyVersion % "test",
      "org.scalacheck" %% "scalacheck" % "1.14.1" % "test")
  }
}
