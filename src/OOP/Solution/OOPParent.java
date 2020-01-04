package OOP.Solution;

import java.lang.annotation.Repeatable;

@Repeatable(OOPParents.class)
public @interface OOPParent {
    Class<?> parent();
    boolean isVirtual() default false;

}
