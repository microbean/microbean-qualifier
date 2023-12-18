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

import java.lang.System.Logger;

import java.lang.constant.Constable;
import java.lang.constant.DynamicConstantDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import org.microbean.constant.Constables;

import org.microbean.invoke.ContentHashable;

import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.WARNING;

import static java.lang.constant.ConstantDescs.BSM_INVOKE;
import static java.lang.constant.ConstantDescs.CD_Collection;
import static java.lang.constant.ConstantDescs.CD_Map;
import static java.lang.constant.ConstantDescs.CD_String;
import static java.lang.constant.DirectMethodHandleDesc.Kind.STATIC;

import static org.microbean.qualifier.ConstantDescs.CD_NamedAttributeMap;

public record NamedAttributeMap<V>(String name,
                                   Map<String, V> attributes,
                                   Map<String, V> info,
                                   Collection<NamedAttributeMap<V>> metadata)
  implements Comparable<NamedAttributeMap<V>>, Constable, ContentHashable {

  private static final Logger LOGGER = System.getLogger(NamedAttributeMap.class.getName());

  public NamedAttributeMap(final String name) {
    this(name, Map.of(), Map.of(), List.of());
  }

  public NamedAttributeMap(final String name, final V value) {
    this(name, Map.of("value", value), Map.of(), List.of());
  }

  public NamedAttributeMap(final String name, final Map<String, V> attributes) {
    this(name, attributes, Map.of(), List.of());
  }

  @SuppressWarnings("unchecked")
  public NamedAttributeMap {
    Objects.requireNonNull(name, "name");
    if (attributes.isEmpty()) {
      attributes = Collections.emptySortedMap();
    } else {
      final SortedMap<String, V> m =
        attributes instanceof TreeMap<String, V> cloneMe ?
        (SortedMap<String, V>)cloneMe.clone() :
        new TreeMap<>(attributes instanceof SortedMap<String, V> sm ? sm.comparator() : null);
      m.putAll(attributes);
      attributes = Collections.unmodifiableSortedMap(m);
    }
    if (info.isEmpty()) {
      info = Collections.emptySortedMap();
    } else {
      final SortedMap<String, V> m =
        info instanceof TreeMap<String, V> cloneMe ?
        (TreeMap<String, V>)cloneMe.clone() :
        new TreeMap<>(info instanceof SortedMap<String, V> sm ? sm.comparator() : null);
      m.putAll(info);
      info = Collections.unmodifiableSortedMap(m);
    }
    if (metadata.isEmpty()) {
      metadata = List.of();
    } else {
      final List<NamedAttributeMap<V>> l =
        metadata instanceof ArrayList<NamedAttributeMap<V>> cloneMe ?
        (List<NamedAttributeMap<V>>)cloneMe.clone() :
        new ArrayList<>(metadata);
      Collections.sort(l);
      metadata = Collections.unmodifiableList(l);
    }
  }

  public final boolean isEmpty() {
    return this.attributes().isEmpty();
  }

  public final boolean containsKey(final String k) {
    return this.attributes().containsKey(k);
  }

  public final V get(final String k) {
    return this.attributes().get(k);
  }

  @Override // ContentHashable
  public final Optional<String> contentHashInput() {
    final StringBuilder sb = new StringBuilder(this.name());
    for (final Entry<String, ?> e : this.attributes().entrySet()) {
      sb.append(e.getKey());
      final Object value = e.getValue();
      if (value instanceof ContentHashable ch) {
        final CharSequence cs = ch.contentHashInput().orElse(null);
        if (cs == null) {
          return Optional.empty();
        }
        sb.append(cs);
      } else if (value != null) {
        sb.append(value);
      }
    }
    return Optional.of(sb.toString());
  }

  // Consistent with equals().
  @Override // Comparable
  @SuppressWarnings("unchecked")
  public final int compareTo(final NamedAttributeMap<V> other) {
    if (other == null) {
      return -1;
    }
    int cmp = this.name().compareTo(other.name());
    if (cmp != 0) {
      return cmp;
    }
    final Map<String, V> attributes = this.attributes();
    final Map<String, V> herAttributes = other.attributes();
    cmp = Integer.signum(attributes.size() - herAttributes.size());
    if (cmp != 0) {
      return cmp;
    }
    final Iterator<Entry<String, V>> myIterator = attributes.entrySet().iterator();
    final Iterator<Entry<String, V>> herIterator = herAttributes.entrySet().iterator();
    while (myIterator.hasNext()) {
      final Entry<String, V> myEntry = myIterator.next();
      final Entry<String, V> herEntry = herIterator.next();
      final String myKey = myEntry.getKey();
      final String herKey = herEntry.getKey();
      if (myKey == null) {
        if (herKey != null) {
          return 1;
        }
      } else if (herKey == null) {
        return -1;
      }
      cmp = myKey.compareTo(herKey);
      if (cmp != 0) {
        return cmp;
      }
      final V myValue = myEntry.getValue();
      final V herValue = herEntry.getValue();
      if (myValue == null) {
        if (herValue != null) {
          return 1;
        }
      } else if (herValue == null) {
        return -1;
      } else if (myValue instanceof Comparable) {
        try {
          cmp = ((Comparable<V>)myValue).compareTo(herValue);
        } catch (final ClassCastException ohWell) {
          if (LOGGER.isLoggable(WARNING)) {
            LOGGER.log(WARNING, ohWell);
          }
        }
      } else if (!myValue.equals(herValue)) {
        if (LOGGER.isLoggable(DEBUG)) {
          LOGGER.log(DEBUG, "Using toString() for value comparison: {0} <=> {1}", myValue.toString(), herValue.toString());
        }
        cmp = myValue.toString().compareTo(herValue.toString());
      }
      if (cmp != 0) {
        return cmp;
      }
    }
    // Note: we do not consider info or metadata on purpose
    assert cmp == 0;
    return 0;
  }

  @Override
  public final boolean equals(final Object other) {
    if (other == this) {
      return true;
    } else if (other != null && other.getClass() == this.getClass()) {
      final NamedAttributeMap<?> her = (NamedAttributeMap<?>)other;
      // Note: no info or metadata on purpose
      return
        this.name().equals(her.name()) &&
        this.attributes().equals(her.attributes());
    } else {
      return false;
    }
  }

  @Override
  public final int hashCode() {
    int hashCode = 17;
    hashCode = 31 * hashCode + this.name().hashCode();
    return 31 * hashCode + this.attributes().hashCode();
  }

  @Override // Constable
  public final Optional<DynamicConstantDesc<NamedAttributeMap<V>>> describeConstable() {
    return Constables.describeConstable(this.attributes())
      .flatMap(attributesDesc -> Constables.describeConstable(this.info())
               .flatMap(infoDesc -> Constables.describeConstable(this.metadata())
                        .map(mdDesc -> DynamicConstantDesc.of(BSM_INVOKE,
                                                              MethodHandleDesc.ofMethod(STATIC,
                                                                                        CD_NamedAttributeMap,
                                                                                        "of",
                                                                                        MethodTypeDesc.of(CD_NamedAttributeMap,
                                                                                                          CD_String,
                                                                                                          CD_Map,
                                                                                                          CD_Map,
                                                                                                          CD_Collection)),
                                                              this.name(),
                                                              attributesDesc,
                                                              infoDesc,
                                                              mdDesc))));
  }

  // Called by describeConstable().
  public static final <V> NamedAttributeMap<V> of(final String name,
                                                  final Map<String, V> attributes,
                                                  final Map<String, V> info,
                                                  final Collection<NamedAttributeMap<V>> metadata) {
    return new NamedAttributeMap<>(name, attributes, info, metadata);
  }

}
