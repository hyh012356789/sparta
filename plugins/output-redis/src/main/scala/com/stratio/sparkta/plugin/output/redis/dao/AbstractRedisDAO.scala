/**
 * Copyright (C) 2014 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
* Copyright (C) 2014 Stratio (http://stratio.com)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*         http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stratio.sparkta.plugin.output.redis.dao

import com.redis.RedisClientPool
import com.stratio.sparkta.sdk.{Bucketer, DimensionValue}

/**
 * Trait with common operations over redis server.
 * 
 * @author anistal
 */
trait AbstractRedisDAO {

  def hostname : String
  
  def port : Int

  def eventTimeFieldName : String = "eventTime"
  
  val IdSeparator: String = ":"

  protected def pool: RedisClientPool = AbstractRedisDAO.pool(hostname, port)
  
  def hset(key: Any, field: Any, value: Any) = {
    pool.withClient(client =>
      client.hset(key, field, value)
    )
  }

  def hget(key: Any, field: Any) = {
    pool.withClient(client =>
      client.hget(key, field)
    )
  }

  /**
   * Depending of the bucketType the dimension name could change.
   *
   * @param dimensionValue that contains the bucketType.
   * @return dimensionName contained in the dimensionValue.
   */
  def extractDimensionName(dimensionValue: DimensionValue): String = {
    dimensionValue.bucketType match {
      case Bucketer.identity => dimensionValue.dimension.name
      case _ => dimensionValue.bucketType.id
    }
  }
}

/**
 * Initializes singletons objects needed in the trait.
 * 
 * @author anistal
 */
private object AbstractRedisDAO {
  var instance : Option[RedisClientPool] = None

  /**
   * Initializes a Redis connection pool.
   *
   * @param hostname of the redis server.
   * @param port of the redis server.
   * @return a pool of connections.
   */
  def pool(hostname: String, port: Int): RedisClientPool = {
    instance = this.instance match {
      case None => Some(new RedisClientPool(hostname, port))
      case _ => this.instance
    }
    this.instance.get
  }
}
