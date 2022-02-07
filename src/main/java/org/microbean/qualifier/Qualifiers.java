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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.TreeMap;

import java.util.function.Function;
import java.util.function.Predicate;

import static java.lang.constant.ConstantDescs.BSM_INVOKE;
import static java.lang.constant.ConstantDescs.DEFAULT_NAME;
import static java.lang.constant.ConstantDescs.NULL;
import static java.lang.constant.DirectMethodHandleDesc.Kind.STATIC;

import static java.util.Collections.emptySortedMap;
import static java.util.Collections.unmodifiableSortedMap;

import static org.microbean.qualifier.ConstantDescs.CD_Constable;
import static org.microbean.qualifier.ConstantDescs.CD_Qualifiers;

/**
 * A {@link Constable}, immutable, sorted set of key-value pairs that
 * can be used to further qualify an object for many different
 * purposes.
 *
 * <p>Keys are sorted according to their natural order.</p>
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
public final class Qualifiers<K extends Constable & Comparable<K>, V extends Constable> implements Constable, Iterable<Entry<K, V>> {


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


  /**
   * Creates a new {@linkplain Map#isEmpty() empty} {@link
   * Qualifiers}.
   */
  public Qualifiers() {
    this(Map.of(), false);
  }

  /**
   * Creates a new {@link Qualifiers}.
   *
   * @param qualifiers a {@link Map} representing the qualifiers; may be
   * {@code null} in which case an {@linkplain Map#isEmpty() empty
   * <code>Map</code>} will be used instead
   */
  public Qualifiers(final Map<? extends K, ? extends V> qualifiers) {
    this(qualifiers, true);
  }

  @SuppressWarnings("unchecked")
  private Qualifiers(final Map<? extends K, ? extends V> qualifiers, final boolean copy) {
    if (qualifiers == null || qualifiers.isEmpty()) {
      this.qualifiers = emptySortedMap();
    } else if (copy || !(qualifiers instanceof SortedMap)) {
      this.qualifiers = unmodifiableSortedMap(new TreeMap<>(qualifiers));
    } else {
      this.qualifiers = unmodifiableSortedMap((SortedMap<K, V>)qualifiers);
    }
  }


  /*
   * Instance methods.
   */


  /**
   * Returns {@code true} if this {@link Qualifiers} is logically empty.
   *
   * @return {@code true} if this {@link Qualifiers} is logically
   * empty
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   *
   * @see #size()
   */
  public final boolean isEmpty() {
    return this.toMap().isEmpty();
  }

  /**
   * Returns {@code 0} or a positive integer describing the number of
   * entries contained by this {@link Qualifiers}.
   *
   * @return the size of this {@link Qualifiers}
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final int size() {
    return this.toMap().size();
  }

  /**
   * Returns a non-{@code null}, immutable {@link Iterator} of {@link
   * Entry} instances contained by this {@link Qualifiers}.
   *
   * @return a non-{@code null}, immutable {@link Iterator} of {@link
   * Entry} instances contained by this {@link Qualifiers}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  @Override // Iterable<K, V>
  public final Iterator<Entry<K, V>> iterator() {
    return this.toMap().entrySet().iterator();
  }

  /**
   * Returns a non-{@code null}, immutable {@link Spliterator} of
   * {@link Entry} instances contained by this {@link Qualifiers}.
   *
   * @return a non-{@code null}, immutable {@link Spliterator} of
   * {@link Entry} instances contained by this {@link Qualifiers}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  @Override // Iterable<K, V>
  public final Spliterator<Entry<K, V>> spliterator() {
    return this.toMap().entrySet().spliterator();
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
   * <li>the {@linkplain Map#size() size} of the first {@link Qualifiers}
   * is greater than the {@linkplain Map#size() size} of the second
   * {@link Qualifiers}, and the {@linkplain Map#entrySet() entry set}
   * of this {@link Qualifiers} {@linkplain
   * Set#containsAll(java.util.Collection) contains all} of the
   * entries in the second {@link Qualifiers}' {@linkplain
   * Map#entrySet() entry set}</li>
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
   * @see Map#size()
   *
   * @see Set#containsAll(java.util.Collection)
   */
  public final boolean contains(final Qualifiers<?, ?> other) {
    return this == other || this.size() >= other.size() && this.toMap().entrySet().containsAll(other.toMap().entrySet());
  }

  /**
   * Returns {@code true} if and only if this {@link Qualifiers}
   * logically contains an entry equal to the supplied {@link Entry}.
   *
   * @param e the {@link Entry} to test; may be {@code null} in which
   * case {@code false} will be returned
   *
   * @return {@code true} if and only if this {@link Qualifiers}
   * logically contains an entry equal to the supplied {@link Entry}
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final boolean contains(final Entry<?, ?> e) {
    final Object v = e == null ? null : this.toMap().get(e.getKey());
    return v != null && v.equals(e.getValue());
  }

  /**
   * Returns {@code true} if and only if this {@link Qualifiers}
   * logically contains a key equal to the supplied {@code key}.
   *
   * @param key the key in question; may be {@code null} in which case
   * {@code false} will be returned
   *
   * @return {@code true} if and only if this {@link Qualifiers}
   * logically contains a key equal to the supplied {@code key}
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final boolean containsKey(final Object key) {
    return key != null && this.toMap().containsKey(key);
  }

  /**
   * Returns the value indexed under the supplied {@code key}, or
   * {@code null} if there is no such value.
   *
   * @param key the key in question; may be {@code null} in which case
   * {@code false} will be returned
   *
   * @return the value indexed under the supplied {@code key}, or
   * {@code null} if there is no such value
   *
   * @nullability This method may return {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final V get(final Object key) {
    return key == null ? null : this.toMap().get(key);
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
    } else if (other == null || other.isEmpty()) {
      return 0;
    } else {
      final Collection<? extends Entry<?, ?>> otherEntrySet = other.toMap().entrySet();
      return (int)this.toMap().entrySet()
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
   * null}
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
    } else if (other == null || other.isEmpty()) {
      return this.size();
    } else if (this.equals(other)) {
      return 0;
    } else {
      final Collection<Entry<?, ?>> otherSymmetricDifference = new HashSet<>(this.toMap().entrySet());
      other.toMap()
        .entrySet()
        .stream()
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
    final Collection<Entry<K, V>> entrySet = this.toMap().entrySet();
    if (entrySet.isEmpty()) {
      return
        Optional.of(DynamicConstantDesc.ofNamed(BSM_INVOKE,
                                                DEFAULT_NAME,
                                                CD_Qualifiers,
                                                MethodHandleDesc.ofMethod(STATIC,
                                                                          CD_Qualifiers,
                                                                          "of",
                                                                          MethodTypeDesc.of(CD_Qualifiers))));
    } else {
      final int bsmInvokeArgumentsLength = 2 * entrySet.size() + 1;
      final ConstantDesc[] bsmInvokeArguments = new ConstantDesc[bsmInvokeArgumentsLength];
      bsmInvokeArguments[0] =
        MethodHandleDesc.ofMethod(STATIC,
                                  CD_Qualifiers,
                                  "of",
                                  MethodTypeDesc.of(CD_Qualifiers,
                                                    CD_Constable.arrayType()));
      int i = 1;
      for (final Entry<K, V> entry : entrySet) {
        final Constable key = entry.getKey();
        final ConstantDesc k = key == null ? null : key.describeConstable().orElse(null);
        if (k == null) {
          return Optional.empty();
        }
        bsmInvokeArguments[i++] = k;
        final Constable value = entry.getValue();
        final ConstantDesc v = value == null ? NULL : value.describeConstable().orElse(null);
        if (v == null) {
          return Optional.empty();
        }
        bsmInvokeArguments[i++] = v;
      }
      return Optional.of(DynamicConstantDesc.ofNamed(BSM_INVOKE,
                                                     DEFAULT_NAME,
                                                     CD_Qualifiers,
                                                     bsmInvokeArguments));
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
   * @see Object#hashCode()
   */
  @Override // Object
  public final int hashCode() {
    return this.toMap().hashCode();
  }

  /**
   * Returns {@code true} if this {@link Qualifiers} is equal to the
   * supplied {@link Object}.
   *
   * @param other the object to test; may be {@code null} in which
   * case {@code false} will be returned
   *
   * @return {@code true} if this {@link Qualifiers} is equal to the
   * supplied {@link Object}; {@code false} otherwise
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe  for concurrent use by multiple
   * threads.
   */
  @Override // Object
  public final boolean equals(final Object other) {
    if (other == this) {
      return true;
    } else if (other != null && other.getClass() == this.getClass()) {
      final Qualifiers<?, ?> her = (Qualifiers<?, ?>)other;
      return
        Objects.equals(this.toMap(), her.toMap());
    } else {
      return false;
    }
  }

  /**
   * Returns a non-{@code null} {@link String} representation of this
   * {@link Qualifiers}.
   *
   * @return a non-{@code null} {@link String} representation of this
   * {@link Qualifiers}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe  for concurrent use by multiple
   * threads.
   */
  @Override // Object
  public final String toString() {
    return this.toMap().toString();
  }

  /**
   * Returns a <strong>new</strong> {@link Qualifiers} whose keys are
   * produced by the supplied {@link Function}, which is expected to
   * prepend a prefix to the original key and return the result.
   *
   * @param <K2> the type borne by the new keys
   *
   * @param f a non-{@code null}, deterministic, idempotent {@link
   * Function} that accepts keys drawn from this {@link Qualifiers}'
   * {@linkplain Map#entrySet() entry set} and returns a non-{@code
   * null} prefixed version of that key; must not be {@code null}
   *
   * @return a <strong>new</strong> {@link Qualifiers} whose keys have
   * been prefixed by the actions of the supplied {@link Function}
   *
   * @exception NullPointerException if {@code f} is {@code null}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic,
   * assuming the supplied {@link Function} is.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads, assuming the supplied {@link Function} is
   */
  public final <K2 extends Constable & Comparable<K2>> Qualifiers<K2, V> withPrefix(final Function<? super K, ? extends K2> f) {
    final Map<K2, V> map = new TreeMap<>();
    for (final Entry<? extends K, ? extends V> entry : this.toMap().entrySet()) {
      map.put(f.apply(entry.getKey()), entry.getValue());
    }
    return new Qualifiers<>(map, false);
  }

  /**
   * Returns a {@link Qualifiers} with additional
   * entries sourced from the supplied {@link Qualifiers}.
   *
   * <p>The returned {@link Qualifiers} <strong>will be new</strong>
   * unless {@code qualifiers} is {@code null} or {@linkplain
   * #isEmpty() empty}, in which case {@code this} will be
   * returned.</p>
   *
   * @param qualifiers a {@link Qualifiers} whose entries will be part
   * of the new {@link Qualifiers}, displacing any that already exist;
   * may be {@code null} or {@linkplain Map#isEmpty() empty} in which
   * case {@code this} will be returned; all keys and values must be
   * immutable (not just unmodifiable) or undefined behavior will
   * result
   *
   * @return a {@link Qualifiers} with additional entries sourced from
   * the supplied {@link Qualifiers}; never {@code null}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final Qualifiers<K, V> plus(final Qualifiers<? extends K, ? extends V> qualifiers) {
    if (qualifiers == null || qualifiers.isEmpty()) {
      return this;
    } else {
      return new Qualifiers<>(qualifiers.toMap(), false);
    }
  }

  /**
   * Returns a {@link Qualifiers} with additional
   * entries sourced from the supplied {@link Map}.
   *
   * <p>The returned {@link Qualifiers} <strong>will be new</strong>
   * unless {@code qualifiers} is {@code null} or {@linkplain
   * Map#isEmpty() empty}, in which case {@code this} will be
   * returned.</p>
   *
   * @param qualifiers a {@link Map} whose entries will be part of the
   * new {@link Qualifiers}, displacing any that already exist; may be
   * {@code null} or {@linkplain Map#isEmpty() empty} in which case
   * {@code this} will be returned; all keys and values must be
   * immutable (not just unmodifiable) or undefined behavior will
   * result
   *
   * @return a {@link Qualifiers} with additional entries sourced from
   * the supplied {@link Map}; never {@code null}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final Qualifiers<K, V> plus(final Map<? extends K, ? extends V> qualifiers) {
    if (qualifiers == null || qualifiers.isEmpty()) {
      return this;
    } else {
      final Map<K, V> map = new TreeMap<>(this.toMap());
      map.putAll(qualifiers);
      return new Qualifiers<>(map, false);
    }
  }

  /**
   * Returns an immutable {@link Map} representation of this {@link
   * Qualifiers}.
   *
   * @return an immutable {@link Map} representation of this {@link
   * Qualifiers}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final Map<K, V> toMap() {
    return this.qualifiers;
  }


  /*
   * Static methods.
   */


  /**
   * Returns a {@link Qualifiers} that is logically empty.
   *
   * @param <K> the type borne by the keys of the qualifiers in the
   * returned {@link Qualifiers} (somewhat moot since the returned
   * {@link Qualifiers} will be empty)
   *
   * @param <V> the type borne by the values of the qualifiers in the
   * returned {@link Qualifiers} (somewhat moot since the returned
   * {@link Qualifiers} will be empty)
   *
   * @return a {@link Qualifiers} that is logically empty
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  // Used by #describeConstable()
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
  @SuppressWarnings("unchecked")
  public static final <K extends Constable & Comparable<K>, V extends Constable> Qualifiers<K, V> of(final Map<? extends K, ? extends V> map) {
    if (map == null || map.isEmpty()) {
      return of();
    } else {
      return new Qualifiers<>((Map<K, V>)map);
    }
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
    return new Qualifiers<>(Map.of(name0, value0, name1, value1));
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
    return new Qualifiers<>(Map.of(name0, value0, name1, value1, name2, value2));
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
   * at even positions ({@code 0}, {@code 2}, {@code 4}…) are not of
   * type {@code K}
   *
   * @exception NullPointerException if any argument is {@code null}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  // Used by #describeConstable()
  @SuppressWarnings("unchecked")
  public static final <K extends Constable & Comparable<K>, V extends Constable> Qualifiers<K, V> of(final Constable... nameValuePairs) {
    if (nameValuePairs == null || nameValuePairs.length <= 0) {
      return of();
    } else if (nameValuePairs.length % 2 != 0) {
      throw new IllegalArgumentException("nameValuePairs: " + Arrays.toString(nameValuePairs));
    } else {
      final Map<K, V> map = new TreeMap<>();
      for (int i = 0; i < nameValuePairs.length; i++) {
        map.put((K)nameValuePairs[i++], (V)nameValuePairs[i]);
      }
      return new Qualifiers<>(map);
    }
  }

}
