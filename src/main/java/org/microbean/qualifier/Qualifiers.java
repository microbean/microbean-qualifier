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

import java.lang.constant.ClassDesc;
import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.DynamicConstantDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringJoiner;
import java.util.TreeMap;

import java.util.function.Predicate;

import static java.lang.constant.ConstantDescs.BSM_INVOKE;
import static java.lang.constant.ConstantDescs.NULL;

import static java.util.Collections.emptySortedMap;
import static java.util.Collections.unmodifiableSortedMap;

import static org.microbean.qualifier.ConstantDescs.CD_Constable;
import static org.microbean.qualifier.ConstantDescs.CD_Qualifiers;

/**
 * A {@link Constable}, immutable set of 
 * key-value pairs that can be used to further qualify an object for
 * many different purposes.
 *
 * <p>This is a <a
 * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/doc-files/ValueBased.html">value-based
 * class</a>.</p>
 *
 * <p>Values of type {@code K} must be members of classes adhering to
 * <a
 * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/doc-files/ValueBased.html">value-based
 * semantics</a>.</p>
 *
 * <p>Values of type {@code V} must be members of classes adhering to
 * <a
 * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/doc-files/ValueBased.html">value-based
 * semantics</a>.</p>
 *
 * <p>Undefined behavior will result if the preceding requirements are
 * not honored.</p>
 *
 * @param <K> the type borne by the keys of the qualifiers in this
 * {@link Qualifiers}
 *
 * @param <V> the type borne by the values of the qualifiers in this
 * {@link Qualifiers}
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see #of(Constable, Constable)
 */
public final class Qualifiers<K extends Constable & Comparable<K>, V extends Constable> implements Constable {


  /*
   * Static fields.
   */


  private static final Qualifiers<?, ?> EMPTY_QUALIFIERS = new Qualifiers<>();


  /*
   * Instance fields.
   */


  private final SortedMap<K, V> qualifiers;


  /*
   * Constructors.
   */


  private Qualifiers() {
    this(null);
  }

  private Qualifiers(final Map<? extends K, ? extends V> qualifiers) {
    super();
    if (qualifiers == null || qualifiers.isEmpty()) {
      this.qualifiers = emptySortedMap();
    } else {
      this.qualifiers = unmodifiableSortedMap(new TreeMap<>(qualifiers));
    }
  }


  /*
   * Instance methods.
   */


  /**
   * Returns an immutable {@link SortedMap} of the qualifiers in this
   * {@link Qualifiers}.
   *
   * @return an immutable {@link SortedMap} of the qualifiers in this
   * {@link Qualifiers}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final SortedMap<K, V> qualifiers() {
    return this.qualifiers;
  }

  /**
   * Returns {@code true} if this {@link Qualifiers} logically
   * contains the supplied {@link Qualifiers}.
   *
   * <p>A {@link Qualifiers} is said to contain another {@link
   * Qualifiers} if either:</p>
   *
   * <ul>
   *
   * <li>the two {@link Qualifiers} instances are identical, or</li>
   *
   * <li>the {@linkplain #size() size} of the first {@link Qualifiers}
   * is greater than the {@linkplain #size() size} of the second
   * {@link Qualifiers}, and the {@linkplain #entrySet() entry set} of
   * this {@link Qualifiers} {@linkplain
   * Set#containsAll(java.util.Collection) contains all} of the
   * entries in the second {@link Qualifiers}' {@linkplain #entrySet()
   * entry set}</li>
   *
   * </ul>
   *
   * @param other the {@link Qualifiers} to test; must not be {@code
   * null}
   *
   * @return {@code true} if this {@link Qualifiers} logically
   * contains the supplied {@link Qualifiers}
   *
   * @exception NullPointerException if {@code other} is {@code null}
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   *
   * @see #size()
   *
   * @see #entrySet()
   *
   * @see Set#containsAll(java.util.Collection)
   */
  public final boolean contains(final Qualifiers<?, ?> other) {
    return this == other || this.size() >= other.size() && this.entrySet().containsAll(other.entrySet());
  }

  /**
   * Returns {@code true} if this {@link Qualifiers} has no entries.
   *
   * @return {@code true} if this {@link Qualifiers} has no entries
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   *
   * @see #qualifiers()
   *
   * @see Map#isEmpty()
   */
  public final boolean isEmpty() {
    return this.qualifiers().isEmpty();
  }

  /**
   * Returns the number of entries in this {@link Qualifiers}.
   *
   * <p>The number returned is always {@code 0} or greater.</p>
   *
   * @return the number of entries in this {@link Qualifiers}
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   *
   * @see #qualifiers()
   *
   * @see Map#size()
   */
  public final int size() {
    return this.qualifiers().size();
  }

  /**
   * Returns a non-{@code null}, immutable {@link Set} of immutable
   * {@link Entry} instances representing individual qualifier
   * entries.
   *
   * @return a non-{@code null} immutable {@link Set} of immuable
   * {@link Entry} instances representing individual qualifier entries
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   *
   * @see #qualifiers()
   *
   * @see SortedMap#entrySet()
   */
  public final Set<Entry<K, V>> entrySet() {
    return this.qualifiers().entrySet();
  }

  /**
   * Returns {@code true} if and only if this {@link Qualifiers} is a
   * subset of the supplied {@link Qualifiers}.
   *
   * @param other the other {@link Qualifiers}; must not be {@code
   * null}
   *
   * @return {@code true} if and only if this {@link Qualifiers} is a
   * subset of the supplied {@link Qualifiers}
   *
   * @exception NullPointerException if {@code other} is {@code null}
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final boolean isSubsetOf(final Qualifiers<?, ?> other) {
    return other == this || other.contains(this);
  }

  /**
   * Returns the number of entries this {@link Qualifiers} has in
   * common with the supplied {@link Qualifiers}.
   *
   * <p>The number returned will be {@code 0} or greater.</p>
   *
   * @param other the other {@link Qualifiers}; may be {@code null} in
   * which case {@code 0} will be returned
   *
   * @return the number of entries this {@link Qualifiers} has in
   * common with the supplied {@link Qualifiers}
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final int intersectionSize(final Qualifiers<?, ?> other) {
    if (other == this) {
      // Just an identity check to rule this easy case out.
      return this.size();
    } else if (other == null || other.qualifiers.isEmpty()) {
      return 0;
    } else {
      final Collection<? extends Entry<?, ?>> otherEntrySet = other.qualifiers.entrySet();
      return (int)this.entrySet()
        .stream()
        .filter(otherEntrySet::contains)
        .count();
    }
  }

  /**
   * Returns the size of the <em>symmetric difference</em> between
   * this {@link Qualifiers} and the supplied {@link Qualifiers}.
   *
   * <p>The number returned is always {@code 0} or greater.</p>
   *
   * <p>The size of the symmetric difference between two {@link
   * Qualifiers} instances is the number of qualifier entries that are
   * in one {@link Qualifiers} instance but not in the other.</p>
   *
   * @param other the other {@link Qualifiers} instance; may be {@code
   * null} in which case the return value of an invocation of this
   * {@link Qualifiers}' {@link #size()} method will be returned
   *
   * @return the size of the <em>symmetric difference</em> between
   * this {@link Qualifiers} and the supplied {@link Qualifiers};
   * always {@code 0} or greater
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final int symmetricDifferenceSize(final Qualifiers<?, ?> other) {
    if (other == this) {
      // Just an identity check to rule this easy case out.
      return 0;
    } else if (other == null || other.qualifiers.isEmpty()) {
      return this.size();
    } else if (this.equals(other)) {
      return 0;
    } else {
      final Collection<Entry<?, ?>> otherSymmetricDifference = new HashSet<>(this.entrySet());
      other.qualifiers.entrySet().stream()
        .filter(Predicate.not(otherSymmetricDifference::add))
        .forEach(otherSymmetricDifference::remove);
      return otherSymmetricDifference.size();
    }
  }

  /**
   * Returns an {@link Optional} containing a {@link ConstantDesc}
   * representing this {@link Qualifiers}.
   *
   * @return an {@link Optional} containing a {@link ConstantDesc}
   * representing this {@link Qualifiers}; never {@code null}; never
   * {@linkplain Optional#isEmpty() empty}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  @Override // Constable
  public final Optional<? extends ConstantDesc> describeConstable() {
    final Collection<Entry<K, V>> entrySet = this.entrySet();
    if (entrySet.isEmpty()) {
      return
        Optional.of(DynamicConstantDesc.of(BSM_INVOKE,
                                           MethodHandleDesc.ofMethod(DirectMethodHandleDesc.Kind.STATIC,
                                                                     CD_Qualifiers,
                                                                     "of",
                                                                     MethodTypeDesc.of(CD_Qualifiers))));
    } else {
      final int bsmInvokeArgumentsLength = 2 * entrySet.size() + 1;
      final ConstantDesc[] bsmInvokeArguments = new ConstantDesc[bsmInvokeArgumentsLength];
      bsmInvokeArguments[0] =
        MethodHandleDesc.ofMethod(DirectMethodHandleDesc.Kind.STATIC,
                                  CD_Qualifiers,
                                  "of",
                                  MethodTypeDesc.of(CD_Qualifiers,
                                                    CD_Constable.arrayType()));
      int i = 1;
      for (final Entry<K, V> entry : entrySet) {
        final Constable key = entry.getKey();
        final Optional<? extends ConstantDesc> k = key == null ? Optional.empty() : key.describeConstable();
        if (k.isEmpty()) {
          return Optional.empty();
        }
        bsmInvokeArguments[i++] = k.orElseThrow();
        final Constable value = entry.getValue();
        final Optional<? extends ConstantDesc> v = value == null ? Optional.of(NULL) : value.describeConstable();
        if (v.isEmpty()) {
          return Optional.empty();
        }
        bsmInvokeArguments[i++] = v.orElseThrow();
      }
      return Optional.of(DynamicConstantDesc.of(BSM_INVOKE, bsmInvokeArguments));
    }
  }

  /**
   * Returns a hashcode for this {@link Qualifiers}.
   *
   * @return a hashcode for this {@link Qualifiers}
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   *
   * @see #qualifiers()
   *
   * @see Map#hashCode()
   */
  @Override // Object
  public final int hashCode() {
    return this.qualifiers().hashCode();
  }

  /**
   * Returns {@code true} if this {@link Qualifiers} is equal to the
   * supplied {@link Object}.
   *
   * <p>This {@link Qualifiers} is equal to the supplied {@link
   * Object} if and only if all of the following conditions hold:</p>
   *
   * <ul>
   *
   * <li>the supplied {@link Object} is non-{@code null} and returns
   * {@link Qualifiers Qualifiers.class} from its {@link
   * Object#getClass()} method</li>
   *
   * <li>The return value of an invocation of the {@link
   * #qualifiers()} method on this {@link Qualifiers} instance is
   * {@linkplain Map#equals(Object) equal to} the return value of an
   * invocation of the {@link #qualifiers()} method on the other
   * {@link Qualifiers} instance</li>
   *
   * </ul>
   *
   * @param other the {@link Object} to test; may be {@code null} in
   * which case {@code false} will be returned
   *
   * @return {@code true} if this {@link Qualifiers} is equal to the
   * supplied {@link Object}
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   *
   * @see Map#equals(Object)
   */
  @Override // Object
  public final boolean equals(final Object other) {
    if (other == this) {
      return true;
    } else if (other == null || this.getClass() != other.getClass()) {
      return false;
    } else {
      return this.qualifiers().equals(((Qualifiers)other).qualifiers());
    }
  }

  /**
   * Returns a non-{@code null} {@link String} representation of this
   * {@link Qualifiers}.
   *
   * <p>The format of the returned {@link String} is deliberately
   * unspecified.</p>
   *
   * @return a non-{@code null} {@link String} representation of this
   * {@link Qualifiers}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  @Override // Object
  public final String toString() {
    final StringJoiner sj = new StringJoiner(";");
    for (final Entry<?, ?> entry : this.entrySet()) {
      sj.add(entry.getKey() + "=" + entry.getValue());
    }
    return sj.toString();
  }


  /*
   * Static methods.
   */


  /**
   * Returns a {@link Qualifiers} whose {@link #size()} method returns
   * {@code 0} (and whose {@link #isEmpty()} method returns {@code
   * true}).
   *
   * @param <K> the type borne by the keys of the qualifiers in the
   * returned {@link Qualifiers} (somewhat moot since the returned
   * {@link Qualifiers} will be {@linkplain #isEmpty() empty})
   *
   * @param <V> the type borne by the values of the qualifiers in the
   * returned {@link Qualifiers} (somewhat moot since the returned
   * {@link Qualifiers} will be {@linkplain #isEmpty() empty})
   *
   * @return a {@link Qualifiers} whose {@link #size()} method returns
   * {@code 0} (and whose {@link #isEmpty()} method returns {@code
   * true})
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  @SuppressWarnings("unchecked")
  public static final <K extends Constable & Comparable<K>, V extends Constable> Qualifiers<K, V> of() {
    return (Qualifiers<K, V>)EMPTY_QUALIFIERS;
  }

  /**
   * Returns a {@link Qualifiers} equal to one consisting of the
   * entries represented by the supplied {@link Map}.
   *
   * @param <K> the type borne by the keys of the qualifiers in the
   * returned {@link Qualifiers}
   *
   * @param <V> the type borne by the values of the qualifiers in the
   * returned {@link Qualifiers}
   *
   * @param map the {@link Map} of qualifier names and values; may be
   * {@code null} in which case the return value of the {@link #of()}
   * method will be returned
   *
   * @return a {@link Qualifiers} equal to one consisting of the
   * entries represented by the supplied {@link Map}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   *
   * @see #of(Constable, Constable)
   */
  public static final <K extends Constable & Comparable<K>, V extends Constable> Qualifiers<K, V> of(final Map<? extends K, ? extends V> map) {
    if (map == null || map.isEmpty()) {
      return of();
    } else {
      final SortedMap<K, V> newMap = new TreeMap<>();
      map.entrySet().forEach(e -> newMap.put(e.getKey(), e.getValue()));
      return new Qualifiers<>(newMap);
    }
  }

  /**
   * Returns a new {@link Qualifiers} with a single key equal to
   * "{@code value}" and whose value is the supplied {@code value0}
   * argument.
   *
   * @param <V> the type borne by the values of the qualifiers in the
   * returned {@link Qualifiers}
   *
   * @param value0 the new value; must not be {@code null}
   *
   * @return a new {@link Qualifiers} with a single qualifier whose
   * name is {@code value} and whose value is the supplied {@code
   * value0} argument
   *
   * @exception NullPointerException if {@code value0} is {@code null}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   *
   * @see #of(Constable, Constable)
   */
  public static final <V extends Constable> Qualifiers<String, V> of(final V value0) {
    return of("value", value0);
  }

  /**
   * Returns a new {@link Qualifiers} with a single qualifier whose
   * name is the supplied {@code name0} argument, and whose value is
   * the supplied {@code value0} argument.
   *
   * @param <K> the type borne by the keys of the qualifiers in the
   * returned {@link Qualifiers}
   *
   * @param <V> the type borne by the values of the qualifiers in the
   * returned {@link Qualifiers}
   *
   * @param name0 the qualifier name; must not be {@code null}
   *
   * @param value0 the new value; must not be {@code null}
   *
   * @return a new {@link Qualifiers} with a single qualifier whose
   * name is the supplied {@code name0} argument, and whose value is
   * the supplied {@code value0} argument
   *
   * @exception NullPointerException if either {@code name0} or {@code
   * value0} is {@code null}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public static final <K extends Constable & Comparable<K>, V extends Constable> Qualifiers<K, V> of(final K name0, final V value0) {
    return new Qualifiers<>(Map.of(name0, value0));
  }

  /**
   * Returns a {@link Qualifiers} consisting of the entries
   * represented by the supplied alternating name-value pairs.
   *
   * @param <K> the type borne by the keys of the qualifiers in the
   * returned {@link Qualifiers}
   *
   * @param <V> the type borne by the values of the qualifiers in the
   * returned {@link Qualifiers}
   *
   * @param name0 a qualifier name; must not be {@code null}
   *
   * @param value0 the qualifier value corresponding to the
   * immediately preceding qualifier name; must not be {@code null}
   *
   * @param name1 a qualifier name; must not be {@code null}
   *
   * @param value1 the qualifier value corresponding to the
   * immediately preceding qualifier name; must not be {@code null}
   *
   * @return a {@link Qualifiers} consisting of the entries
   * represented by the supplied alternating name-value pairs
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public static final <K extends Constable & Comparable<K>, V extends Constable> Qualifiers<K, V> of(final K name0, final V value0,
                                                                                                     final K name1, final V value1) {
    final SortedMap<K, V> map = new TreeMap<>();
    map.put(name0, value0);
    map.put(name1, value1);
    return new Qualifiers<>(map);
  }

  /**
   * Returns a {@link Qualifiers} consisting of the entries
   * represented by the supplied alternating name-value pairs.
   *
   * @param <K> the type borne by the keys of the qualifiers in the
   * returned {@link Qualifiers}
   *
   * @param <V> the type borne by the values of the qualifiers in the
   * returned {@link Qualifiers}
   *
   * @param name0 a qualifier name; must not be {@code null}
   *
   * @param value0 the qualifier value corresponding to the
   * immediately preceding qualifier name; must not be {@code null}
   *
   * @param name1 a qualifier name; must not be {@code null}
   *
   * @param value1 the qualifier value corresponding to the
   * immediately preceding qualifier name; must not be {@code null}
   *
   * @param name2 a qualifier name; must not be {@code null}
   *
   * @param value2 the qualifier value corresponding to the
   * immediately preceding qualifier name; must not be {@code null}
   *
   * @return a {@link Qualifiers} consisting of the entries
   * represented by the supplied alternating name-value pairs
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public static final <K extends Constable & Comparable<K>, V extends Constable> Qualifiers<K, V> of(final K name0, final V value0,
                                                                                                     final K name1, final V value1,
                                                                                                     final K name2, final V value2) {
    final SortedMap<K, V> map = new TreeMap<>();
    map.put(name0, value0);
    map.put(name1, value1);
    map.put(name2, value2);
    return new Qualifiers<>(map);
  }

  /**
   * Returns a {@link Qualifiers} consisting of the entries
   * represented by the supplied alternating name-value pairs.
   *
   * <p>The supplied {@code nameValuePairs} must be an even-numbered
   * array of non-{@code null} {@link Constable} instances, where the
   * zero-based even-numbered elements are {@link String}s represents
   * qualifier names, and the zero-based odd-numbered {@link
   * Constable} elements represent qualifier values.</p>
   *
   * @param <K> the type borne by the keys of the qualifiers in the
   * returned {@link Qualifiers}
   *
   * @param <V> the type borne by the values of the qualifiers in the
   * returned {@link Qualifiers}
   * 
   * @param nameValuePairs the name-value pairs as described above;
   * may be {@code null} in which case the return value of the {@link
   * #of()} method will be returned
   *
   * @return a {@link Qualifiers} consisting of the entries
   * represented by the supplied alternating name-value pairs
   *
   * @exception IllegalArgumentException if an even number of
   * arguments is not supplied
   *
   * @exception ClassCastException if elements in the supplied array
   * at positions {@code 0}, {@code 2}, {@code 4}… are not {@link
   * String}s
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  @SuppressWarnings("unchecked")
  public static final <K extends Constable & Comparable<K>, V extends Constable> Qualifiers<K, V> of(final Constable... nameValuePairs) {
    if (nameValuePairs == null || nameValuePairs.length <= 0) {
      return Qualifiers.of();
    } else if (nameValuePairs.length % 2 != 0) {
      throw new IllegalArgumentException("nameValuePairs: " + Arrays.toString(nameValuePairs));
    } else {
      final SortedMap<K, V> map = new TreeMap<>();
      for (int i = 0; i < nameValuePairs.length; i++) {
        map.put((K)nameValuePairs[i++], (V)nameValuePairs[i]);
      }
      return new Qualifiers<>(map);
    }
  }

}
