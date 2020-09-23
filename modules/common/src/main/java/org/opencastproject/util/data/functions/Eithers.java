/**
 * Licensed to The Apereo Foundation under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 *
 * The Apereo Foundation licenses this file to you under the Educational
 * Community License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at:
 *
 *   http://opensource.org/licenses/ecl2.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.opencastproject.util.data.functions;

import org.opencastproject.util.data.Either;
import org.opencastproject.util.data.Function;
import org.opencastproject.util.data.Option;

/** {@link Either} related functions. */
public final class Eithers {
  private Eithers() {
  }

  /** Like <code>Either#right()#toOption()</code>. */
  public static <A, B> Function<Either<A, B>, Option<B>> toOption() {
    return new Function<Either<A, B>, Option<B>>() {
      @Override public Option<B> apply(Either<A, B> either) {
        return either.right().toOption();
      }
    };
  }

  /** Flatten nested Eithers. */
  public static <A, B> Either<A, B> flatten(Either<A, Either<A, B>> e) {
    return e.right().bind(new Function<Either<A, B>, Either<A, B>>() {
      @Override public Either<A, B> apply(Either<A, B> e) {
        return e.right().either();
      }
    });
  }
}
