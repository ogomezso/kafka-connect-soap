/**
 * Copyright © 2021 Oscar Gómez (ogomezso0@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.ogomezso.kafka.connect.soap;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

public class SoapEvent extends Struct {

  public static final String ID = "id";
  public static final String DATA = "data";

  final public static Schema SCHEMA = SchemaBuilder.struct()
      .name("com.github.ogomezso.kafka.connect.soap.SoapEvent")
      .doc("Server Sent Event Message")
      .field(ID, SchemaBuilder.string().doc("The event ID").optional().build())
      .field(DATA, SchemaBuilder.string().doc("The event data payload").required().build());

  public SoapEvent(String id, String data) {
    super(SCHEMA);
    this
        .put(ID, id)
        .put(DATA, data);
  }

  @Override
  public String toString() {
    return String.format("[id]=%s [data]=%s",
        this.get(ID),
        this.get(DATA)
    );
  }
}
