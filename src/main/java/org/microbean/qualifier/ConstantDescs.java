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
 * A utility class containing useful {@link
 * java.lang.constant.ConstantDesc}s.
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 */
public final class ConstantDescs {


  /*
   * Static fields.
   */


  public static final ClassDesc CD_Attribute = ClassDesc.of("org.microbean.qualifier.Attribute");
  
  /**
   * A {@link ClassDesc} describing {@link Binding
   * org.microbean.qualifier.Binding}.
   *
   * @nullability This field is never {@code null}.
   */
  public static final ClassDesc CD_Binding = ClassDesc.of("org.microbean.qualifier.Binding");

  /**
   * A {@link ClassDesc} describing {@link Bindings
   * org.microbean.qualifier.Bindings}.
   *
   * @nullability This field is never {@code null}.
   */
  public static final ClassDesc CD_Bindings = ClassDesc.of("org.microbean.qualifier.Bindings");

  static final ClassDesc CD_Comparable = ClassDesc.of("java.lang.Comparable");

  static final ClassDesc CD_Iterable = ClassDesc.of("java.lang.Iterable");

  public static final ClassDesc CD_NamedAttributeSet = ClassDesc.of("org.microbean.qualifier.NamedAttributeSet");
  
  /**
   * A {@link ClassDesc} describing {@link Qualified
   * org.microbean.qualifier.Qualified}.
   *
   * @nullability This field is never {@code null}.
   */
  public static final ClassDesc CD_Qualified = ClassDesc.of("org.microbean.qualifier.Qualified");

  /**
   * A {@link ClassDesc} describing {@link Qualified.Record
   * org.microbean.qualifier.Qualified.Record}.
   *
   * @nullability This field is never {@code null}.
   */
  public static final ClassDesc CD_QualifiedRecord = ClassDesc.of("org.microbean.qualifier.Qualified$Record");

  /**
   * A {@link ClassDesc} describing {@link Qualifier
   * org.microbean.qualifier.Qualifier}.
   *
   * @nullability This field is never {@code null}.
   */
  public static final ClassDesc CD_Qualifier = ClassDesc.of("org.microbean.qualifier.Qualifier");

  /**
   * A {@link ClassDesc} describing {@link Qualifiers
   * org.microbean.qualifier.Qualifiers}.
   *
   * @nullability This field is never {@code null}.
   */
  public static final ClassDesc CD_Qualifiers = ClassDesc.of("org.microbean.qualifier.Qualifiers");


  /*
   * Constructors.
   */


  private ConstantDescs() {
    super();
  }


}
