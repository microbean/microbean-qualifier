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

import java.util.Map;

import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;

import static java.lang.constant.ConstantDescs.CD_Map;
import static java.lang.constant.ConstantDescs.CD_Object;
import static java.lang.constant.ConstantDescs.CD_String;

import static java.lang.constant.DirectMethodHandleDesc.Kind.STATIC;

import static org.microbean.qualifier.ConstantDescs.CD_Qualifier;

/**
 * A {@link Binding} used to qualify objects.
 *
 * <p>This is a <a
 * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/doc-files/ValueBased.html">value-based</a>
 * class.</p>
 *
 * @param <V> the type of a {@link Qualifier}'s {@linkplain #value()
 * value} and of its {@linkplain #attributes() attribute values}
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see Binding
 */
public final class Qualifier<V> extends Binding<V, Qualifier<V>> {


  /*
   * Constructors.
   */


  /**
   * Creates a new {@link Qualifier}.
   *
   * @param name the name; must not be {@code null}
   *
   * @param value the value; may be {@code null}
   *
   * @param attributes further describing this {@link Qualifier}; may
   * be {@code null}
   *
   * @param info informational attributes further describing this
   * {@link Qualifier} that are not considered by its {@link
   * #equals(Object)} implementation; may be {@code null}
   *
   * @see #name()
   *
   * @see #value()
   *
   * @see #attributes()
   *
   * @see #info()
   *
   * @see Binding#Binding(String, Object, Map, Map)
   */
  private Qualifier(final String name,
                    final V value,
                    final Map<? extends String, ?> attributes,
                    final Map<? extends String, ?> info) {
    super(name, value, attributes, info);
  }


  /*
   * Instance methods.
   */


  /**
   * Returns a {@link MethodHandleDesc} describing the constructor or
   * {@code static} method that will be used to create a <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">dynamic
   * constant</a> representing this {@link Qualifier}.
   *
   * <p>End users have no need to call this method.</p>
   *
   * @return a {@link MethodHandleDesc} describing the constructor or
   * {@code static} method that will be used to create a <a
   * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/package-summary.html#condycon">dynamic
   * constant</a> representing this {@link Qualifier}
   *
   * @nullability This method does not return {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  @Override // Binding<V, Qualifier<V>>
  protected final MethodHandleDesc describeConstructor() {
    return
      MethodHandleDesc.ofMethod(STATIC,
                                CD_Qualifier,
                                "of",
                                MethodTypeDesc.of(CD_Qualifier, CD_String, CD_Object, CD_Map, CD_Map));
  }


  /*
   * Static methods.
   */


  /**
   * Returns a {@link Qualifier}, which may or may not be newly
   * created, representing the supplied arguments.
   *
   * @param <V> the type of the {@link Qualifier}'s {@linkplain #value()
   * value} and of its {@linkplain #attributes() attribute values}
   *
   * @param name the {@link Qualifier}'s {@linkplain Qualifier#name()
   * name}; must not be {@code null}
   *
   * @return a {@link Qualifier}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public static final <V> Qualifier<V> of(final String name) {
    return of(name, null, null, null);
  }

  /**
   * Returns a {@link Qualifier}, which may or may not be newly
   * created, representing the supplied arguments.
   *
   * @param <V> the type of the {@link Qualifier}'s {@linkplain #value()
   * value} and of its {@linkplain #attributes() attribute values}
   *
   * @param name the {@link Qualifier}'s {@linkplain Qualifier#name()
   * name}; must not be {@code null}
   *
   * @param value the {@link Qualifier}'s {@linkplain
   * Qualifier#value() value}; may be {@code null}
   *
   * @return a {@link Qualifier}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public static final <V> Qualifier<V> of(final String name, final V value) {
    return of(name, value, null, null);
  }

  /**
   * Returns a {@link Qualifier}, which may or may not be newly
   * created, representing the supplied arguments.
   *
   * @param <V> the type of the {@link Qualifier}'s {@linkplain #value()
   * value} and of its {@linkplain #attributes() attribute values}
   *
   * @param name the {@link Qualifier}'s {@linkplain Qualifier#name()
   * name}; must not be {@code null}
   *
   * @param value the {@link Qualifier}'s {@linkplain
   * Qualifier#value() value}; may be {@code null}
   *
   * @param attributes the {@link Qualifier}'s {@linkplain
   * Qualifier#attributes() attributes}; may be {@code null}
   *
   * @return a {@link Qualifier}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public static final <V> Qualifier<V> of(final String name,
                                          final V value,
                                          final Map<? extends String, ?> attributes) {
    return of(name, value, attributes, null);
  }

  /**
   * Returns a {@link Qualifier}, which may or may not be newly
   * created, representing the supplied arguments.
   *
   * @param <V> the type of the {@link Qualifier}'s {@linkplain #value()
   * value} and of its {@linkplain #attributes() attribute values}
   *
   * @param name the {@link Qualifier}'s {@linkplain Qualifier#name()
   * name}; must not be {@code null}
   *
   * @param value the {@link Qualifier}'s {@linkplain
   * Qualifier#value() value}; may be {@code null}
   *
   * @param attributes the {@link Qualifier}'s {@linkplain
   * Qualifier#attributes() attributes}; may be {@code null}
   *
   * @param info the {@link Qualifier}'s {@linkplain Qualifier#info()
   * informational attributes}; may be {@code null}
   *
   * @return a {@link Qualifier}
   *
   * @nullability This method never returns {@code null}.
   *
   * @idempotency This method is idempotent and deterministic.
   *
   * @threadsafety This method is safe for concurrent use by multiple
   * threads.
   */
  public static final <V> Qualifier<V> of(final String name,
                                          final V value,
                                          final Map<? extends String, ?> attributes,
                                          final Map<? extends String, ?> info) {
    return new Qualifier<>(name, value, attributes, info);
  }

}
