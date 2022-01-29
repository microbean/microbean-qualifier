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

/**
 * A utiliy class containing useful {@link
 * java.lang.constant.ConstantDesc}s (mostly {@link ClassDesc}s).
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 */
public final class ConstantDescs {


  /*
   * Static fields.
   */


  /**
   * A {@link ClassDesc} describing {@link
   * java.lang.constant.Constable java.lang.constant.Constable}.
   *
   * @nullability This field is never {@code null}.
   */
  public static final ClassDesc CD_Constable = ClassDesc.of("java.lang.constant.Constable");

  /**
   * A {@link ClassDesc} describing {@link Qualified
   * org.microbean.qualifier.Qualified}.
   *
   * @nullability This field is never {@code null}.
   */
  public static final ClassDesc CD_Qualified = ClassDesc.of(Qualified.class.getName());

  /**
   * A {@link ClassDesc} describing {@link Qualifiers org.microbean.qualifier.Qualifiers}.
   *
   * @nullability This field is never {@code null}.
   */
  public static final ClassDesc CD_Qualifiers = ClassDesc.of(Qualifiers.class.getName());


  /*
   * Constructors.
   */


  private ConstantDescs() {
    super();
  }

}
