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

package org.apache.spark.sql.hive.test.sqlmacros

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.internal.config
import org.apache.spark.internal.config.UI.UI_ENABLED
import org.apache.spark.sql.catalyst.optimizer.ConvertToLocalRelation
import org.apache.spark.sql.hive.HiveUtils
import org.apache.spark.sql.hive.test.TestHiveContext
import org.apache.spark.sql.internal.SQLConf
import org.apache.spark.sql.internal.StaticSQLConf.WAREHOUSE_PATH

object SQLMacrosTestConf {

  lazy val localConf = new SparkConf()
    .set("spark.sql.test", "")
    .set(SQLConf.CODEGEN_FALLBACK.key, "false")
    .set(
      HiveUtils.HIVE_METASTORE_BARRIER_PREFIXES.key,
      "org.apache.spark.sql.hive.execution.PairSerDe")
    .set(WAREHOUSE_PATH.key, TestHiveContext.makeWarehouseDir().toURI.getPath)
    // SPARK-8910
    .set(UI_ENABLED, false)
    .set(config.UNSAFE_EXCEPTION_ON_MEMORY_LEAK, true)
    // Hive changed the default of hive.metastore.disallow.incompatible.col.type.changes
    // from false to true. For details, see the JIRA HIVE-12320 and HIVE-17764.
    .set("spark.hadoop.hive.metastore.disallow.incompatible.col.type.changes", "false")
    // Disable ConvertToLocalRelation for better test coverage. Test cases built on
    // LocalRelation will exercise the optimization rules better by disabling it as
    // this rule may potentially block testing of other optimization rules such as
    // ConstantPropagation etc.
    .set(SQLConf.OPTIMIZER_EXCLUDED_RULES.key, ConvertToLocalRelation.ruleName)
    /*
    Uncomment to see Plan rewrites
    .set("spark.sql.planChangeLog.level", "ERROR")
    .set(
      "spark.sql.planChangeLog.rules",
      "org.apache.spark.sql.execution.datasources.PruneFileSourcePartitions," +
        "org.apache.spark.sql.execution.datasources.v2.V2ScanRelationPushDown," +
        "org.apache.spark.sql.catalyst.optimizer.RewritePredicateSubquery," +
        "org.apache.spark.sql.catalyst.optimizer.PullupCorrelatedPredicates")
     */
  /* Use these settings to turn off some of the code generation
    .set("spark.sql.codegen.factoryMode", "NO_CODEGEN")
    .set("spark.sql.codegen.maxFields", "0")
    .set("spark.sql.codegen.wholeStage", "false")
   */

    def testMaster: String = "local[*]"

}

object TestSQLMacrosHive
    extends TestHiveContext(
      new SparkContext(
        System.getProperty("spark.sql.test.master", SQLMacrosTestConf.testMaster),
        "TestSQLContext",
        SQLMacrosTestConf.localConf),
      false)
