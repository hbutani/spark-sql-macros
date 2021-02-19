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

/**
 * Spark SQL Macros provides a capability to register custom functions into a [[SparkSession]].
 * This is similar to [[UDFRegistration]]. The difference being SQL Macro attempts to generate
 * an equivalent [[Expression]] for the function body.
 *
 * Given a function registration:
 * {{{
 *   spark.udf.register("intUDF", (i: Int) => {
 *val j = 2
 *i + j
 *})
 * }}}
 * The following query(assuming `sparktest.unit_test` has a column `c_int : Int`):
 * {{{
 *   select intUDF(c_int)
 *   from unit_test
 *   where intUDF(c_int) > 1
 * }}}
 * generates the following physical plan:
 * {{{
    +-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
    |plan
    +----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
    |== Physical Plan ==
     *(1) Project [if (isnull(c_int#9)) null else intUDF(knownnotnull(c_int#9)) AS intUDF(c_int)#10]
    +- *(1) Filter (if (isnull(c_int#9)) null else intUDF(knownnotnull(c_int#9)) > 1)
       +- *(1) ColumnarToRow
          +- FileScan parquet default.unit_test[c_int#9] Batched: true, DataFilters: [(if (isnull(c_int#9)) null else intUDF(knownnotnull(c_int#9)) > 1)], Format: Parquet, Location: InMemoryFileIndex[file:/private/var/folders/qy/qtpc2h2n3sn74gfxpjr6nqdc0000gn/T/warehouse-8b1e79b..., PartitionFilters: [], PushedFilters: [], ReadSchema: struct<c_int:int>

    |
    +--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
 *
 * }}}
 * The `intUDF` is invoked in the `Filter operator` for evaluating the `intUDF(c_int) > 1` predicate;
 * and in the `Project operator` to evaluate the projection `intUDF(c_int)`
 *
 * But the `intUDF` is a trivial function that just adds `2` to its argument.
 * With Spark SQL Macros you can register the function as a macro like this:
 * {{{
 *
 * import org.apache.spark.sql.defineMacros._
 *
 * spark.registerMacro("intUDM", spark.udm((i: Int) => {
 * val j = 2
 * i + j
 * }))
 * }}}
 * The query:
 * {{{
 *  select intUDM(c_int)
 *  from sparktest.unit_test
 *  where intUDM(c_int) < 0
 * }}}
 * generates the following physical plan:
 * {{{
    +-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
    |plan
    +----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
    |== Physical Plan ==
     *(1) Project [(c_int#9 + 1) AS (c_int + 1)#27]
    +- *(1) Filter (isnotnull(c_int#9) AND ((c_int#9 + 1) > 1))
       +- *(1) ColumnarToRow
          +- FileScan parquet default.unit_test[c_int#9] Batched: true, DataFilters: [isnotnull(c_int#9), ((c_int#9 + 1) > 1)], Format: Parquet, Location: InMemoryFileIndex[file:/private/var/folders/qy/qtpc2h2n3sn74gfxpjr6nqdc0000gn/T/warehouse-8b1e79b..., PartitionFilters: [], PushedFilters: [IsNotNull(c_int)], ReadSchema: struct<c_int:int>

    |
    +--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
 * }}}
 * The predicate `intUDM(c_int) < 0` becomes `("C_INT" + 1) < 0`
 * and the projection `intUDM(c_int)` becomes `"C_INT" + 2`.
 *
 * '''DESIGN NOTES'''
 *
 * '''Injection of Static Values:'''
 * We allow macro call-site static values to be used in the macro code.
 * These values need to be translated to catalyst expression trees.
 * Spark's [[org.apache.spark.sql.catalyst.ScalaReflection]] and already
 * provides a mechanism for inferring and converting to catalyst expressions
 * (via [[org.apache.spark.sql.catalyst.encoders.ExpressionEncoder]]s)
 * values of supported types. We leverage
 * this mechanism. But in order to leverage it we need to stand-up
 * a runtime Universe inside the macro invocation. This is fine because
 * [[SQLMacro]] is invoked in an env. that has all the Spark classes in the
 * classpath. The only issue it that we cannot use the Thread Classloader
 * of the Macro invocation. For this reason [[MacrosScalaReflection]]
 * is a copy of [[org.apache.spark.sql.catalyst.ScalaReflection]] with its
 * `mirror` setup on `org.apache.spark.util.Utils.getSparkClassLoader`
 *
 * '''Transferring Catalyst Expression Tree by Serialization:'''
 * Instead of developing a new builder capability to construct
 * macro universe Trees of catalyst Expressions, we directly construct
 * catalyst Expressions. To Lift these catalyst Expressions back to
 * the runtime world we use the serialization mechanism of catalyst
 * Expressions. So the [[SQLMacroExpressionBuilder]] is constructed
 * with the serialized form of the catalyst Expression that represents
 * the original macro code. In the runtime world this serialized form
 * is deserialized and on macro invocation [[MacroArg]] positions
 * are replaced with the Catalyst expressions at the invocation site.
 *
 */
package object sqlmacros {}
