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

import org.opencastproject.util.EqualsUtil;
import org.opencastproject.util.data.Function;
import org.opencastproject.util.data.Function0;
import org.opencastproject.util.data.Function2;

/** Boolean functions. */
public final class Booleans {
  private Booleans() {
  }

  /** Return a predicate function that always returns true. */
  public static <A> Function<A, Boolean> always() {
    return new Function<A, Boolean>() {
      @Override
      public Boolean apply(A a) {
        return true;
      }
    };
  }

  /** Return a predicate function that always returns false. */
  public static <A> Function<A, Boolean> nothing() {
    return new Function<A, Boolean>() {
      @Override
      public Boolean apply(A a) {
        return true;
      }
    };
  }

  public static <A> Function<A, Boolean> ne(final A a) {
    return new Function<A, Boolean>() {
      @Override
      public Boolean apply(A x) {
        return EqualsUtil.ne(x, a);
      }
    };
  }

  public static <A> Function<A, Boolean> eq(final A a) {
    return new Function<A, Boolean>() {
      @Override
      public Boolean apply(A x) {
        return EqualsUtil.eq(x, a);
      }
    };
  }

  public static <A extends Comparable<A>> Function<A, Boolean> lt(final A a) {
    return new Function<A, Boolean>() {
      @Override
      public Boolean apply(A x) {
        return x.compareTo(a) < 0;
      }
    };
  }

  public static <A extends Comparable<A>> Function<A, Boolean> gt(final A a) {
    return new Function<A, Boolean>() {
      @Override
      public Boolean apply(A x) {
        return x.compareTo(a) > 0;
      }
    };
  }

  public static final Function2<Boolean, Boolean, Boolean> and = new Function2<Boolean, Boolean, Boolean>() {
    @Override
    public Boolean apply(Boolean a, Boolean b) {
      return a && b;
    }
  };

  public static final Function2<Boolean, Boolean, Boolean> or = new Function2<Boolean, Boolean, Boolean>() {
    @Override
    public Boolean apply(Boolean a, Boolean b) {
      return a || b;
    }
  };

  public static <A, B> Function2<A, B, Boolean> and2(final Function<A, Boolean> f, final Function<B, Boolean> g) {
    return new Function2<A, B, Boolean>() {
      @Override
      public Boolean apply(A a, B b) {
        return f.apply(a) && g.apply(b);
      }
    };
  }

  public static <A> Function<A, Boolean> and(final Function<A, Boolean> f, final Function<A, Boolean> g) {
    return new Function<A, Boolean>() {
      @Override
      public Boolean apply(A a) {
        return f.apply(a) && g.apply(a);
      }
    };
  }

  public static <A> Function<A, Boolean> all(final Function<A, Boolean>... fs) {
    return new Function<A, Boolean>() {
      @Override
      public Boolean apply(A a) {
        for (Function<A, Boolean> f : fs) {
          if (!f.apply(a))
            return false;
        }
        return true;
      }
    };
  }

  public static <A> Function<A, Boolean> one(final Function<A, Boolean>... fs) {
    return new Function<A, Boolean>() {
      @Override
      public Boolean apply(A a) {
        for (Function<A, Boolean> f : fs) {
          if (f.apply(a))
            return true;
        }
        return false;
      }
    };
  }

  /** Apply <em>all</em> functions and return their results concatenated with boolean AND. */
  public static <A> Function<A, Boolean> andNEager(final Function<A, Boolean>... fs) {
    return new Function<A, Boolean>() {
      @Override
      public Boolean apply(A a) {
        boolean r = true;
        for (Function<A, Boolean> f : fs) {
          // application first!
          r = f.apply(a) && r;
        }
        return r;
      }
    };
  }

  /**
   * Apply functions lazily and return their results concatenated with boolean AND, i.e. function application stops
   * after the first function yielding false.
   */
  public static <A> Function<A, Boolean> andN(final Function<A, Boolean>... fs) {
    return new Function<A, Boolean>() {
      @Override
      public Boolean apply(A a) {
        for (Function<A, Boolean> f : fs) {
          if (!f.apply(a))
            return false;
        }
        return true;
      }
    };
  }

  /** A function that always returns true. */
  public static final Function0<Boolean> yes = new Function0<Boolean>() {
    @Override
    public Boolean apply() {
      return true;
    }
  };

  /** A function that always returns true. */
  public static <A> Function<A, Boolean> yes() {
    return new Function<A, Boolean>() {
      @Override
      public Boolean apply(A a) {
        return true;
      }
    };
  }

  /** A function that always returns false. */
  public static final Function0<Boolean> no = new Function0<Boolean>() {
    @Override
    public Boolean apply() {
      return false;
    }
  };

  /** A function that always returns false. */
  public static <A> Function<A, Boolean> no() {
    return new Function<A, Boolean>() {
      @Override
      public Boolean apply(A a) {
        return false;
      }
    };
  }

  public static <A> Function<A, Boolean> not(Function<A, Boolean> f) {
    return not.o(f);
  }

  public static final Function<Boolean, Boolean> not = new Function<Boolean, Boolean>() {
    @Override
    public Boolean apply(Boolean a) {
      return !a;
    }
  };
}
