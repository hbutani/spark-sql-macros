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

import sbt._
import sbt.Keys.fullClasspath
import sbtassembly.AssemblyPlugin.autoImport.{
  MergeStrategy,
  PathList,
  assemblyExcludedJars,
  assemblyMergeStrategy,
  assemblyOption
}
import sbtassembly.AssemblyKeys.assembly


object Assembly {

  def assemblyPredicate(d: Attributed[File]): Boolean = {
    true
  }

  lazy val assemblySettings =
    Seq(
      assemblyOption in assembly :=
        (assemblyOption in assembly).value.copy(includeScala = false),
      assemblyExcludedJars in assembly := {
        val cp = (fullClasspath in assembly).value
        cp filter assemblyPredicate
      },
      assemblyMergeStrategy in assembly := {
        case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
        case PathList("META-INF", "maven", ps @ _*) => MergeStrategy.first
        case PathList("META-INF", "services", ps @ _*) => MergeStrategy.first
        case PathList("com", "fasterxml", "jackson", "annotation", _*) => MergeStrategy.first
        case PathList(ps @ _*) if ps.last == "pom.properties" => MergeStrategy.first
        case x =>
          val oldStrategy = (assemblyMergeStrategy in assembly).value
          oldStrategy(x)
      }
    )

}
