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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import java.util.function.Function;
import java.util.function.Predicate;

import static java.lang.constant.ConstantDescs.BSM_INVOKE;
import static java.lang.constant.ConstantDescs.CD_Object;
import static java.lang.constant.ConstantDescs.DEFAULT_NAME;
import static java.lang.constant.ConstantDescs.NULL;
import static java.lang.constant.DirectMethodHandleDesc.Kind.STATIC;

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
 * @see #of(Comparable, Object)
 */
public final class Qualifiers<K extends Comparable<? super K>, V> extends Bindings<K, V> {


  /*
   * Static fields.
   */


  private static final Qualifiers<?, ?> EMPTY_QUALIFIERS = new Qualifiers<>();


  /*
   * Constructors.
   */


  /**
   * Creates a new {@linkplain Map#isEmpty() empty} {@link
   * Qualifiers}.
   */
  public Qualifiers() {
    super(Map.of(), Map.of(), false);
  }

  /**
   * Creates a new {@link Qualifiers}.
   *
   * @param qualifiers a {@link Map} representing the qualifiers; may be
   * {@code null} in which case an {@linkplain Map#isEmpty() empty
   * <code>Map</code>} will be used instead
   */
  public Qualifiers(final Map<? extends K, ? extends V> qualifiers) {
    super(qualifiers, Map.of(), true);
  }

  /**
   * Creates a new {@link Qualifiers}.
   *
   * @param qualifiers a {@link Map} representing the qualifiers; may be
   * {@code null} in which case an {@linkplain Map#isEmpty() empty
   * <code>Map</code>} will be used instead
   *
   * @param info informational key-value pairs; may be {@code
   * null} in which case an {@linkplain Map#isEmpty() empty
   * <code>Map</code>} will be used instead
   */
  public Qualifiers(final Map<? extends K, ? extends V> qualifiers,
                    final Map<? extends K, ? extends V> info) {
    super(qualifiers, info, true);
  }

  private Qualifiers(final Map<? extends K, ? extends V> qualifiers,
                     final Map<? extends K, ? extends V> info,
                     final boolean copy) {
    super(qualifiers, info, copy);
  }


  /*
   * Instance methods.
   */


  /**
   * Returns an {@link Optional} containing a {@link ConstantDesc}
   * representing this {@link Qualifiers}, or an {@linkplain
   * Optional#isEmpty() empty} {@link Optional} if any of this {@link
   * Qualifiers}' keys or values is neither a {@link Constable} nor a
   * {@link ConstantDesc}.
   *
   * <p><strong>Note:</strong> Only entries that are binding are
   * described.</p>
   *
   * @return an {@link Optional} containing a {@link ConstantDesc}
   * representing this {@link Qualifiers}; never {@code null}
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
      // Rehydrate via Qualifiers.of()
      return
        Optional.of(DynamicConstantDesc.ofNamed(BSM_INVOKE,
                                                DEFAULT_NAME,
                                                CD_Qualifiers,
                                                MethodHandleDesc.ofMethod(STATIC,
                                                                          CD_Qualifiers,
                                                                          "of",
                                                                          MethodTypeDesc.of(CD_Qualifiers))));
    }
    final int bsmInvokeArgumentsLength = 2 * entrySet.size() + 1;
    final ConstantDesc[] bsmInvokeArguments = new ConstantDesc[bsmInvokeArgumentsLength];
    // Rehydrate via Qualifiers.of(Object...)
    bsmInvokeArguments[0] =
      MethodHandleDesc.ofMethod(STATIC,
                                CD_Qualifiers,
                                "of",
                                MethodTypeDesc.of(CD_Qualifiers,
                                                  CD_Object.arrayType()));
    int i = 1;
    for (final Entry<K, V> entry : entrySet) {
      final ConstantDesc k;
      final Object key = entry.getKey();
      if (key instanceof Constable ck) {
        k = ck.describeConstable().orElse(null);
      } else if (key instanceof ConstantDesc cd) {
        k = cd;
      } else {
        return Optional.empty();
      }
      bsmInvokeArguments[i++] = k;
      final ConstantDesc v;
      final Object value = entry.getValue();
      if (value == null){
        v = NULL;
      } else if (value instanceof Constable cv) {
        v = cv.describeConstable().orElse(null);
      } else if (value instanceof ConstantDesc cd) {
        v = cd;
      } else {
        return Optional.empty();
      }
      bsmInvokeArguments[i++] = v;
    }
    return Optional.of(DynamicConstantDesc.ofNamed(BSM_INVOKE,
                                                   DEFAULT_NAME,
                                                   CD_Qualifiers,
                                                   bsmInvokeArguments));
  }

  /**
   * Returns a <strong>usually new</strong> {@link Qualifiers} whose
   * keys are produced by the supplied {@link Function}, which is
   * expected to prepend a prefix to the original key and return the
   * result.
   *
   * <p>If this {@link Qualifiers} is {@linkplain #isEmpty() empty},
   * then {@code this} is returned.</p>
   *
   * @param f a deterministic, idempotent {@link Function} that
   * accepts keys drawn from this {@link Qualifiers}' {@linkplain
   * Map#entrySet() entry set} and returns a non-{@code null} prefixed
   * version of that key; may be {@code null} in which case {@code
   * this} will be returned
   *
   * @return a <strong>usually new</strong> {@link Qualifiers} whose
   * keys have been prefixed by the actions of the supplied {@link
   * Function}
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
  public final Qualifiers<K, V> withPrefix(final Function<? super K, ? extends K> f) {
    if (f == null || this.isEmpty()) {
      return this;
    } else {
      final Map<K, V> bindings = new TreeMap<>();
      for (final Entry<? extends K, ? extends V> entry : this.toMap().entrySet()) {
        bindings.put(f.apply(entry.getKey()), entry.getValue());
      }
      final Map<K, V> info;
      final Map<K, V> myInfo = this.info();
      if (myInfo.isEmpty()) {
        info = Map.of();
      } else {
        info = new TreeMap<>();
        for (final Entry<? extends K, ? extends V> entry : myInfo.entrySet()) {
          info.put(f.apply(entry.getKey()), entry.getValue());
        }
      }
      return new Qualifiers<K, V>(bindings, info, false);
    }
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
   *
   * @see #plus(Map)
   */
  public final Qualifiers<K, V> plus(final Qualifiers<? extends K, ? extends V> qualifiers) {
    if (qualifiers == null || qualifiers.isEmpty()) {
      return this;
    } else {
      return this.plus(qualifiers.toMap(), qualifiers.info());
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
    return this.plus(qualifiers, Map.of());
  }

  /**
   * Returns a {@link Qualifiers} with additional
   * entries sourced from the supplied {@link Map}s.
   *
   * <p>The returned {@link Qualifiers} <strong>will be new</strong>
   * unless both {@link Map}s are {@code null} or {@linkplain
   * Map#isEmpty() empty}, in which case {@code this} will be
   * returned.</p>
   *
   * @param qualifiers a {@link Map} whose entries will be part of the
   * new {@link Qualifiers}, displacing any that already exist; may be
   * {@code null} or {@linkplain Map#isEmpty() empty} in which case an
   * {@linkplain Map#isEmpty() empty <code>Map</code>} will be used
   * instead; all keys and values must be immutable (not just
   * unmodifiable) or undefined behavior will result
   *
   * @param info informational key-value pairs; may be {@code
   * null} in which case an {@linkplain Map#isEmpty() empty
   * <code>Map</code>} will be used instead
   *
   * @return a {@link Qualifiers} with additional entries sourced from
   * the supplied {@link Map}s; never {@code null}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public final Qualifiers<K, V> plus(final Map<? extends K, ? extends V> qualifiers,
                                     final Map<? extends K, ? extends V> info) {
    if (qualifiers == null || qualifiers.isEmpty()) {
      if (info == null || info.isEmpty()) {
        return this;
      } else {
        final Map<K, V> newInfo = new TreeMap<>(this.info());
        newInfo.putAll(info);
        return new Qualifiers<>(this.toMap(), newInfo, false);
      }      
    } else if (info == null || info.isEmpty()) {
      final Map<K, V> newQualifiers = new TreeMap<>(this.toMap());
      newQualifiers.putAll(qualifiers);
      return new Qualifiers<>(newQualifiers, this.info(), false);
    } else {
      final Map<K, V> newQualifiers = new TreeMap<>(this.toMap());
      newQualifiers.putAll(qualifiers);
      final Map<K, V> newInfo = new TreeMap<>(this.info());
      newInfo.putAll(info);
      return new Qualifiers<>(newQualifiers, newInfo, false);
    }
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
  public static final <K extends Comparable<? super K>, V> Qualifiers<K, V> of() {
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
   * @param bindings the {@link Map} of qualifier names and values;
   * may be {@code null} in which case the return value of the {@link
   * #of()} method will be returned
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
   * @see #of(Map, Map)
   */
  public static final <K extends Comparable<? super K>, V> Qualifiers<K, V> of(final Map<? extends K, ? extends V> bindings) {
    return of(bindings, Map.of());
  }

  /**
   * Returns a {@link Qualifiers} equal to one consisting of the
   * entries represented by the supplied {@link Map}s.
   *
   * @param <K> the type borne by the keys of the qualifiers in the
   * returned {@link Qualifiers}
   *
   * @param <V> the type borne by the values of the qualifiers in the
   * returned {@link Qualifiers}
   *
   * @param bindings the {@link Map} of qualifier names and values;
   * may be {@code null} in which case the return value of the {@link
   * #of()} method will be returned
   *
   * @param info informational key-value pairs; may be {@code null} in
   * which case an {@linkplain Map#isEmpty() empty <code>Map</code>}
   * will be used instead
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
   * @see #of(Comparable, Object)
   */
  public static final <K extends Comparable<? super K>, V> Qualifiers<K, V> of(final Map<? extends K, ? extends V> bindings,
                                                                               final Map<? extends K, ? extends V> info) {
    if (bindings == null || bindings.isEmpty()) {
      if (info == null || info.isEmpty()) {
        return of();
      } else {
        return new Qualifiers<>(Map.of(), info, true);
      }
    } else if (info == null || info.isEmpty()) {
      return new Qualifiers<>(bindings, Map.of(), true);
    } else {
      return new Qualifiers<>(bindings, info, true);
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
  public static final <K extends Comparable<? super K>, V> Qualifiers<K, V> of(final K name0, final V value0) {
    return new Qualifiers<>(Map.of(name0, value0), Map.of(), false);
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
  public static final <K extends Comparable<? super K>, V> Qualifiers<K, V> of(final K name0, final V value0,
                                                                               final K name1, final V value1) {
    return new Qualifiers<>(Map.of(name0, value0, name1, value1), Map.of(), false);
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
  public static final <K extends Comparable<? super K>, V> Qualifiers<K, V> of(final K name0, final V value0,
                                                                               final K name1, final V value1,
                                                                               final K name2, final V value2) {
    return new Qualifiers<>(Map.of(name0, value0, name1, value1, name2, value2), Map.of(), false);
  }

  /**
   * Returns a {@link Qualifiers} consisting of the entries
   * represented by the supplied alternating name-value pairs.
   *
   * <p>The supplied {@code nameValuePairs} must be an even-numbered
   * array of non-{@code null} {@link Object} instances, where the
   * zero-based even-numbered elements represent qualifier keys, and
   * the zero-based odd-numbered elements represent qualifier
   * values.</p>
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
  public static final <K extends Comparable<? super K>, V> Qualifiers<K, V> of(final Object... nameValuePairs) {
    if (nameValuePairs == null || nameValuePairs.length <= 0) {
      return of();
    } else if (nameValuePairs.length % 2 != 0) {
      throw new IllegalArgumentException("nameValuePairs: " + Arrays.toString(nameValuePairs));
    } else {
      final Map<K, V> map = new TreeMap<>();
      for (int i = 0; i < nameValuePairs.length; i++) {
        map.put((K)nameValuePairs[i++], (V)nameValuePairs[i]);
      }
      return new Qualifiers<>(map, Map.of(), false);
    }
  }

}
