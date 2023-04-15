/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright © 2023 microBean™.
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

import java.lang.constant.ClassDesc;
import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.DynamicConstantDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;

import java.util.Objects;
import java.util.Optional;

import static java.lang.constant.ConstantDescs.BSM_INVOKE;
import static java.lang.constant.ConstantDescs.CD_Object;
import static java.lang.constant.ConstantDescs.CD_String;
import static java.lang.constant.ConstantDescs.NULL;

import static org.microbean.qualifier.ConstantDescs.CD_Attribute;

// Experimental for now. Trying to simplify all the crap in this package.
public record Attribute<V>(String name, V value) implements Comparable<Attribute<?>>, Constable {

  public Attribute {
    Objects.requireNonNull(name, "name");
  }

  @Override // Constable
  public final Optional<DynamicConstantDesc<Attribute<V>>> describeConstable() {
    final ConstantDesc valueCd;
    final V value = this.value();
    if (value == null) {
      valueCd = NULL;
    } else if (value instanceof Constable c) {
      valueCd = c.describeConstable().orElseThrow();
    } else if (value instanceof ConstantDesc cd) {
      valueCd = cd;
    } else {
      return Optional.empty();
    }
    return
      Optional.of(DynamicConstantDesc.of(BSM_INVOKE,
                                         MethodHandleDesc.ofMethod(DirectMethodHandleDesc.Kind.STATIC,
                                                                   CD_Attribute,
                                                                   "of",
                                                                   MethodTypeDesc.of(CD_Attribute, CD_String, CD_Object)),
                                         this.name(),
                                         valueCd));
  }

  @Override
  @SuppressWarnings("unchecked")
  public final int compareTo(final Attribute<?> other) {
    if (other == null) {
      return -1;
    } else if (this.equals(other)) {
      return 0;
    }

    final int cmp = this.name().compareTo(other.name());
    if (cmp != 0) {
      return cmp;
    }

    final V myValue = this.value();
    final Object otherValue = other.value();
    if (myValue == null) {
      return otherValue == null ? 0 : 1;
    } else if (otherValue == null) {
      return -1;
    } else if (myValue instanceof Comparable) {
      try {
        return ((Comparable<V>)myValue).compareTo((V)otherValue);
      } catch (final ClassCastException ohWell) {
      }
    }
    return String.valueOf(myValue).compareTo(String.valueOf(otherValue));
  }


  /*
   * Static methods.
   */


  // Invoked by describeConstable().
  public static final <V> Attribute<V> of(final String name, final V value) {
    return new Attribute<>(name, value);
  }

}
