package OOP.Solution;

import OOP.Provided.OOP4AmbiguousMethodException;
import OOP.Provided.OOP4MethodInvocationFailedException;
import OOP.Provided.OOP4NoSuchMethodException;
import OOP.Provided.OOP4ObjectInstantiationFailedException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        boolean result = false;

        // for every parent
        for (Object parent : directParents) {

            // is my class = cls?
            if (parent.getClass().equals(cls)) {
                return true;
            }

            // is cls my parent's regular ancestor
            if ((cls).isAssignableFrom(parent.getClass())) {
                return true;
            }

            // if im a OOPObj - mulIn
            if ((OOPObject.class).isAssignableFrom(parent.getClass())){
                result = ((OOPObject)parent).multInheritsFrom(cls);
            }
        }
        return result;
    }

    public Object definingObject(String methodName, Class<?>... argTypes)
            throws OOP4AmbiguousMethodException, OOP4NoSuchMethodException {
        // TODO: check whether ambiguous
        boolean found = true;
        Object returned = null;

        // check whether this (I) have the method
        try {
            this.getClass().getMethod(methodName, argTypes);
            return this;
        } catch (NoSuchMethodException e){
            found = false;
        }

        // check whether my parents have the method
        if (!found) {

            boolean maybeAmbiguous = false;
            // for every parent
            for (Object parent : directParents) {
                try {
                    // if im not OOPObjParent
                    parent.getClass().getMethod(methodName, argTypes);
                    if( maybeAmbiguous ){
                        throw new OOP4AmbiguousMethodException();
                    }
                    maybeAmbiguous = true;
                    found = true;
                    returned = parent;
                } catch (NoSuchMethodException e) {

                    // if im a OOPObj - definingObject
                    if ((OOPObject.class).isAssignableFrom(parent.getClass())){
                        try {
                            returned = ((OOPObject)parent).definingObject(methodName, argTypes);
                            maybeAmbiguous = true;
                            found = true;
                        }
                        catch (OOP4NoSuchMethodException OOPe){
                            found = false;
                        }
                    }
                }
            }
            if( found ) {
                return returned;
            }
        }

        // method not found
        throw new OOP4NoSuchMethodException();
    }

    public Object invoke(String methodName, Object... callArgs) throws
            OOP4AmbiguousMethodException, OOP4NoSuchMethodException, OOP4MethodInvocationFailedException {

        // Separate callArgs types, find the object who defines the method, invoke
        List<Class> argTypes = new ArrayList<Class>();

        for( Object arg : callArgs ){
            if(arg.getClass().equals(Integer.class)){
                argTypes.add( Integer.TYPE );
            } else {
                argTypes.add(arg.getClass());
            }
        }

        Class[] typesClasses = new Class[callArgs.length];
        typesClasses = argTypes.toArray(typesClasses);
        Object defObj = this.definingObject(methodName, typesClasses );

        try {
            Method method = defObj.getClass().getMethod(methodName, typesClasses);
            method.invoke(defObj, callArgs);
        } catch (IllegalAccessException e) {
            throw new OOP4MethodInvocationFailedException();
        } catch (InvocationTargetException e) {
            throw new OOP4MethodInvocationFailedException();
        } catch (NoSuchMethodException e) {
            throw new OOP4MethodInvocationFailedException();
        }

        return defObj;
    }
    public static void main(String[] args) throws OOP4ObjectInstantiationFailedException, OOP4NoSuchMethodException, OOP4AmbiguousMethodException, OOP4MethodInvocationFailedException {

        System.out.print( "A parents: ");
        A a = new A();
        System.out.print( "B parents: ");
        B b = new B();
        System.out.print( "C parents: ");
        C c = new C();
        System.out.print( "D parents: ");
        D d = new D();

        // multiInherits from test
        System.out.println( "D multiinherits from P true: ");
        System.out.println( d.multInheritsFrom(P.class));
        System.out.println( "D multiinherits from A true: ");
        System.out.println( d.multInheritsFrom(A.class));
        System.out.println( "D multiinherits from B true: ");
        System.out.println( d.multInheritsFrom(B.class));
        System.out.println( "D multiinherits from String true: ");
        System.out.println( d.multInheritsFrom(String.class));
        System.out.println( "D multiinherits from Boolean false: ");
        System.out.println( d.multInheritsFrom(Boolean.class));
        System.out.println( "D multiinherits from OOPObjc true: ");
        System.out.println( d.multInheritsFrom(OOPObject.class));

        // definingObject test
        System.out.println( "D Sleeps: D ");
        System.out.println( d.definingObject("Sleep").getClass().getName());

        System.out.println( "D Sleeps int: D");
        System.out.println( d.definingObject("Sleep", int.class, String.class ).getClass().getName());


        //System.out.println( "D Speaks: B ");
        //System.out.println( d.definingObject("Speak").getClass().getName());

        //System.out.println( "D Sneaks: A ");
        //System.out.println( d.definingObject("Sneak").getClass().getName());

        // TODO doesnt work?
        System.out.println( "D Stores: P ?");
        System.out.println( d.definingObject("Store").getClass().getName());

        // invoke test
        System.out.println( "D invokes Sleep: zzz ");
        d.invoke("Sleep"); // no args

        System.out.println( "D invokes Sleep: nnnnn ");
        d.invoke("Sleep", 5, "d"); // Integer

        System.out.println( "D invokes Sleep: ssssss ");
        d.invoke("Sleep", "s"); // String

        System.out.println( "D invokes Speak: Im hunter B ");
        d.invoke("Speak"); // String

        System.out.println( "D invokes Sneak: Im hunter A");
        d.invoke("Sneak"); // String
    }
}

class A extends OOPObject {
    public A() throws OOP4ObjectInstantiationFailedException {
        super();
    }
    public void Sneak(){
        System.out.println( "Im hunter A");
    }
}

@OOPParent(parent = A.class, isVirtual = false)
class B extends OOPObject {
    public B() throws OOP4ObjectInstantiationFailedException {
        super();
    }
    public void Speak(){
        System.out.println( "Im hunter B");
    }
}

@OOPParent(parent = A.class, isVirtual = false)
class C extends OOPObject {
    public C() throws OOP4ObjectInstantiationFailedException {
        super();
    }
    public void Speak(){
        System.out.println( "Im hunter C");
    }
}

class P {
    public P() {
    }
    public void Store(){
        System.out.println( "Im hunter P");
    }
}

class T extends P {
    public T() {
        super();
    }
    public void Speak(){
        System.out.println( "Im hunter T");
    }
}

class S extends T {
    public S() {
        super();
    }
    public void Speak(){
        System.out.println( "Im hunter S");
    }
}

@OOPParent(parent = B.class, isVirtual = false)
@OOPParent(parent = C.class, isVirtual = false)
@OOPParent(parent = String.class, isVirtual = false)
@OOPParent(parent = S.class, isVirtual = false)
class D extends OOPObject {
    public D() throws OOP4ObjectInstantiationFailedException {
        super();
    }
    public void Sleep(){
        System.out.println( "zzz");
    }
    public void Sleep( int n, String s ){
        System.out.println( "nnnn");
    }
    public void Sleep( String st ){
        System.out.println( "sssss");
    }
}