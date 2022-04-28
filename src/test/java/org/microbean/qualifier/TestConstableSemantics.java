/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright © 2022 microBean™.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.microbean.qualifier;

import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;

import java.lang.invoke.MethodHandles;

import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class TestConstableSemantics {

  private TestConstableSemantics() {
    super();
  }

  @Test
  final void testQualifiedRecordClassName() {
    assertEquals("org.microbean.qualifier.Qualified$Record", Qualified.Record.class.getName());
  }

  @Test
  final void testConstableSemantics() throws ReflectiveOperationException {
    final Qualifiers<String, String> q = Qualifiers.of("a", "b", "c", "d");
    assertEquals(q, q.describeConstable().orElseThrow().resolveConstantDesc(MethodHandles.lookup()));
  }

  @Test
  final void testDeliberateLossOfInfo() throws ReflectiveOperationException {
    final Qualifiers<String, String> q0 =
      Qualifiers.of(Map.of("a", "b", "c", "d"),
                    Map.of("w", "x", "y", "z"));
    @SuppressWarnings("unchecked")
    final Qualifiers<String, String> q1 =
      (Qualifiers<String, String>)q0.describeConstable().orElseThrow().resolveConstantDesc(MethodHandles.lookup());
    assertEquals(q0.toMap(), q1.toMap());
    assertTrue(q1.info().isEmpty());
    assertNotEquals(q0.info(), q1.info());
    assertEquals(q0, q1);
  }

}
