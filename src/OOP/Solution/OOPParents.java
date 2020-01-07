package OOP.Solution;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface OOPParents {
    // This is the container annotation for OOPParent.
    // Modify it to comply with the requirements for the @Repeatable annotation.
    // TODO: Implement
    OOPParent[] value();
}
