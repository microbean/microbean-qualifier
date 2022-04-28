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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.TreeMap;

import java.util.function.Predicate;

import java.util.stream.Stream;

import static java.util.Collections.emptySortedMap;
import static java.util.Collections.unmodifiableSortedMap;

/**
 * An {@code abstract}, {@link Constable}, immutable, sorted set of
 * key-value pairs that can be used to further refine an object for
 * many different purposes.
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
 * @param <K> the type borne by the keys of the entries in this
 * {@link Bindings}
 *
 * @param <V> the type borne by the values of the entries in this
 * {@link Bindings}
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 */
public abstract class Bindings<K extends Comparable<? super K>, V> implements Constable, Iterable<Entry<K, V>> {


  /*
   * Instance fields.
   */


  private final SortedMap<K, V> bindings;

  private final SortedMap<K, V> info;


  /*
   * Constructors.
   */


  /**
   * Creates a new {@link Bindings}.
   *
   * @param bindings a {@link Map} representing the bindings to be
   * modeled by this {@link Bindings}; may be {@code null} in which
   * case an {@linkplain Map#isEmpty() empty <code>Map</code>} will be
   * used instead
   *
   * @param info informational key-value pairs; may be {@code
   * null} in which case an {@linkplain Map#isEmpty() empty
   * <code>Map</code>} will be used instead
   *
   * @param copy if {@code false}, the supplied {@link Map}s will not
   * be copied and will be stored by reference; it is recommended to
   * supply {@code true} for this parameter in all but the most
   * specific circumstances
   */
  @SuppressWarnings("unchecked")
  protected Bindings(final Map<? extends K, ? extends V> bindings,
                     final Map<? extends K, ? extends V> info,
                     final boolean copy) {
    super();
    if (bindings == null || bindings.isEmpty()) {
      this.bindings = emptySortedMap();
    } else if (copy || !(bindings instanceof SortedMap)) {
      this.bindings = unmodifiableSortedMap(new TreeMap<>(bindings));
    } else {
      this.bindings = unmodifiableSortedMap((SortedMap<K, V>)bindings);
    }
    if (info == null || info.isEmpty()) {
      this.info = emptySortedMap();
    } else {
      final TreeMap<K, V> map = new TreeMap<>();
      for (final K key : info.keySet()) {
        if (!this.bindings.containsKey(key)) {
          map.put(key, info.get(key));
        }
      }
      this.info = unmodifiableSortedMap(map);
    }
  }

  /**
   * Returns an immutable {@link Map} representation of this {@link
   * Bindings}.
   *
   * @return an immutable {@link Map} representation of this {@link
   * Bindings}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final Map<K, V> toMap() {
    return this.bindings;
  }

  /**
   * Returns an immutable {@link Map} representation of the
   * informational {@link Map} {@linkplain #Bindings(Map, Map,
   * boolean) supplied at construction time}.
   *
   * @return an immutable {@link Map} representation of the
   * informational {@link Map} {@linkplain #Bindings(Map, Map,
   * boolean) supplied at construction time}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final Map<K, V> info() {
    return this.info;
  }

  /**
   * Returns {@code true} if this {@link Bindings} is logically empty.
   *
   * @return {@code true} if this {@link Bindings} is logically empty
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
   * entries contained by this {@link Bindings}.
   *
   * @return the size of this {@link Bindings}
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
   * Entry} instances contained by this {@link Bindings}.
   *
   * @return a non-{@code null}, immutable {@link Iterator} of {@link
   * Entry} instances contained by this {@link Bindings}
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
   * {@link Entry} instances contained by this {@link Bindings}.
   *
   * @return a non-{@code null}, immutable {@link Spliterator} of
   * {@link Entry} instances contained by this {@link Bindings}
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
   * Returns a possibly parallel {@link Stream} of this {@link
   * Bindings}' {@linkplain Entry entries}.
   *
   * @return a possibly parallel {@link Stream} of this {@link
   * Bindings}' {@linkplain Entry entries}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final Stream<Entry<K, V>> parallelStream() {
    return this.toMap().entrySet().parallelStream();
  }

  /**
   * Returns a possibly parallel {@link Stream} of this {@link
   * Bindings}' {@linkplain Entry entries}.
   *
   * @return a possibly parallel {@link Stream} of this {@link
   * Bindings}' {@linkplain Entry entries}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final Stream<Entry<K, V>> stream() {
    return this.toMap().entrySet().stream();
  }

  /**
   * Returns {@code true} if this {@link Bindings} logically contains
   * the supplied {@link Bindings}.
   *
   * <p>A {@link Bindings} is said to contain another {@link
   * Bindings} if either:</p>
   *
   * <ul>
   *
   * <li>the two {@link Bindings} instances are identical, or</li>
   *
   * <li>the {@linkplain Map#size() size} of the first {@link
   * Bindings} is greater than the {@linkplain Map#size() size} of the
   * second {@link Bindings}, and the {@linkplain Map#entrySet() entry
   * set} of this {@link Bindings} {@linkplain
   * java.util.Set#containsAll(java.util.Collection) contains all} of
   * the entries in the second {@link Bindings}' {@linkplain
   * Map#entrySet() entry set}</li>
   *
   * </ul>
   *
   * @param other the {@link Bindings} to test; must not be {@code
   * null}
   *
   * @return {@code true} if this {@link Bindings} logically
   * contains the supplied {@link Bindings}
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
   * @see java.util.Set#containsAll(java.util.Collection)
   */
  public final boolean contains(final Bindings<?, ?> other) {
    return this == other || this.size() >= other.size() && this.toMap().entrySet().containsAll(other.toMap().entrySet()); 
  }

  /**
   * Returns {@code true} if and only if this {@link Bindings}
   * logically contains an entry equal to the supplied {@link Entry}.
   *
   * @param e the {@link Entry} to test; may be {@code null} in which
   * case {@code false} will be returned
   *
   * @return {@code true} if and only if this {@link Bindings}
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
   * Returns {@code true} if and only if this {@link Bindings}
   * logically contains a key equal to the supplied {@code key}.
   *
   * @param key the key in question; may be {@code null} in which case
   * {@code false} will be returned
   *
   * @return {@code true} if and only if this {@link Bindings}
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
   * Returns {@code true} if and only if this {@link Bindings} is a
   * subset of the supplied {@link Bindings}.
   *
   * @param other the other {@link Bindings}; must not be {@code
   * null}
   *
   * @return {@code true} if and only if this {@link Bindings} is a
   * subset of the supplied {@link Bindings}
   *
   * @exception NullPointerException if {@code other} is {@code null}
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final boolean isSubsetOf(final Bindings<?, ?> other) {
    return other == this || other.contains(this);
  }

  /**
   * Returns the number of entries this {@link Bindings} has in
   * common with the supplied {@link Bindings}.
   *
   * <p>The number returned will be {@code 0} or greater.</p>
   *
   * @param other the other {@link Bindings}; may be {@code null} in
   * which case {@code 0} will be returned
   *
   * @return the number of entries this {@link Bindings} has in
   * common with the supplied {@link Bindings}
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final int intersectionSize(final Bindings<?, ?> other) {
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
   * this {@link Bindings} and the supplied {@link Bindings}.
   *
   * <p>The number returned is always {@code 0} or greater.</p>
   *
   * <p>The size of the symmetric difference between two {@link
   * Bindings} instances is the number of qualifier entries that are
   * in one {@link Bindings} instance but not in the other.</p>
   *
   * @param other the other {@link Bindings} instance; may be {@code
   * null}
   *
   * @return the size of the <em>symmetric difference</em> between
   * this {@link Bindings} and the supplied {@link Bindings};
   * always {@code 0} or greater
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final int symmetricDifferenceSize(final Bindings<?, ?> other) {
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
   * Returns a hashcode for this {@link Bindings}.
   *
   * <p>Only binding entries are considered.</p>
   *
   * @return a hashcode for this {@link Bindings}
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   *
   * @see Object#hashCode()
   */
  @Override // Object
  public int hashCode() {
    return this.toMap().hashCode();
  }

  /**
   * Returns {@code true} if this {@link Bindings} is equal to the
   * supplied {@link Object}.
   *
   * <p>Only binding entries are considered.</p>
   *
   * @param other the object to test; may be {@code null} in which
   * case {@code false} will be returned
   *
   * @return {@code true} if this {@link Bindings} is equal to the
   * supplied {@link Object}; {@code false} otherwise
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe  for concurrent use by multiple
   * threads.
   */
  @Override // Object
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    } else if (other != null && other.getClass() == this.getClass()) {
      return
        Objects.equals(this.toMap(), ((Bindings<?, ?>)other).toMap());
    } else {
      return false;
    }
  }

  /**
   * Returns a non-{@code null} {@link String} representation of this
   * {@link Bindings}.
   *
   * <p>The format of the returned {@link String} is deliberately
   * undefined and subject to change without notice between versions
   * of this class.</p>
   *
   * @return a non-{@code null} {@link String} representation of this
   * {@link Bindings}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe  for concurrent use by multiple
   * threads.
   *
   * @see #toString(boolean)
   */
  @Override // Object
  public String toString() {
    return this.toString(true);
  }

  /**
   * Returns a non-{@code null} {@link String} representation of this
   * {@link Bindings}.
   *
   * <p>The format of the returned {@link String} is deliberately
   * undefined and subject to change without notice between versions
   * of this class.</p>
   *
   * @param withInfo if {@code true}, then a {@link String}
   * representatino of the {@linkplain #info() informational bindings}
   * will be included in the returned {@link String}
   *
   * @return a non-{@code null} {@link String} representation of this
   * {@link Bindings}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe  for concurrent use by multiple
   * threads.
   *
   * @see #toString(boolean)
   */
  public String toString(final boolean withInfo) {
    if (withInfo) {
      return Map.of("bindings", this.toMap().toString(), "info", this.info().toString()).toString();
    } else {
      return Map.of("bindings", this.toMap().toString()).toString();
    }
  }

}
