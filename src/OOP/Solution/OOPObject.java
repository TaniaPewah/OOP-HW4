package OOP.Solution;

import OOP.Provided.OOP4AmbiguousMethodException;
import OOP.Provided.OOP4MethodInvocationFailedException;
import OOP.Provided.OOP4NoSuchMethodException;
import OOP.Provided.OOP4ObjectInstantiationFailedException;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OOPObject {
    private List<Object> directParents;
    private List<Object> allAnscestors;
    private HashMap<String, Object> virtualAncestor;

    public OOPObject() throws OOP4ObjectInstantiationFailedException {

        directParents = new ArrayList<Object>();
        virtualAncestor = new HashMap<>();

        // get all my annotations - runtime reflection
        Class myself = this.getClass();
        OOPParent[] annotations = (OOPParent[]) myself.getAnnotationsByType(OOPParent.class);

        // for every parent annotation
        for (OOPParent parentAnnot : annotations) {

            // debug print
            System.out.println(parentAnnot.parent().getName());
            Object object = null;
            try {
                // create newInstance
                object = parentAnnot.parent().getConstructor().newInstance();
            } catch (Exception e) {
                throw new OOP4ObjectInstantiationFailedException();
            }
            directParents.add(object);
        }
    }

    public boolean multInheritsFrom(Class<?> cls) {
        // get all my annotations - runtime reflection
        // checks only for direct parents now-
        // TODO add recursion
        Class myself = this.getClass();

        // for every parent
        for (Object parent : directParents) {

            // is my class = cls?
            if (parent.getClass().equals(cls)) {
                return true;
            }
            // if im a OOPObj - mulIn
            if ((OOPObject.class).isAssignableFrom(parent.getClass())){
                return ((OOPObject)parent).multInheritsFrom(cls);
            }
        }
        return false;
    }

    public Object definingObject(String methodName, Class<?>... argTypes)
            throws OOP4AmbiguousMethodException, OOP4NoSuchMethodException {
        // TODO: Implement
        //.getMethods()
        //.getDeclaredMethods()

        return null;
    }

    public Object invoke(String methodName, Object... callArgs) throws
            OOP4AmbiguousMethodException, OOP4NoSuchMethodException, OOP4MethodInvocationFailedException {
        // TODO: Implement
        return null;
    }
    public static void main(String[] args) throws OOP4ObjectInstantiationFailedException {

        System.out.print( "A parents: ");
        A a = new A();
        System.out.print( "B parents: ");
        B b = new B();
        System.out.print( "C parents: ");
        C c = new C();
        System.out.print( "D parents: ");
        D d = new D();
        System.out.println( "D multiinherits from A true: ");
        System.out.println( d.multInheritsFrom(A.class));
        System.out.println( "D multiinherits from B true: ");
        System.out.println( d.multInheritsFrom(B.class));
        System.out.println( "D multiinherits from String true: ");
        System.out.println( d.multInheritsFrom(String.class));
        System.out.println( "D multiinherits from Boolean false: ");
        System.out.println( d.multInheritsFrom(Boolean.class));
    }
}

class A extends OOPObject {
    public A() throws OOP4ObjectInstantiationFailedException {
        super();
    }
}

@OOPParent(parent = A.class, isVirtual = false)
class B extends OOPObject {
    public B() throws OOP4ObjectInstantiationFailedException {
        super();
    }
}

@OOPParent(parent = A.class, isVirtual = false)
class C extends OOPObject {
    public C() throws OOP4ObjectInstantiationFailedException {
        super();
    }
}
@OOPParent(parent = B.class, isVirtual = false)
@OOPParent(parent = C.class, isVirtual = false)
@OOPParent(parent = String.class, isVirtual = false)
class D extends OOPObject {
    public D() throws OOP4ObjectInstantiationFailedException {
        super();
    }
}