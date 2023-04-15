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

import java.lang.constant.ClassDesc;
import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;
import java.lang.constant.DynamicConstantDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import org.microbean.constant.Constables;

import static java.lang.constant.ConstantDescs.BSM_INVOKE;
import static java.lang.constant.ConstantDescs.CD_Map;
import static java.lang.constant.ConstantDescs.CD_Object;
import static java.lang.constant.ConstantDescs.CD_String;
import static java.lang.constant.ConstantDescs.NULL;

import static java.util.Collections.emptySortedMap;
import static java.util.Collections.unmodifiableSortedMap;

import static org.microbean.qualifier.ConstantDescs.CD_Comparable;

/**
 * An abstract {@linkplain #attributes() attributed} {@linkplain #name() name}-{@linkplain #value() value} pair.
 *
 * @param <V> the type of a {@link Binding}'s {@linkplain #value() value} and of its {@linkplain #attributes() attribute
 * values}
 *
 * @param <B> the type of the subclass
 *
 * @author <a href="https://about.me/lairdnelson" target="_parent">Laird Nelson</a>
 */
public abstract class Binding<V, B extends Binding<V, B>> implements Comparable<B>, Constable {


  /*
   * Instance fields.
   */


  private final String name;

  private final V value;

  private final SortedMap<String, Object> attributes;

  private final SortedMap<String, Object> info;


  /*
   * Constructors.
   */


  /**
   * Creates a new {@link Binding}.
   *
   * @param name the name; must not be {@code null}
   *
   * @param value the value; may be {@code null}
   *
   * @param attributes further describing this {@link Binding}; may be {@code null}
   *
   * @param info informational attributes further describing this {@link Binding} that are not considered by its {@link
   * #equals(Object)} implementation; may be {@code null}
   *
   * @see #name()
   *
   * @see #value()
   *
   * @see #attributes()
   *
   * @see #info()
   */
  @SuppressWarnings("unchecked")
  protected Binding(final String name,
                    final V value,
                    final Map<? extends String, ?> attributes,
                    final Map<? extends String, ?> info) {
    super();
    this.name = Objects.requireNonNull(name);
    this.value = value;
    this.attributes =
      attributes == null || attributes.isEmpty() ? emptySortedMap() : unmodifiableSortedMap(new TreeMap<>(attributes));
    if (info == null || info.isEmpty()) {
      this.info = emptySortedMap();
    } else {
      final TreeMap<String, Object> map = new TreeMap<>();
      for (final String key : info.keySet()) {
        if (!this.attributes.containsKey(key)) {
          map.put(key, info.get(key));
        }
      }
      this.info = unmodifiableSortedMap(map);
    }
  }


  /*
   * Instance methods.
   */


  /**
   * Returns the name of this {@link Binding}.
   *
   * @return the name of this {@link Binding}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple threads.
   */
  public final String name() {
    return this.name;
  }

  /**
   * Returns the value of this {@link Binding}, which may be {@code null}.
   *
   * @return the value of this {@link Binding}
   *
   * @nullability This method may return {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple threads.
   */
  public final V value() {
    return this.value;
  }

  /**
   * Returns an immutable {@link SortedMap} representing any attributes further describing this {@link Binding}.
   *
   * <p>The attributes are considered by the {@link #equals(Object)} method.</p>
   *
   * @return the attributes of this {@link Binding}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple threads.
   */
  public final SortedMap<String, Object> attributes() {
    return this.attributes;
  }

  /**
   * Returns an immutable {@link SortedMap} representing any informational-only attributes further describing this
   * {@link Binding}.
   *
   * <p>The attributes are not considered by the {@link #equals(Object)} method.</p>
   *
   * @return the informational attributes of this {@link Binding}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple threads.
   */
  public final SortedMap<String, Object> info() {
    return this.info;
  }

  /**
   * Returns an {@link Optional} housing a {@link ConstantDesc} describing this {@link Binding}, if this {@link Binding}
   * is capable of being represented as a <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">dynamic
   * constant</a>, or an {@linkplain Optional#isEmpty() empty} {@link Optional} if not.
   *
   * @return an {@link Optional} housing a {@link ConstantDesc} describing this {@link Binding}, if this {@link Binding}
   * is capable of being represented as a <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">dynamic
   * constant</a>, or an {@linkplain Optional#isEmpty() empty} {@link Optional} if not
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple threads.
   *
   * @see #describeConstructor()
   *
   * @see <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">Dynamically-computed
   * constants</a>
   */
  @Override // Constable
  public Optional<? extends ConstantDesc> describeConstable() {
    final MethodHandleDesc constructor = this.describeConstructor();
    if (constructor == null) {
      return Optional.empty();
    }
    final V value = this.value();
    final ConstantDesc valueCd;
    if (value == null) {
      valueCd = NULL;
    } else if (value instanceof Constable c) {
      valueCd = c.describeConstable().orElse(null);
    } else if (value instanceof ConstantDesc cd) {
      valueCd = cd;
    } else {
      return Optional.empty();
    }
    final ConstantDesc attributesCd = Constables.describeConstable(this.attributes()).orElse(null);
    if (attributesCd == null) {
      return Optional.empty();
    }
    final ConstantDesc infoCd = Constables.describeConstable(this.info()).orElse(null);
    if (infoCd == null) {
      return Optional.empty();
    }
    return
      Optional.of(DynamicConstantDesc.of(BSM_INVOKE, constructor, this.name(), valueCd, attributesCd, infoCd));
  }

  /**
   * Returns a {@link MethodHandleDesc} describing the constructor or {@code static} method that will be used to create
   * a <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">dynamic
   * constant</a> representing this {@link Binding}.
   *
   * @return a {@link MethodHandleDesc} describing the constructor or {@code static} method that will be used to create
   * a <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">dynamic
   * constant</a> representing this {@link Binding}
   *
   * @nullability This method does not, and its overrides must not, return {@code null}.
   *
   * @idempotency This method is, and its overrides must be, idempotent and deterministic.
   *
   * @threadsafety This method is, and its overrides must be, safe for concurrent use by multiple threads.
   */
  protected MethodHandleDesc describeConstructor() {
    return
      MethodHandleDesc.ofConstructor(this.getClass().describeConstable().orElseThrow(),
                                     new ClassDesc[] { CD_String, CD_Object, CD_Map, CD_Map });
  }

  /**
   * Returns a hashcode for this {@link Binding} that represents its {@linkplain #name() name}, {@linkplain #value()
   * value} and {@linkplain #attributes() attributes}.
   *
   * <p>A subclass that overrides this method must also override the {@link #equals(Object)} and {@link
   * #compareTo(Binding)} methods accordingly.</p>
   *
   * @return a hashcode for this {@link Binding}
   *
   * @idempotency This method is, and its overrides must be, idempotent and deterministic.
   *
   * @threadsafety This method is, and its overrides must be, safe for concurrent use by multiple threads.
   *
   * @see #equals(Object)
   *
   * @see #compareTo(Binding)
   */
  @Override // Object
  public int hashCode() {
    int hashCode = 17;

    Object v = this.name();
    int c = v == null ? 0 : v.hashCode();
    hashCode = 37 * hashCode + c;

    v = this.value();
    c = v == null ? 0 : v.hashCode();
    hashCode = 37 * hashCode + c;

    v = this.attributes();
    c = v == null ? 0 : v.hashCode();
    hashCode = 37 * hashCode + c;

    return hashCode;
  }

  /**
   * Returns an {@code int} representing a {@linkplain Comparable#compareTo(Object) comparison} of this {@link Binding}
   * with the supplied {@link Binding}, by considering the {@linkplain #name() names}, {@linkplain #value() values} and
   * {@linkplain #attributes() attributes} of both {@link Binding}s.
   *
   * <p>Any {@linkplain #attributes() attribute values} that are not {@link Comparable} instances will be compared based
   * on their {@linkplain Object#toString() string representations}.  It is strongly recommended that {@linkplain
   * #attributes() attribute values} implement {@link Comparable}.</p>
   *
   * <p>This method is, and its overrides must be, {@linkplain Comparable consistent with equals}.</p>
   *
   * <p>A subclass that overrides this method must also override the {@link #hashCode()} and {@link #equals(Object)}
   * methods accordingly.</p>
   *
   * @param other the other {@link Binding}; may be {@code null} in which case a negative value will be returned
   *
   * @return an {@code int} representing a {@linkplain Comparable#compareTo(Object) comparison} of this {@link Binding}
   * with the supplied {@link Binding}
   *
   * @idempotency This method is, and its overrides must be, idempotent and deterministic.
   *
   * @threadsafety This method is, and its overrides must be, safe for concurrent use by multiple threads.
   *
   * @see #hashCode()
   *
   * @see #equals(Object)
   */
  @Override // Comparable<B>
  @SuppressWarnings("unchecked")
  public int compareTo(final B other) {
    if (other == null) {
      return -1;
    } else if (this.equals(other)) {
      return 0;
    } else {

      int cmp = this.name().compareTo(other.name());
      if (cmp != 0) {
        return cmp;
      }

      Object myValue = this.value();
      Object otherValue = other.value();
      if (value == null) {
        if (otherValue != null) {
          return 1;
        }
      } else if (otherValue == null) {
        return -1;
      } else if (myValue instanceof Comparable) {
        try {
          cmp = ((Comparable<Object>)myValue).compareTo(otherValue);
        } catch (final ClassCastException ohWell) {
        }
      }
      if (cmp != 0) {
        return cmp;
      }

      cmp = String.valueOf(this.value()).compareTo(String.valueOf(other.value())); // NOTE
      if (cmp != 0) {
        return cmp;
      }

      final SortedMap<String, Object> myAttributes = this.attributes();
      final SortedMap<String, Object> otherAttributes = other.attributes();
      cmp = Integer.compare(myAttributes.size(), otherAttributes.size());
      if (cmp != 0) {
        return cmp;
      }
      
      final Iterator<Entry<String, Object>> myEntries = myAttributes.entrySet().iterator();
      final Iterator<Entry<String, Object>> otherEntries = otherAttributes.entrySet().iterator();
      while (myEntries.hasNext()) {
        final Entry<String, ?> myEntry = myEntries.next();
        final Entry<String, ?> otherEntry = otherEntries.next();
        cmp = myEntry.getKey().compareTo(otherEntry.getKey());
        if (cmp != 0) {
          return cmp;
        }

        myValue = myEntry.getValue();
        otherValue = otherEntry.getValue();
        if (myValue == null) {
          if (otherValue != null) {
            return 1;
          }
        } else if (otherValue == null) {
          return -1;
        } else if (myValue instanceof Comparable) {
          try {
            cmp = ((Comparable<Object>)myValue).compareTo(otherValue);
          } catch (final ClassCastException ohWell) {
          }
        }
        if (cmp != 0) {
          return cmp;
        }

        cmp = String.valueOf(myEntry.getValue()).compareTo(String.valueOf(otherEntry.getValue())); // NOTE
        if (cmp != 0) {
          return cmp;
        }
      }

      assert cmp == 0 : "cmp: " + cmp; // really this means equals()/hashCode() were implemented badly
      return cmp;
    }
  }

  /**
   * Returns {@code true} if the supplied {@link Object} is equal to this {@link Binding}.
   *
   * <p>The supplied {@link Object} is considered to be equal to this {@link Binding} if and only if:</p>
   *
   * <ul>
   *
   * <li>Its {@linkplain #getClass() class} is identical to this {@link Binding}'s {@linkplain #getClass() class},
   * and</li>
   *
   * <li>Its {@linkplain #name() name} {@linkplain String#equals(Object) is equal to} this {@link Binding}'s {@linkplain
   * #name() name}, and</li>
   *
   * <li>Its {@linkplain #value() value} {@linkplain Object#equals(Object) is equal to} this {@link Binding}'s
   * {@linkplain #value() value}, and</li>
   *
   * <li>Its {@linkplain #attributes() attributes} {@linkplain Map#equals(Object) are equal to} this {@link Binding}'s
   * {@linkplain #attributes() attributes}</li>
   *
   * </ul>
   *
   * <p>A subclass that overrides this method must also override the {@link #hashCode()} and {@link #compareTo(Binding)}
   * methods accordingly.</p>
   *
   * @param other the {@link Object} to test; may be {@code null} in which case {@code false} will be returned
   *
   * @return {@code true} if the supplied {@link Object} is equal to this {@link Binding}
   *
   * @idempotency This method is, and its overrides must be, idempotent and deterministic.
   *
   * @threadsafety This method is, and its overrides must be, safe for concurrent use by multiple threads.
   *
   * @see #hashCode()
   *
   * @see #compareTo(Binding)
   */
  @Override // Object
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    } else if (other != null && other.getClass() == this.getClass()) {
      final Binding<?, ?> her = (Binding<?, ?>)other;
      return
        Objects.equals(this.name(), her.name()) &&
        Objects.equals(this.value(), her.value()) &&
        Objects.equals(this.attributes(), her.attributes());
    } else {
      return false;
    }
  }

  /**
   * Returns a {@link String} representation of this {@link Binding}.
   *
   * <p>The format of the returned {@link String} is deliberately undefined and may change between versions of this
   * class without prior notice.</p>
   *
   * @return a {@link String} representation of this {@link Binding}
   *
   * @nullability This method does not, and its overrides must not, return {@code null}.
   *
   * @idempotency This method is, and its overrides must be, idempotent and deterministic.
   *
   * @threadsafety This method is, and its overrides must be, safe for concurrent use by multiple threads.
   */
  @Override // Object
  public String toString() {
    final StringBuilder sb = new StringBuilder(this.name()).append('=').append(this.value());
    final Map<?, ?> attributes = this.attributes();
    if (!attributes.isEmpty()) {
      sb.append(' ').append(attributes);
    }
    final Map<?, ?> info = this.info();
    if (!info.isEmpty()) {
      sb.append(" (").append(info).append(')');
    }
    return sb.toString();
  }

}
