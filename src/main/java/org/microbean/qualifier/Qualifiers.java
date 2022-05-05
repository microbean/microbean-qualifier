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

import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

import java.util.function.Function;

import static java.lang.constant.ConstantDescs.CD_Collection;

import static java.lang.constant.DirectMethodHandleDesc.Kind.STATIC;

import static org.microbean.qualifier.ConstantDescs.CD_Iterable;
import static org.microbean.qualifier.ConstantDescs.CD_Qualifiers;

/**
 * An immutable {@link Bindings} containing {@link Qualifier}
 * instances.
 *
 * <p>This is a <a
 * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/doc-files/ValueBased.html">value-based</a>
 * class.</p>
 *
 * @param <K> the type of a {@link Qualifier}'s {@linkplain
 * Qualifier#attributes() attribute keys}
 *
 * @param <V> the type of a {@link Qualifier}'s {@linkplain
 * Qualifier#attributes() attribute values}
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see Bindings
 */
public final class Qualifiers<K extends Comparable<? super K>, V> extends Bindings<K, V, Qualifier<K, V>> {


  /*
   * Static fields.
   */


  private static final Qualifiers<?, ?> EMPTY = new Qualifiers<>(List.of());


  /*
   * Constructors.
   */


  private Qualifiers(final Iterable<? extends Qualifier<K, V>> qualifiers) {
    super(qualifiers);
  }


  /*
   * Instance methods.
   */


  /**
   * Returns a <strong>usually new</strong> {@link Qualifiers} with
   * this {@link Qualifiers}' entries and an additional entry
   * consisting of the supplied {@link Qualifier}.
   *
   * <p>The returned {@link Qualifiers} <strong>will be new</strong>
   * unless {@code qualifier} is {@code null}, in which case {@code
   * this} will be returned.</p>
   *
   * @param qualifier a {@link Qualifier}; may be {@code null} in
   * which case {@code this} will be returned
   *
   * @return a {@link Qualifiers} with this {@link Qualifiers}'
   * entries and an additional entry consisting of the supplied {@link
   * Qualifier}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   *
   * @see #plus(Iterable)
   */
  public final Qualifiers<K, V> plus(final Qualifier<K, V> qualifier) {
    if (qualifier == null) {
      return this;
    } else if (this.isEmpty()) {
      return of(qualifier);
    } else {
      return this.plus(List.of(qualifier));
    }
  }

  /**
   * Returns a <strong>usually new</strong> {@link Qualifiers} with
   * this {@link Qualifiers}' entries and additional entries
   * represented by the supplied {@code qualifiers}.
   *
   * <p>The returned {@link Qualifiers} <strong>will be new</strong>
   * unless {@code qualifier} is {@code null}, in which case {@code
   * this} will be returned.</p>
   *
   * @param qualifiers additional {@link Qualifier}s; may be {@code
   * null} in which case {@code this} will be returned
   *
   * @return a {@link Qualifiers} with this {@link Qualifiers}'
   * entries and additional entries represented by the supplied {@code
   * qualifiers}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final Qualifiers<K, V> plus(final Iterable<? extends Qualifier<K, V>> qualifiers) {
    if (qualifiers == null) {
      return this;
    } else if (this.isEmpty()) {
      return of(qualifiers);
    }
    final Collection<Qualifier<K, V>> newQualifiers = new TreeSet<>();
    for (final Qualifier<K, V> q : this) {
      newQualifiers.add(q);
    }
    for (final Qualifier<K, V> q : qualifiers) {
      newQualifiers.add(q);
    }
    return of(newQualifiers);
  }

  /**
   * Returns a <strong>usually new</strong> {@link Qualifiers} whose
   * {@link Qualifier}s' {@linkplain Qualifier#attributes() attribute
   * keys} are prefixed with the supplied {@code prefix}.
   *
   * <p>If this {@link Qualifiers} is {@linkplain #isEmpty() empty},
   * then {@code this} is returned.</p>
   *
   * @param prefix a prefix; if {@code null} then {@code this} will be returned
   *
   * @return a <strong>usually new</strong> {@link Qualifiers} whose
   * {@link Qualifier}s' {@linkplain Qualifier#attributes() attribute
   * keys} are prefixed with the supplied {@code prefix}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic,
   * assuming the supplied {@link Function} is.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads
   */
  public final Qualifiers<K, V> withPrefix(final String prefix) {
    if (prefix == null || this.isEmpty()) {
      return this;
    }
    return this.withPrefix(q -> prefix + q.name());
  }

  /**
   * Returns a <strong>usually new</strong> {@link Qualifiers} whose
   * {@link Qualifier}s' {@linkplain Qualifier#attributes() attribute
   * keys} are produced by the supplied {@link Function}, which is
   * expected to prepend a prefix to the original key and return the
   * result.
   *
   * <p>If this {@link Qualifiers} is {@linkplain #isEmpty() empty},
   * then {@code this} is returned.</p>
   *
   * @param f a deterministic, idempotent {@link Function} that
   * accepts keys drawn from this {@link Qualifiers}' {@link
   * Qualifier}s' {@linkplain Qualifier#attributes() attribute keys}
   * and returns a non-{@code null} prefixed version of that key; may
   * be {@code null} in which case {@code this} will be returned
   *
   * @return a <strong>usually new</strong> {@link Qualifiers} whose
   * {@link Qualifier}s' {@linkplain Qualifier#attributes() attribute
   * keys} have been prefixed by the actions of the supplied {@link
   * Function}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic,
   * assuming the supplied {@link Function} is.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads, assuming the supplied {@link Function} is
   */
  public final Qualifiers<K, V> withPrefix(final Function<? super Qualifier<K, V>, ? extends String> f) {
    if (f == null || this.isEmpty()) {
      return this;
    }
    final Collection<Qualifier<K, V>> newQualifiers = new TreeSet<>();
    for (final Qualifier<K, V> q : this) {
      newQualifiers.add(Qualifier.of(f.apply(q),
                                     q.value(),
                                     q.attributes(),
                                     q.info()));
    }
    return of(newQualifiers);
  }

  /**
   * Returns a {@link MethodHandleDesc} describing the constructor or
   * {@code static} method that will be used to create a <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">dynamic
   * constant</a> representing this {@link Qualifiers}.
   *
   * @return a {@link MethodHandleDesc} describing the constructor or
   * {@code static} method that will be used to create a <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">dynamic
   * constant</a> representing this {@link Qualifiers}
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
  @Override // Bindings<K, V, Qualifier<K, V>>
  protected final MethodHandleDesc describeConstructor() {
    return
      MethodHandleDesc.ofMethod(STATIC,
                                CD_Qualifiers,
                                "of",
                                MethodTypeDesc.of(CD_Qualifiers, CD_Iterable));
  }


  /*
   * Static methods.
   */


  /**
   * Returns a {@link Qualifiers}, which may or may not be newly
   * created, whose {@link #isEmpty() isEmpty()} method will return
   * {@code true}.
   *
   * @param <K> the type of the {@link Qualifier}'s {@linkplain
   * Qualifier#attributes() attribute keys}
   *
   * @param <V> the type of the {@link Qualifier}'s {@linkplain
   * Qualifier#attributes() attribute values}
   *
   * @return a {@link Qualifiers}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  @SuppressWarnings("unchecked")
  public static final <K extends Comparable<? super K>, V> Qualifiers<K, V> of() {
    return (Qualifiers<K, V>)EMPTY;
  }

  /**
   * Returns a {@link Qualifiers}, which may or may not be newly
   * created, representing the supplied arguments.
   *
   * @param <K> the type of the {@link Qualifier}'s {@linkplain
   * Qualifier#attributes() attribute keys}
   *
   * @param <V> the type of the {@link Qualifier}'s {@linkplain
   * Qualifier#attributes() attribute values}
   *
   * @param qualifier the sole {@link Qualifier} the {@link
   * Qualifiers} will contain; must not be {@code null}
   *
   * @return a {@link Qualifiers}
   *
   * @exception NullPointerException if {@code qualifier} is {@code
   * null}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public static final <K extends Comparable<? super K>, V> Qualifiers<K, V> of(final Qualifier<K, V> qualifier) {
    return of(List.of(Objects.requireNonNull(qualifier, "qualifier")));
  }

  /**
   * Returns a {@link Qualifiers}, which may or may not be newly
   * created, representing the supplied arguments.
   *
   * @param <K> the type of the {@link Qualifier}'s {@linkplain
   * Qualifier#attributes() attribute keys}
   *
   * @param <V> the type of the {@link Qualifier}'s {@linkplain
   * Qualifier#attributes() attribute values}
   *
   * @param qualifiers an {@link Iterable} representing {@link
   * Qualifier} instances the {@link Qualifiers} will contain; may be
   * {@code null}
   *
   * @return a {@link Qualifiers}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public static final <K extends Comparable<? super K>, V> Qualifiers<K, V> of(final Iterable<? extends Qualifier<K, V>> qualifiers) {
    if (qualifiers == null) {
      return of();
    }
    final Iterator<? extends Qualifier<K, V>> i = qualifiers.iterator();
    if (i.hasNext()) {
      final Collection<Qualifier<K, V>> newQualifiers = new TreeSet<>();
      newQualifiers.add(i.next());
      while (i.hasNext()) {
        newQualifiers.add(i.next());
      }
      return new Qualifiers<>(newQualifiers);
    }
    return of();
  }

}
