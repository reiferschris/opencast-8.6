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


package org.opencastproject.util.data;

import static org.opencastproject.util.data.functions.Functions.chuck;

/**
 * Run a side effect.
 *
 * @see X
 */
public abstract class Effect2<A, B> extends Function2<A, B, Void> {
  @Override
  public Void apply(A a, B b) {
    run(a, b);
    return null;
  }

  /** Run the side effect. */
  protected abstract void run(A a, B b);

  /** Return the effect as a function of arity 2. */
  public Function2<A, B, Void> toFunction() {
    return this;
  }

  /** Version of {@link Effect0} that allows for throwing a checked exception. */
  public abstract static class X<A, B> extends Effect2<A, B> {
    @Override
    protected final void run(A a, B b) {
      try {
        xrun(a, b);
      } catch (Exception e) {
        chuck(e);
      }
    }

    /**
     * Run the side effect. Any thrown exception gets "chucked" so that you may
     * catch them as is. See {@link org.opencastproject.util.data.functions.Functions#chuck(Throwable)} for details.
     */
    protected abstract void xrun(A a, B b) throws Exception;
  }
}
