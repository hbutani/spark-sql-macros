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

abstract class AbstractTest
  extends fixture.FunSuite
    with fixture.TestDataFixture
    with BeforeAndAfterAll
    with Logging {

  override def beforeAll(): Unit = {
    TestSQLMacrosHive.sql(
      """create table unit_test(
        |  c_varchar2_40 string,
        |  c_number decimal(38,18),
        |  c_int int
        |)
        |using parquet""".stripMargin
    )
  }
}
