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
import java.lang.constant.DynamicConstantDesc;
import java.lang.constant.MethodHandleDesc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.TreeSet;

import java.util.function.Predicate;

import java.util.stream.Stream;

import org.microbean.constant.Constables;

import static java.lang.constant.ConstantDescs.BSM_INVOKE;
import static java.lang.constant.ConstantDescs.CD_Collection;

import static java.util.Collections.emptySortedSet;
import static java.util.Collections.unmodifiableSortedSet;

import static org.microbean.qualifier.ConstantDescs.CD_Iterable;

/**
 * An abstract, immutable {@linkplain Iterable iterable} collection of
 * {@link Binding} instances.
 *
 * @param <V> the type of a {@link Binding}'s {@linkplain
 * Binding#attributes() attribute values}
 *
 * @param <B> The concrete subtype of this class
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 */
public abstract class Bindings<V, B extends Binding<V, B>> implements Constable, Iterable<B> {


  /*
   * Instance fields.
   */


  private final SortedSet<B> bindings;


  /*
   * Constructors.
   */


  /**
   * Creates a new {@link Bindings}.
   *
   * <p>Duplicate {@link Binding} instances contained by the supplied
   * {@link Iterable} will be discarded.</p>
   *
   * @param bindings the {@link Binding} instances that will be
   * contained by this {@link Bindings}
   */
  protected Bindings(final Iterable<? extends B> bindings) {
    super();
    if (bindings == null) {
      this.bindings = emptySortedSet();
    } else {
      final Iterator<? extends B> i = bindings.iterator();
      if (i.hasNext()) {
        final SortedSet<B> newBindings = new TreeSet<>();
        newBindings.add(i.next());
        while (i.hasNext()) {
          newBindings.add(i.next());
        }
        this.bindings = unmodifiableSortedSet(newBindings);
      } else {
        this.bindings = emptySortedSet();
      }
    }
  }


  /*
   * Instance methods.
   */


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
    return this.bindings.isEmpty();
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
    return this.bindings.size();
  }

  /**
   * Returns the sole {@link Binding} {@linkplain Binding#value()
   * value} whose {@linkplain Binding#name() name} {@linkplain
   * String#equals(Object) is equal to} the supplied {@code name}, or
   * {@code null} if either there is no such {@link Binding} or there
   * are several {@link Binding}s with the supplied {@code name}.
   *
   * @param name the name; may be {@code null} in which case {@code
   * false} will be returned
   *
   * @return the sole {@link Binding} {@linkplain Binding#value()
   * value} whose {@linkplain Binding#name() name} {@linkplain
   * String#equals(Object) is equal to} the supplied {@code name}, or
   * {@code null} if either there is no such {@link Binding} or there
   * are several {@link Binding}s with the supplied {@code name}.
   *
   * @nullability This method may return {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   *
   * @see #unique(String)
   */
  public final V uniqueValue(final String name) {
    final B b = this.unique(name);
    return b == null ? null : b.value();
  }

  /**
   * Returns the sole {@link Binding} instance whose {@linkplain
   * Binding#name() name} {@linkplain String#equals(Object) is equal
   * to} the supplied {@code name}, or {@code null} if either there is
   * no such {@link Binding} or there are several {@link Binding}s
   * with the supplied {@code name}.
   *
   * @param name the name; may be {@code null} in which case {@code
   * false} will be returned
   *
   * @return the sole {@link Binding} instance whose {@linkplain
   * Binding#name() name} {@linkplain String#equals(Object) is equal
   * to} the supplied {@code name}, or {@code null} if either there is
   * no such {@link Binding} or there are several {@link Binding}s
   * with the supplied {@code name}.
   *
   * @nullability This method may return {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final B unique(final String name) {
    B returnValue = null;
    if (name != null) {
      for (final B b : this) {
        if (b.name().equals(name)) {
          if (returnValue == null) {
            returnValue = b;
          } else {
            return null;
          }
        }
      }
    }
    return returnValue;
  }

  /**
   * Returns {@code true} if and only if the supplied {@link Object}
   * is contained by this {@link Bindings}.
   *
   * <p>If the {@link Object} is a {@link Binding}, the containment
   * check is performed via an equality check.  If the {@link Object}
   * is a {@link String}, then this method will return {@code true} if
   * there is a {@link Binding} contained by this {@link Bindings}
   * whose {@linkplain Binding#name() name} {@linkplain
   * String#equals(Object) is equal to} the supplied {@link
   * String}.</p>
   *
   * <p>There may be many {@link Binding} instances contained by this
   * {@link Bindings} whose {@linkplain Binding#name() names} are
   * {@linkplain String#equals(Object) equal}.</p>
   *
   * @param o the {@link Object} to test; {@code true} return values
   * are possible only when this {@link Object} is either a {@link
   * Binding} or a {@link String}
   *
   * @return {@code true} if and only if the supplied {@link Object}
   * is contained by this {@link Bindings}
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   *
   * @see #containsUnique(String)
   */
  public final boolean contains(final Object o) {
    if (o == null || o instanceof Binding<?, ?>) {
      return this.bindings.contains(o);
    }
    for (final B b : this) {
      if (b.name().equals(o)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns {@code true} if and only if there is exactly one {@link
   * Binding} contained by this {@link Bindings} whose {@linkplain
   * Binding#name() name} {@linkplain String#equals(Object) is equal
   * to} the supplied {@code name}.
   *
   * @param name the name to test; may be {@code null} in which case
   * {@code false} will be returned
   *
   * @return {@code true} if and only if there is exactly one {@link
   * Binding} contained by this {@link Bindings} whose {@linkplain
   * Binding#name() name} {@linkplain String#equals(Object) is equal
   * to} the supplied {@code name}
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final boolean containsUnique(final String name) {
    return this.unique(name) != null;
  }

  /**
   * Returns a {@link Stream} of this {@link Bindings}' {@linkplain
   * Binding entries}.
   *
   * @return a {@link Stream} of this {@link Bindings}' {@linkplain
   * Binding entries}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final Stream<B> stream() {
    return this.bindings.stream();
  }

  /**
   * Returns a non-{@code null}, immutable {@link Iterator} of {@link
   * Binding} instances contained by this {@link Bindings}.
   *
   * @return a non-{@code null}, immutable {@link Iterator} of {@link
   * Binding} instances contained by this {@link Bindings}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  @Override // Iterable<B>
  public final Iterator<B> iterator() {
    return this.bindings.iterator();
  }

  /**
   * Returns a non-{@code null}, immutable {@link Spliterator} of
   * {@link Binding} instances contained by this {@link Bindings}.
   *
   * @return a non-{@code null}, immutable {@link Spliterator} of
   * {@link Binding} instances contained by this {@link Bindings}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  @Override // Iterable<B>
  public final Spliterator<B> spliterator() {
    return this.bindings.spliterator();
  }

  /**
   * Returns the number of entries this {@link Bindings} has in common
   * with the supplied {@link Iterable}.
   *
   * <p>The number returned will be {@code 0} or greater.</p>
   *
   * @param other the {@link Iterable}; may be {@code null} in which
   * case {@code 0} will be returned
   *
   * @return the number of entries this {@link Bindings} has in common
   * with the supplied {@link Iterable}
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final int intersectionSize(final Iterable<?> other) {
    if (other == null) {
      return 0;
    } else if (this == other) {
      return this.size();
    } else {
      final Iterator<?> i = other.iterator();
      if (i.hasNext()) {
        int count = this.bindings.contains(i.next()) ? 1 : 0;
        while (i.hasNext()) {
          if (this.bindings.contains(i.next())) {
            ++count;
          }
        }
        return count;
      } else {
        return 0;
      }
    }
  }

  /**
   * Returns the size of the <em>symmetric difference</em> between
   * this {@link Bindings} and the supplied {@link Iterable}.
   *
   * <p>The number returned is always {@code 0} or greater.</p>
   *
   * <p>The size of the symmetric difference between this {@link
   * Bindings} instance and the supplied {@link Iterable} is the
   * number of entries that are in one of these two objects but not in
   * the other.</p>
   *
   * @param other an {@link Iterable}; may be {@code null} in which
   * case the result of an invocation of this {@link Bindings}'
   * {@link #size()} method will be returned
   *
   * @return the size of the <em>symmetric difference</em> between
   * this {@link Bindings} and the supplied {@link Iterable}; always
   * {@code 0} or greater
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final int symmetricDifferenceSize(final Iterable<?> other) {
    if (other == null) {
      return this.size();
    } else if (this == other) {
      return 0;
    } else {
      final Iterator<?> i = other.iterator();
      if (i.hasNext()) {
        final Set<? super Object> symmetricDifference = new HashSet<>(this.bindings);
        Object b = i.next();
        if (!symmetricDifference.add(b)) {
          symmetricDifference.remove(b);
        }
        while (i.hasNext()) {
          b = i.next();
          if (!symmetricDifference.add(b)) {
            symmetricDifference.remove(b);
          }
        }
        return symmetricDifference.size();
      } else {
        return this.size();
      }
    }
  }

  /**
   * Returns an {@link Optional} housing a {@link ConstantDesc}
   * describing this {@link Bindings}, if this {@link Bindings} is
   * capable of being represented as a <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">dynamic
   * constant</a>, or an {@linkplain Optional#isEmpty() empty} {@link
   * Optional} if not.
   *
   * @return an {@link Optional} housing a {@link ConstantDesc}
   * describing this {@link Binding}, if this {@link Bindings} is
   * capable of being represented as a <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">dynamic
   * constant</a>, or an {@linkplain Optional#isEmpty() empty} {@link
   * Optional} if not
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   *
   * @see #describeConstructor()
   *
   * @see <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">Dynamically-computed
   * constants</a>
   */
  @Override // Constable
  public final Optional<? extends ConstantDesc> describeConstable() {
    final MethodHandleDesc constructor = this.describeConstructor();
    if (constructor == null) {
      return Optional.empty();
    }
    final ConstantDesc bindingsCd = Constables.describeConstable(this.bindings).orElse(null);
    if (bindingsCd == null) {
      return Optional.empty();
    }
    return
      Optional.of(DynamicConstantDesc.of(BSM_INVOKE, constructor, bindingsCd));
  }

  /**
   * Returns a {@link MethodHandleDesc} describing the constructor or
   * {@code static} method that will be used to create a <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">dynamic
   * constant</a> representing this {@link Bindings}.
   *
   * @return a {@link MethodHandleDesc} describing the constructor or
   * {@code static} method that will be used to create a <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">dynamic
   * constant</a> representing this {@link Bindings}
   *
   * @nullability This method does not, and its overrides must not,
   * return {@code null}.
   *
   * @idempotency This method is, and its overrides must be,
   * idempotent and deterministic.
   *
   * @threadsafety This method is, and its overrides must be, safe for
   * concurrent use by multiple threads.
   */
  protected MethodHandleDesc describeConstructor() {
    return
      MethodHandleDesc.ofConstructor(this.getClass().describeConstable().orElseThrow(),
                                     new ClassDesc[] { CD_Iterable });
  }

  /**
   * Returns a hashcode for this {@link Bindings}.
   *
   * @return a hashcode for this {@link Bindings}
   *
   * @idempotency This method is, and its overrides must be,
   * idempotent and deterministic.
   *
   * @threadsafety This method is, and its overrides must be, safe for
   * concurrent use by multiple threads.
   *
   * @see #equals(Object)
   */
  @Override // Object
  public final int hashCode() {
    return this.bindings.hashCode();
  }

  /**
   * Returns {@code true} if and only if this {@link Bindings} is
   * equal to the supplied {@link Object}.
   *
   * <p>The supplied {@link Object} is considered to be equal to this
   * {@link Bindings} if and only if:</p>
   *
   * <ul>
   *
   * <li>Its {@linkplain #getClass() class} is identical to this
   * {@link Bindings}' {@linkplain #getClass() class}, and</li>
   *
   * <li>Each of the {@link Binding} instances it contains is
   * {@linkplain Binding#equals(Object) equal to} the corresponding
   * {@link Binding} instance contained by this {@link Bindings}</li>
   *
   * </ul>
   *
   * @param other the {@link Object} to test; may be {@code null} in
   * which case {@code false} will be returned
   *
   * @return {@code true} if the supplied {@link Object} is equal to
   * this {@link Binding}
   *
   * @idempotency This method is, and its overrides must be,
   * idempotent and deterministic.
   *
   * @threadsafety This method is, and its overrides must be, safe for
   * concurrent use by multiple threads.
   *
   * @see #hashCode()
   *
   * @see Binding#equals(Object)
   */
  @Override // Object
  public final boolean equals(final Object other) {
    if (other == this) {
      return true;
    } else if (other != null && other.getClass() == this.getClass()) {
      final Bindings<?, ?> her = (Bindings<?, ?>)other;
      return
        Objects.equals(this.bindings, her.bindings);
    } else {
      return false;
    }
  }

  /**
   * Returns a {@link String} representation of this {@link Bindings}.
   *
   * <p>The format of the returned {@link String} is deliberately
   * undefined and may change between versions of this class without
   * prior notice.</p>
   *
   * @return a {@link String} representation of this {@link Bindings}
   *
   * @nullability This method does not, and its overrides must not,
   * return {@code null}.
   *
   * @idempotency This method is, and its overrides must be,
   * idempotent and deterministic.
   *
   * @threadsafety This method is, and its overrides must be, safe for
   * concurrent use by multiple threads.
   */
  @Override // Object
  public String toString() {
    return this.bindings.toString();
  }

}
