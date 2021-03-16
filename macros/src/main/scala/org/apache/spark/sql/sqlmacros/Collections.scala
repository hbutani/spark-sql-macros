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
import org.apache.spark.sql.types.{ArrayType, MapType}

trait Collections {
  self: ExprBuilders with ExprTranslator =>

  import macroUniverse._

  object CollName {
    def unapply(t : mTree) :
    Option[mTermName] = t match {
      case Select(Ident(collNm), TermName("apply")) if collNm.isTermName =>
        Some(collNm.toTermName)
      case _ => None
    }
  }

  object GetEntryExpr {
    def unapply(vInfo : ValInfo, idxExpr : sparkexpr.Expression) :
    Option[sparkexpr.Expression] = vInfo.typInfo.catalystType match {
      case a : ArrayType => Some(sparkexpr.GetArrayItem(vInfo.rhsExpr, idxExpr))
      case m : MapType => Some(sparkexpr.GetMapValue(vInfo.rhsExpr, idxExpr))
      case _ => None
    }
  }

  object CollectionApply {
    def unapply(t: mTree): Option[sparkexpr.Expression] =
      t match {
        case q"$id(..$args)" if args.size == 1 =>
          for (
            collNm <- CollName.unapply(id);
            vInfo <- scope.get(collNm);
            idxExpr <- CatalystExpression.unapply(args(0).asInstanceOf[mTree]);
            valExpr <- GetEntryExpr.unapply(vInfo, idxExpr)
          ) yield valExpr
        case _ => None
      }
  }

  object CollectionConstruct {
    val arrApplySym = macroUniverse.typeOf[Array.type].decl(TermName("apply"))
    val mapApplySym = macroUniverse.typeOf[Map.type].member(TermName("apply"))

    def unapply(t: mTree): Option[sparkexpr.Expression] =
      t match {
        case q"$id(..$args)" if arrApplySym.alternatives.contains(id.symbol) =>
          for (
            entries <- CatalystExpressions.unapplySeq(args)
          ) yield sparkexpr.CreateArray(entries)
        case q"$id(..$args)(..$implArgs)" if arrApplySym.alternatives.contains(id.symbol) =>
          for (
            entries <- CatalystExpressions.unapplySeq(args)
          ) yield sparkexpr.CreateArray(entries)
        case q"$id(..$args)" if mapApplySym.alternatives.contains(id.symbol) =>
          for (
            entries <- CatalystExpressions.unapplySeq(args)
            if entries.forall(_.isInstanceOf[sparkexpr.CreateNamedStruct])
          ) yield {
            val mEntries = entries.flatMap(_.asInstanceOf[sparkexpr.CreateNamedStruct].valExprs)
            sparkexpr.CreateMap(mEntries)
          }
        case _ => None
      }
  }

  /*
    functions TODO:

    Size <- arr.size, map.size
    MapKeys <- m.KeyArray // special func
    ArraysZip <- arr.zip
    MapValues <- m.ValueArray // special func
    MapEntries <- by a CollectionUtils func
    MapConcat <- m ++ m
    MapFromEntries <- by a CollectionUtils func
    SortArray <- a.sorted, by a CollectionUtils func

    Shuffle(arr) <- by a CollectionUtils func
    Reverse <- arr.reverse
    ArrayContains <- arr.contains
    ArraysOverlap <- by a CollectionUtils func
    Slice <- arr.slice
    ArrayJoin <- arr.mkString
    ArrayMin <- arr.min
    ArrayMax <- arr.max
    ArrayPosition <- by a CollectionUtils func
    Concat <- arr ++ arr
    Flatten <- arr(arr, arr).flatten

    Sequence <- by a CollectionUtils func; some cases of Range(0, 10) => Seq(0, 9)
    ArrayRepeat <- by a CollectionUtils func
    ArrayRemove <- by a CollectionUtils func

    ArrayDistinct <- arr.distinct  // advanced translate; a ArrayDistinct represents a Scala Set
    ArrayUnion <- arr ++ arr
    ArrayIntersect <- arr intersect arr
    ArrayExcept <- by a CollectionUtils func; arr except arr

   */

  object CollectionFunctions {
    def unapply(t: mTree): Option[sparkexpr.Expression] =
      t match {
        case _ => None
      }
  }
}
