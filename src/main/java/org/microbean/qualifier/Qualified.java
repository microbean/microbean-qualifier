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
import java.lang.constant.ConstantDesc;
import java.lang.constant.DynamicConstantDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;

import java.util.Optional;

import static java.lang.constant.ConstantDescs.BSM_INVOKE;
import static java.lang.constant.ConstantDescs.CD_Object;
import static java.lang.constant.ConstantDescs.DEFAULT_NAME;
import static java.lang.constant.ConstantDescs.NULL;

import static java.lang.constant.DirectMethodHandleDesc.Kind.INTERFACE_STATIC;

import static org.microbean.qualifier.ConstantDescs.CD_Qualified;
import static org.microbean.qualifier.ConstantDescs.CD_Qualifiers;

/**
 * A {@link Constable} pairing of a {@link Qualifiers} and an {@link
 * Object} that is qualified by them.
 *
 * @param <V> the type borne by the values of the {@link Qualifiers}
 * in this {@link Qualified}
 *
 * @param <T> the type of the object that is qualified; note that if it
 * does not extend {@link Constable} then a {@link Qualified} bearing
 * it will return an {@linkplain Optional#empty() empty
 * <code>Optional</code>} from its {@link #describeConstable()} method
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see Qualifiers
 *
 * @see Constable
 */
public interface Qualified<V, T> extends Constable {


  /*
   * Instance methods.
   */


  /**
   * Returns this {@link Qualified}'s {@link Qualifiers}.
   *
   * @return this {@link Qualified}'s {@link Qualifiers}
   *
   * @nullability Implementations of this method must not return
   * {@code null}.
   *
   * @idempotency Implementations of this method must be idempotent
   * and deterministic.
   *
   * @threadsafety Implementations of this method must be safe for
   * concurrent use by multiple threads.
   */
  public Qualifiers<V> qualifiers();

  /**
   * Returns this {@link Qualified}'s qualified object.
   *
   * @return this {@link Qualified}'s qualified object, which may be
   * {@code null}
   *
   * @nullability Implementations of this method may return {@code null}.
   *
   * @idempotency Implementations of this method must be idempotent
   * and deterministic.
   *
   * @threadsafety Implementations of this method must be safe for
   * concurrent use by multiple threads.
   */
  public T qualified();

  /**
   * Returns an {@link Optional} housing a {@link ConstantDesc}
   * describing this {@link Qualified}, if this {@link Qualified} is
   * capable of being represented as a <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">dynamic
   * constant</a>, or an {@linkplain Optional#isEmpty() empty} {@link
   * Optional} if not.
   *
   * @return an {@link Optional} housing a {@link ConstantDesc}
   * describing this {@link Qualified}, if this {@link Qualified} is
   * capable of being represented as a <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">dynamic
   * constant</a>, or an {@linkplain Optional#isEmpty() empty} {@link
   * Optional} if not
   *
   * @nullability This method does not, and its overrides must not,
   * return {@code null}.
   *
   * @idempotency This method is, and its overrides must be,
   * idempotent and deterministic.
   *
   * @threadsafety This method is, and its overrides must be, safe for
   * concurrent use by multiple threads.
   *
   * @see <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">Dynamically-computed
   * constants</a>
   */
  @Override // Constable
  public default Optional<? extends ConstantDesc> describeConstable() {
    final T qualified = this.qualified();
    final ConstantDesc qualifiedCd;
    if (qualified == null) {
      qualifiedCd = NULL;
    } else if (qualified instanceof Constable c) {
      qualifiedCd = c.describeConstable().orElse(null);
    } else if (qualified instanceof ConstantDesc cd) {
      qualifiedCd = cd;
    } else {
      return Optional.empty();
    }
    final ConstantDesc qualifiersCd = this.qualifiers().describeConstable().orElse(null);
    if (qualifiersCd == null) {
      return Optional.empty();
    }
    // Call Qualified.of(qualifiers, qualified) to rehydrate.
    return
      Optional.of(DynamicConstantDesc.ofNamed(BSM_INVOKE,
                                              DEFAULT_NAME,
                                              CD_Qualified,
                                              MethodHandleDesc.ofMethod(INTERFACE_STATIC,
                                                                        CD_Qualified,
                                                                        "of",
                                                                        MethodTypeDesc.of(CD_Qualified,
                                                                                          CD_Qualifiers,
                                                                                          CD_Object)),
                                              qualifiersCd,
                                              qualifiedCd));
  }


  /*
   * Static methods.
   */


  /**
   * Returns a {@link Qualified}, which may or may not be newly
   * created, representing the supplied qualified object.
   *
   * @param <V> the type of the {@link Qualified}'s {@link
   * Qualifiers}' {@linkplain Binding#value() value} and {@linkplain
   * Binding#attributes() attribute values}
   *
   * @param <T> the type of the qualified object
   *
   * @param qualified the qualified object; may be {@code null}
   *
   * @return a {@link Qualified}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is neither idempotent nor deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   *
   * @see #of(Qualifiers, Object)
   */
  public static <V, T> Qualified<V, T> of(final T qualified) {
    return Record.of(qualified);
  }

  /**
   * Returns a {@link Qualified}, which may or may not be newly
   * created, representing the supplied {@link Qualifiers} and
   * qualified object.
   *
   * @param <V> the type of the {@link Qualified}'s {@link
   * Qualifiers}' {@linkplain Binding#value() value} and {@linkplain
   * Binding#attributes() attribute values}
   *
   * @param <T> the type of the qualified object
   *
   * @param qualifiers the {@link Qualifiers}; may be {@code null}
   *
   * @param qualified the qualified object; may be {@code null}
   *
   * @return a {@link Qualified}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is neither idempotent nor deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  // Called by #describeConstable().
  public static <V, T> Qualified<V, T> of(final Qualifiers<V> qualifiers, final T qualified) {
    return Record.of(qualifiers, qualified);
  }


  /*
   * Inner and nested classes.
   */


  /**
   * A {@link Qualified} {@link java.lang.Record}.
   *
   * @param <V> the type borne by the values of the {@link Qualifiers}
   * in this {@link Qualified.Record}
   *
   * @param <T> the type of the object that is qualified; note that if
   * it does not extend {@link Constable} then a {@link Record}
   * bearing it will return an {@linkplain Optional#empty() empty
   * <code>Optional</code>} from its {@link #describeConstable()}
   * method
   *
   * @param qualifiers the {@link Qualifiers}; may be {@code null} in
   * which case the return value of {@link Qualifiers#of()} will be
   * used instead
   *
   * @param qualified the object being qualified; may be {@code null}
   *
   * @author <a href="https://about.me/lairdnelson"
   * target="_parent">Laird Nelson</a>
   */
  public static final record Record<V, T>(Qualifiers<V> qualifiers, T qualified)
    implements Qualified<V, T>, Constable {


    /*
     * Constructors.
     */


    /**
     * Creates a new {@link Record}.
     *
     * @param qualified the object being qualified; may be {@code
     * null}
     *
     * @see #Record(Qualifiers, Object)
     */
    public Record(final T qualified) {
      this(Qualifiers.of(), qualified);
    }

    /**
     * Creates a new {@link Record}.
     *
     * @param qualifiers the {@link Qualifiers}; may be {@code null}
     * in which case the return value of {@link Qualifiers#of()} will
     * be used instead
     *
     * @param qualified the object being qualified; may be {@code
     * null}
     */
    public Record {
      if (qualifiers == null) {
        qualifiers = Qualifiers.of();
      }
    }


    /*
     * Instance methods.
     */


    /**
     * Returns an {@link Optional} housing a {@link ConstantDesc}
     * describing this {@link Record}, if this {@link Record} is
     * capable of being represented as a <a
     * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">dynamic
     * constant</a>, or an {@linkplain Optional#isEmpty() empty}
     * {@link Optional} if not.
     *
     * @return an {@link Optional} housing a {@link ConstantDesc}
     * describing this {@link Record}, if this {@link Record} is
     * capable of being represented as a <a
     * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">dynamic
     * constant</a>, or an {@linkplain Optional#isEmpty() empty}
     * {@link Optional} if not
     *
     * @nullability This method does not, and its overrides must not,
     * return {@code null}.
     *
     * @idempotency This method is, and its overrides must be,
     * idempotent and deterministic.
     *
     * @threadsafety This method is, and its overrides must be, safe
     * for concurrent use by multiple threads.
     *
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">Dynamically-computed
     * constants</a>
     */
    @Override // Constable
    public final Optional<? extends ConstantDesc> describeConstable() {
      return Qualified.super.describeConstable();
    }


    /*
     * Static methods.
     */


    /**
     * Returns a {@link Record}, which may or may not be newly
     * created, representing the qualified object.
     *
     * @param <V> the type of the {@link Record}'s {@link Qualifiers}'
     * {@linkplain Binding#value() value} and {@linkplain
     * Binding#attributes() attribute values}
     *
     * @param <T> the type of the qualified object
     *
     * @param qualified the qualified object; may be {@code null}
     *
     * @return a {@link Record}
     *
     * @nullability This method never returns {@code null}.
     *
     * @idempotency This method is neither idempotent nor deterministic.
     *
     * @threadsafety This method is safe for concurrent use by multiple
     * threads.
     *
     * @see #of(Qualifiers, Object)
     */
    public static final <V, T> Record<V, T> of(final T qualified) {
      return of(Qualifiers.<V>of(), qualified);
    }

    /**
     * Returns a {@link Record}, which may or may not be newly
     * created, representing the supplied {@link Qualifiers} and
     * qualified object.
     *
     * @param <V> the type of the {@link Record}'s {@link Qualifiers}'
     * {@linkplain Binding#value() value} and {@linkplain
     * Binding#attributes() attribute values}
     *
     * @param <T> the type of the qualified object
     *
     * @param qualifiers the {@link Qualifiers}; may be {@code null}
     *
     * @param qualified the qualified object; may be {@code null}
     *
     * @return a {@link Record}
     *
     * @nullability This method never returns {@code null}.
     *
     * @idempotency This method is neither idempotent nor deterministic.
     *
     * @threadsafety This method is safe for concurrent use by multiple
     * threads.
     */
    public static final <V, T> Record<V, T> of(final Qualifiers<V> qualifiers, final T qualified) {
      return new Record<>(qualifiers, qualified);
    }


  }

}
