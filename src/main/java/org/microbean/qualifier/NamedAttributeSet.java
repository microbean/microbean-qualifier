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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.microbean.constant.Constables;

import static java.lang.constant.ConstantDescs.BSM_INVOKE;
import static java.lang.constant.ConstantDescs.CD_Set;
import static java.lang.constant.ConstantDescs.CD_String;

import static org.microbean.qualifier.ConstantDescs.CD_NamedAttributeSet;

public record NamedAttributeSet<V>(Kind kind, String name, Set<Attribute<V>> attributes, Set<Attribute<V>> info) implements Constable {

  public NamedAttributeSet(final Kind kind, final String name, final Map<? extends String, ? extends V> attributes, final Map<? extends String, ? extends V> info) {
    this(kind, name, toAttributeSet(attributes), toAttributeSet(info));
  }
  
  public NamedAttributeSet(final Kind kind, final String name) {
    this(kind, name, Set.of(), Set.of());
  }

  public NamedAttributeSet(final Kind kind, final String name, final V soleValue) {
    this(kind, name, Set.of(new Attribute<>("value", soleValue)), Set.of());
  }

  public NamedAttributeSet(final Kind kind, final String name, final String soleAttributeName, final V soleAttributeValue) {
    this(kind, name, Set.of(new Attribute<>(soleAttributeName, soleAttributeValue)), Set.of());
  }

  public NamedAttributeSet(final Kind kind, final String name, final Attribute<V> soleAttribute) {
    this(kind, name, Set.of(soleAttribute), Set.of());
  }

  public NamedAttributeSet {
    Objects.requireNonNull(kind, "kind");
    Objects.requireNonNull(name, "name");
    if (attributes == null || attributes.isEmpty()) {
      attributes = Set.of();
    } else if (attributes.size() == 1) {
      attributes = Set.copyOf(attributes);
    } else {
      attributes = Collections.unmodifiableSet(new TreeSet<>(attributes));
    }
    if (info == null || info.isEmpty()) {
      info = Set.of();
    } else if (info.size() == 1) {
      info = Set.copyOf(info);
    } else {
      info = Collections.unmodifiableSet(new TreeSet<>(info));
    }
  }

  @Override
  public final Optional<DynamicConstantDesc<NamedAttributeSet<V>>> describeConstable() {
    final ConstantDesc attributes = Constables.describeConstable(this.attributes()).orElse(null);
    if (attributes == null) {
      return Optional.empty();
    }
    final ConstantDesc info = Constables.describeConstable(this.info()).orElse(null);
    if (info == null) {
      return Optional.empty();
    }
    return
      Optional.of(DynamicConstantDesc.of(BSM_INVOKE,
                                         MethodHandleDesc.ofMethod(DirectMethodHandleDesc.Kind.STATIC,
                                                                   CD_NamedAttributeSet,
                                                                   "of",
                                                                   MethodTypeDesc.of(CD_NamedAttributeSet,
                                                                                     ClassDesc.of("org.microbean.qualifier.NamedAttributeSet$Kind"),
                                                                                     CD_String,
                                                                                     CD_Set,
                                                                                     CD_Set)),
                                         this.kind().describeConstable().orElseThrow(),
                                         this.name(),
                                         attributes,
                                         info));
  }

  @Override
  public final int hashCode() {
    int hashCode = 17;
    hashCode = 37 * hashCode + this.kind().hashCode();
    hashCode = 37 * hashCode + this.name().hashCode();
    hashCode = 37 * hashCode + this.attributes().hashCode();
    // Note: no info()
    return hashCode;
  }

  @Override
  public final boolean equals(final Object other) {
    if (other == this) {
      return true;
    } else if (other != null && other.getClass() == this.getClass()) {
      final NamedAttributeSet<?> her = (NamedAttributeSet<?>)other;
      // Note: no info()
      return
        this.kind() == her.kind() &&
        this.name().equals(her.name()) &&
        this.attributes().equals(her.attributes());
    } else {
      return false;
    }
  }

  public final Attribute<V> attribute(final String name) {
    for (final Attribute<V> a : this.attributes()) {
      if (name.equals(a.name())) {
        return a;
      }
    }
    return null;
  }

  public final Attribute<V> info(final String name) {
    for (final Attribute<V> a : this.info()) {
      if (name.equals(a.name())) {
        return a;
      }
    }
    return null;
  }

  // Called by describeConstable().
  public static final <V> NamedAttributeSet<V> of(final Kind kind, final String name, final Set<Attribute<V>> attributes, final Set<Attribute<V>> info) {
    return new NamedAttributeSet<>(kind, name, attributes, info);
  }
  
  public static final <V> NamedAttributeSet<V> bindings(final String name, final Set<Attribute<V>> attributes, final Set<Attribute<V>> info) {
    return of(Kind.BINDINGS, name, attributes, info);
  }

  public static final <V> NamedAttributeSet<V> qualifiers(final String name, final Set<Attribute<V>> attributes, final Set<Attribute<V>> info) {
    return of(Kind.QUALIFIERS, name, attributes, info);
  }

  public static final <V> Set<Attribute<V>> toAttributeSet(final Map<? extends String, ? extends V> map) {
    if (map == null || map.isEmpty()) {
      return Set.of();
    }
    final Set<Attribute<V>> set = new LinkedHashSet<>();
    for (final Entry<? extends String, ? extends V> entry : map.entrySet()) {
      set.add(new Attribute<>(entry.getKey(), entry.getValue()));
    }
    return Collections.unmodifiableSet(set);
  }

  public enum Kind {
    BINDINGS, QUALIFIERS;
  }

}
