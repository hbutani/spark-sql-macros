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

package org.apache.spark.sql

import org.scalatest.{fixture, BeforeAndAfterAll}

import org.apache.spark.internal.Logging
import org.apache.spark.sql.hive.test.sqlmacros.TestSQLMacrosHive
import org.apache.spark.sql.sqlmacros.SQLMacroExpressionBuilder

abstract class AbstractTest
  extends fixture.FunSuite
    with fixture.TestDataFixture
    with BeforeAndAfterAll
    with Logging {

  import scala.tools.reflect.ToolBox
  import org.apache.spark.sql.catalyst.ScalaReflection._
  import universe._

  protected val tb = mirror.mkToolBox()

  protected def eval(fnTree : Tree) : Either[_, SQLMacroExpressionBuilder] = {
    tb.eval(
      q"""{
          new org.apache.spark.sql.defineMacros.SparkSessionMacroExt(
             org.apache.spark.sql.hive.test.sqlmacros.TestSQLMacrosHive.sparkSession
             ).udm(${fnTree})
          }
        """).asInstanceOf[Either[_, SQLMacroExpressionBuilder]]
  }

  protected def register(nm : String, fnTree : Tree): Unit = {
    tb.eval(
      q"""{
          import org.apache.spark.sql.defineMacros._
          val ss = org.apache.spark.sql.hive.test.sqlmacros.TestSQLMacrosHive.sparkSession
          ss.registerMacro($nm,ss.udm(${fnTree}))
        }"""
    )
  }

  // scalastyle:off println
  protected def handleMacroOutput(r: Either[Any, SQLMacroExpressionBuilder]) = {
    r match {
      case Left(fn) => println(s"Failed to create expression for ${fn}")
      case Right(fb) =>
        val s = fb.macroExpr.treeString(false).split("\n").
          map(s => if (s.length > 100) s.substring(0, 97) + "..." else s).mkString("\n")
        println(
          s"""Spark SQL expression is
             |${fb.macroExpr.sql}""".stripMargin)
    }
  }

  def printOut(s : => String) : Unit = {
    println(s)
  }

  // scalastyle:on


  override def beforeAll(): Unit = {
    TestSQLMacrosHive.sql(
      """create table if not exists unit_test(
        |  c_varchar2_40 string,
        |  c_number decimal(38,18),
        |  c_int int
        |)
        |using parquet""".stripMargin
    )
  }
}
