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

public final class ConstantDescs {

  public static final ClassDesc CD_Constable = ClassDesc.of("java.lang.constant.Constable");
  
  public static final ClassDesc CD_Qualified = ClassDesc.of(Qualified.class.getName());

  public static final ClassDesc CD_Qualifiers = ClassDesc.of(Qualifiers.class.getName());

  private ConstantDescs() {
    super();
  }

}
