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

import java.lang.constant.Constable;
import java.lang.constant.DynamicConstantDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.microbean.constant.Constables;

import org.microbean.invoke.ContentHashable;

import static java.lang.constant.ConstantDescs.BSM_INVOKE;
import static java.lang.constant.ConstantDescs.CD_Collection;
import static java.lang.constant.ConstantDescs.CD_Object;
import static java.lang.constant.DirectMethodHandleDesc.Kind.STATIC;

import static org.microbean.qualifier.ConstantDescs.CD_Attributed;

public record Attributed<T, V>(Collection<NamedAttributeMap<V>> attributes, T attributed) implements AttributeBearing<V>, Constable, ContentHashable {

  @SuppressWarnings("unchecked")
  public Attributed {
    Objects.requireNonNull(attributed, "attributed");
    if (attributes.isEmpty()) {
      attributes = List.of();
    } else {
      final List<NamedAttributeMap<V>> l =
        attributes instanceof ArrayList<NamedAttributeMap<V>> cloneMe ?
        (List<NamedAttributeMap<V>>)cloneMe.clone() :
        new ArrayList<>(attributes);
      Collections.sort(l);
      attributes = Collections.unmodifiableList(l);
    }
  }

  @Override // ContentHashable
  public final Optional<String> contentHashInput() {
    final StringBuilder sb = new StringBuilder();
    for (final NamedAttributeMap<V> a : this.attributes()) {
      sb.append(a.contentHashInput().orElse(""));
    }
    final Object attributed = this.attributed();
    if (attributed instanceof ContentHashable ch) {
      final Optional<?> o = ch.contentHashInput();
      if (o.isPresent()) {
        sb.append(o.orElseThrow());
      }
    } else if (attributed != null) {
      sb.append(attributed.toString());
    }
    return Optional.of(sb.toString());
  }

  @Override // Constable
  public final Optional<DynamicConstantDesc<Attributed<T, V>>> describeConstable() {
    return Constables.describeConstable(this.attributed())
      .flatMap(attributedDesc -> Constables.describeConstable(this.attributes())
               .map(attributesDesc -> DynamicConstantDesc.of(BSM_INVOKE,
                                                             MethodHandleDesc.ofMethod(STATIC,
                                                                                       CD_Attributed,
                                                                                       "of",
                                                                                       MethodTypeDesc.of(CD_Attributed,
                                                                                                         CD_Collection,
                                                                                                         CD_Object)),
                                                             attributesDesc,
                                                             attributedDesc)));
  }

  // Used by describeConstable().
  public static final <T, V> Attributed<T, V> of(final Collection<NamedAttributeMap<V>> attributes, final T attributed) {
    return new Attributed<>(attributes, attributed);
  }

}
