package ramd.api;

import java.lang.reflect.Method;

public abstract class Checkable<C extends Checkable>{

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

    abstract C build();
}
