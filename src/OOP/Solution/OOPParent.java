package OOP.Solution;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Target(ElementType.TYPE)
@Repeatable(OOPParents.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface OOPParent {
    Class<?> parent();
    boolean isVirtual() default false;

}
