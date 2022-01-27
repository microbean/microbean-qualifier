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
import static java.lang.constant.ConstantDescs.NULL;
import static java.lang.constant.DirectMethodHandleDesc.Kind.STATIC;

import static org.microbean.qualifier.ConstantDescs.CD_Qualified;
import static org.microbean.qualifier.ConstantDescs.CD_Qualifiers;

@Experimental
public record Qualified<T>(Qualifiers<?> qualifiers, T qualified) implements Constable {


  /*
   * Canonical constructor.
   */

  
  public Qualified {
    qualifiers = Objects.requireNonNull(qualifiers, "qualifiers");
  }


  /*
   * Instance methods.
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
          Optional.of(DynamicConstantDesc.of(BSM_INVOKE,
                                             MethodHandleDesc.ofMethod(STATIC,
                                                                       CD_Qualified,
                                                                       "of",
                                                                       MethodTypeDesc.of(CD_Qualified,
                                                                                         CD_Qualifiers,
                                                                                         CD_Object))));
      }
    }
    return Optional.empty();
  }


  /*
   * Static methods.
   */

  
  public static final <T> Qualified<T> of(final Qualifiers<?> qualifiers, final T qualified) {
    return new Qualified<>(qualifiers, qualified);
  }

}
