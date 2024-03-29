/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright © 2022–2023 microBean™.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.microbean.qualifier;

import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;

import java.lang.invoke.MethodHandles;

import java.util.List;
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
    final Qualifiers<String> q =
      Qualifiers.of(List.of(Qualifier.<String>of("a", "b"),
                            Qualifier.<String>of("c", "d")));
    assertEquals(q, q.describeConstable().orElseThrow(AssertionError::new).resolveConstantDesc(MethodHandles.lookup()));
  }

  @Test
  final void testQualifierDescribeConstable() throws ReflectiveOperationException {
    final Qualifier<String> q = Qualifier.of("a", null, Map.of("c", "d"));
    assertEquals(q, q.describeConstable().orElseThrow(AssertionError::new).resolveConstantDesc(MethodHandles.publicLookup().in(Qualifier.class)));
  }

  @Test
  final void testQualifiersDescribeConstable() throws ReflectiveOperationException {
    final Qualifiers<String> q2 = Qualifiers.of(Qualifier.of("a", "b", Map.of("c", "d")));
    assertEquals(q2, q2.describeConstable().orElseThrow(AssertionError::new).resolveConstantDesc(MethodHandles.publicLookup().in(Qualifiers.class)));
  }

  @Test
  final void testNamedAttributeMapDescribeConstable() throws ReflectiveOperationException {
    final NamedAttributeMap<String> a = new NamedAttributeMap<>("crap", Map.of("a", "b"));
    assertEquals(a, a.describeConstable().orElseThrow(AssertionError::new).resolveConstantDesc(MethodHandles.publicLookup().in(NamedAttributeMap.class)));
  }
  
}
