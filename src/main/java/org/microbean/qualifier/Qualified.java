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

import static java.lang.constant.ConstantDescs.BSM_INVOKE;
import static java.lang.constant.ConstantDescs.CD_Object;
import static java.lang.constant.ConstantDescs.DEFAULT_NAME;
import static java.lang.constant.ConstantDescs.NULL;
import static java.lang.constant.DirectMethodHandleDesc.Kind.STATIC;

import static org.microbean.qualifier.ConstantDescs.CD_Qualified;
import static org.microbean.qualifier.ConstantDescs.CD_Qualifiers;

/**
 * A {@link Constable} pairing of a {@link Qualifiers} and a thing
 * that is qualified by them.
 *
 * @param <T> the type of the thing that is qualified
 *
 * @param qualifiers the {@link Qualifiers}; must not be {@code null}
 *
 * @param qualified the thing being qualified; may be {@code null}
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see Qualifiers
 */
@Experimental
public record Qualified<T>(Qualifiers<?, ?> qualifiers, T qualified) implements Constable {


  /*
   * Canonical constructor.
   */


  /**
   * Creates a new {@link Qualified}.
   *
   * @param qualifiers the {@link Qualifiers}; must not be {@code null}
   *
   * @param qualified the thing being qualified; may be {@code null}
   *
   * @exception NullPointerException if {@code qualifiers} is {@code
   * null}
   */
  public Qualified {
    qualifiers = Objects.requireNonNull(qualifiers, "qualifiers");
  }


  /*
   * Instance methods.
   */


  /**
   * Returns an {@link Optional} containing the nominal descriptor for
   * this instance, if one can be constructed, or an {@linkplain
   * Optional#isEmpty() empty} {@link Optional} if one cannot be
   * constructed.
   *
   * <p>This method will return an {@linkplain Optional#isEmpty()
   * empty} {@link Optional} if the return value of {@link
   * #qualified()} is not {@code null} and is not an instance of
   * {@link Constable}.</p>
   *
   * @return an {@link Optional} containing the nominal descriptor for
   * this instance, if one can be constructed, or an {@linkplain
   * Optional#isEmpty() empty} {@link Optional} if one cannot be
   * constructed; never {@code null}
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
    final Optional<? extends ConstantDesc> qualifiersDesc = this.qualifiers().describeConstable();
    if (qualifiersDesc.isPresent()) {
      final T qualified = this.qualified();
      final Optional<? extends ConstantDesc> qualifiedDesc;
      if (qualified == null) {
        qualifiedDesc = Optional.of(NULL);
      } else if (qualified instanceof Constable c) {
        qualifiedDesc = c.describeConstable();
      } else {
        qualifiedDesc = Optional.empty();
      }
      if (qualifiedDesc.isPresent()) {
        return
          Optional.of(DynamicConstantDesc.ofNamed(BSM_INVOKE,
                                                  DEFAULT_NAME,
                                                  CD_Qualified,
                                                  MethodHandleDesc.ofConstructor(CD_Qualified,
                                                                                 CD_Qualifiers,
                                                                                 CD_Object)));
      }
    }
    return Optional.empty();
  }

}
