/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xienng.flink.cdc;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


public class DataStreamJob {

    public static void main(String[] args) throws Exception {
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname("10.8.10.28")
                .port(3306)
                // 设置需要关注的数据库
                .databaseList("cannaltest")
                //设置需要关联的表
                .tableList("cannaltest.article")
                .username("root")
                .password("root")
                //将捕获到的binlog转换成JSON，注意一定要设置时区，否则会报错
                .deserializer(new JsonDebeziumDeserializationSchema()).serverTimeZone("Europe/London")
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // enable checkpoint
//        env.enableCheckpointing(3000);
        env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL Source")

                .setParallelism(1)
                .print().setParallelism(1);
        env.execute("Print MySQL Snapshot + Binlog");
    }
}
