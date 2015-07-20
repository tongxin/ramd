package ramd;

import java.lang.reflect.Method;

public abstract class Checkable<C extends Checkable>{

    /**
     * Verify the existence of certain method in this class.
     * @param mth method name.
     * @param prototype method's parameter types.
     * @return method object or null if there's no matching.
     */
    public Method check(String mth, Class[] prototype) {
        for (Method m : this.getClass().getMethods()) {
            if (!m.getName().equals(mth)) continue;

            Class[] ptypes = m.getParameterTypes();

            if (ptypes.length != prototype.length) continue;

            int i = 0;
            for (; i < ptypes.length; i++)
                if (!ptypes[i].isAssignableFrom(prototype[i]))
                    break;

            if (i < ptypes.length) continue;

            return m;
        }
        return null;
    }

    /**
     * Provides a way to create a non-trivial instance through reflection.
     * Usage: CheckableClass ins = CheckableClass.newInstance().build()
     * @return created instance of class C
     */
    abstract C build();

}
