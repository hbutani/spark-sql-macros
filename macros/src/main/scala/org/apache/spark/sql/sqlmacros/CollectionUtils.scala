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

import java.sql.{Date, Timestamp}

import org.apache.spark.unsafe.types.CalendarInterval

// scalastyle:off

object CollectionUtils {

  def mapEntries[K,V](m : Map[K, V]) : Array[(K,V)] = ???
  def mapFromEntries[K,V](arr : Array[(K,V)]) : Map[K, V] = ???

  def sortArray[T](arr : Array[T], asc : Boolean) : Array[T] = ???
  def shuffleArray[T](arr : Array[T]) : Array[T] = ???
  def shuffleArray[T](arr : Array[T], randomSeed: Long) : Array[T] = ???
  def overlapArrays[T](lArr : Array[T], rArr : Array[T]) : Array[T] = ???
  def positionArray[T](lArr : Array[T], elem : T) : Long = ???
  def sequence[T : Integral](start : T, stop : T, step : T) : Array[T] = ???
  def date_sequence(start : Date, stop : Date, step : CalendarInterval) : Array[Date] = ???
  def timestamp_sequence(start : Timestamp, stop : Timestamp, step : CalendarInterval) : Array[Timestamp] = ???
  def removeArray[T](arr : Array[T], elem: T) : Array[T] = ???
  def exceptArray[T](lArr : Array[T], rArr : Array[T]) : Array[T] = ???
  def mapKeys[K, V](map : Map[K,V]) : Array[K] = ???
  def mapValues[K, V](map : Map[K,V]) : Array[V] = ???
}
