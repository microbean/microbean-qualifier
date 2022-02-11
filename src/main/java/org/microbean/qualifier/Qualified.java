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

import java.util.Objects;
import java.util.Optional;

import org.microbean.development.annotation.Experimental;
import org.microbean.development.annotation.OverridingEncouraged;

import static java.lang.constant.ConstantDescs.BSM_INVOKE;
import static java.lang.constant.ConstantDescs.CD_Object;
import static java.lang.constant.ConstantDescs.DEFAULT_NAME;
import static java.lang.constant.ConstantDescs.NULL;
import static java.lang.constant.DirectMethodHandleDesc.Kind.STATIC;

import static org.microbean.qualifier.ConstantDescs.CD_QualifiedRecord;
import static org.microbean.qualifier.ConstantDescs.CD_Qualifiers;

/**
 * A {@link Constable} pairing of a {@link Qualifiers} and an {@link
 * Object} that is qualified by them.
 *
 * @param <K> the type borne by the keys of the {@link Qualifiers} in
 * this {@link Qualified}
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
@Experimental
public interface Qualified<K extends Constable & Comparable<K>, V extends Constable, T> extends Constable {


  /*
   * Instance methods.
   */


  /**
   * Returns the {@link Qualifiers} that qualifies this {@link
   * Qualified}.
   *
   * @return the {@link Qualifiers} that qualifies this {@link
   * Qualified}; never {@code null}
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
  public Qualifiers<K, V> qualifiers();

  /**
   * Returns the qualified thing this {@link Qualified} represents.
   *
   * @return the qualified thing this {@link Qualified} represents;
   * possibly, but not normally, {@code null}
   *
   * @nullability Implementations of this method may return {@code
   * null}.
   *
   * @idempotency Implementations of this method must be idempotent
   * and deterministic.
   *
   * @threadsafety Implementations of this method must be safe for
   * concurrent use by multiple threads.
   */
  public T qualified();

  /**
   * Returns an {@link Optional} containing the nominal descriptor for
   * this instance, if one can be constructed, <strong>or an
   * {@linkplain Optional#isEmpty() empty} {@link Optional} if one
   * cannot be constructed</strong>.
   *
   * <p>The default implementation of this method returns an {@link
   * Optional Optional&lt;? extends ConstantDesc&gt;} that is computed
   * from only the return value of the {@link #qualifiers()} method
   * and the return value of the {@link #qualified()} method.  This
   * may or may not be sufficient for any given subclass'
   * semantics.</p>
   *
   * @return an {@link Optional} containing the nominal descriptor for
   * this instance, if one can be constructed, <strong>or an
   * {@linkplain Optional#isEmpty() empty} {@link Optional} if one
   * cannot be constructed</strong>; never {@code null}
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
  @Override // Constable
  @OverridingEncouraged
  public default Optional<? extends ConstantDesc> describeConstable() {
    final ConstantDesc qualifiersDesc = this.qualifiers().describeConstable().orElse(null);
    if (qualifiersDesc != null) {
      final Object qualified = this.qualified();
      final ConstantDesc qualifiedDesc;
      if (qualified == null) {
        qualifiedDesc = NULL;
      } else if (qualified instanceof Constable cq) {
        qualifiedDesc = cq.describeConstable().orElse(null);
      } else if (qualified instanceof ConstantDesc cd) {
        qualifiedDesc = cd;
      } else {
        qualifiedDesc = null;
      }
      if (qualifiedDesc != null) {
        return
          Optional.of(DynamicConstantDesc.ofNamed(BSM_INVOKE,
                                                  DEFAULT_NAME,
                                                  CD_QualifiedRecord,
                                                  MethodHandleDesc.ofConstructor(CD_QualifiedRecord,
                                                                                 CD_Qualifiers,
                                                                                 CD_Object),
                                                  qualifiersDesc,
                                                  qualifiedDesc));
      }
    }
    return Optional.empty();
  }


  /*
   * Inner and nested classes.
   */


  /**
   * A {@link Qualified} {@link java.lang.Record}.
   *
   * @param <K> the type borne by the keys of the {@link Qualifiers} in
   * this {@link Qualified.Record}
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
  public static final record Record<K extends Constable & Comparable<K>, V extends Constable, T>(Qualifiers<K, V> qualifiers,
                                                                                                 T qualified)
    implements Qualified<K, V, T> {


    /*
     * Canonical constructor.
     */


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

  }

}
