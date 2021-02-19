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
package org.apache.spark.sql.sqlmacros

import org.apache.spark.sql.catalyst.{expressions => sparkexpr}

trait Options {
  self: ExprBuilders with ExprTranslator =>

  import macroUniverse._

  object OptionPatterns {

    val someApplySym = typeOf[Some.type].member(TermName("apply"))

    def unapply(t: mTree): Option[sparkexpr.Expression] =
      t match {
        case q"$op.get" =>
          for (
            e <- CatalystExpression.unapply(op)
          ) yield sparkexpr.objects.UnwrapOption(e.dataType, e)
        case q"$id[$_]($op)" if id.symbol == someApplySym =>
          for (
            e <- CatalystExpression.unapply(op)
          ) yield sparkexpr.objects.WrapOption(e, e.dataType)
        case _ => None
      }
  }
}
