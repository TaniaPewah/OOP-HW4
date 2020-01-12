package OOP.Solution;

import OOP.Provided.OOP4AmbiguousMethodException;
import OOP.Provided.OOP4MethodInvocationFailedException;
import OOP.Provided.OOP4NoSuchMethodException;
import OOP.Provided.OOP4ObjectInstantiationFailedException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OOPObject {
    private List<Object> directParents;
    private List<Object> allAnscestors;
    private HashMap<String, Object> virtualAncestor;
    private static HashMap<String, Object> StaticVirtualAncestor;
    private static Class youngest_object_created;

    /* this function initiates all of the virtual parents FIRST! via dfs. it uses the static map StaticVirtualAncestor
    so it always has access to which objects were already created */
    private void initVirtualClasses(Class myself) throws OOP4ObjectInstantiationFailedException {
        OOPParent[] annotations = (OOPParent[]) myself.getAnnotationsByType(OOPParent.class);
        for (OOPParent parentClass : annotations) {
            initVirtualClasses(parentClass.parent()); //in DFS first there is a recursive call.
            if (parentClass.isVirtual() && !StaticVirtualAncestor.containsKey(parentClass.parent().getName())) {
                //if the parent is virtual and it has not been initiated yet.
                try {
                    Object instance = parentClass.parent().getConstructor().newInstance();
                    StaticVirtualAncestor.put(parentClass.parent().getName(), instance);
                } catch (Exception e) {
                    throw new OOP4ObjectInstantiationFailedException();
                }
            }
        }
    }


    public OOPObject() throws OOP4ObjectInstantiationFailedException {
        Class myself = this.getClass();
        //we will use a static map to help with the construction. initiate it at the beginning of the construction process.
        if(youngest_object_created == null){
            youngest_object_created = myself;
            StaticVirtualAncestor = new HashMap<>();
        }

        directParents = new ArrayList<Object>();
        virtualAncestor = new HashMap<>();

        // get all my annotations - runtime reflection
        initVirtualClasses(myself);

        OOPParent[] annotations = (OOPParent[]) myself.getAnnotationsByType(OOPParent.class);
        // for every parent annotation add an instance to the directParents dictionary.
        // if its virtual- it has already been created. if its regular- create it and add.
        for (OOPParent parentAnnot : annotations) {
            Object instance = null;
            if (parentAnnot.isVirtual())
                instance = StaticVirtualAncestor.get(parentAnnot.parent().getName());
            else {
                try {
                    instance = parentAnnot.parent().getConstructor().newInstance();
                } catch (Exception e) {
                    throw new OOP4ObjectInstantiationFailedException();
                }
            }
            directParents.add(instance);
        }
        //at the end, redefine youngest_object_created to null, so it will work again next time we construct a new object.
        if(myself == youngest_object_created) {
            youngest_object_created = null;
            virtualAncestor = (HashMap<String, Object>) StaticVirtualAncestor.clone();
        }
    }

    public boolean multInheritsFrom(Class<?> cls) {
        // get all my annotations - runtime reflection
        // checks only for direct parents now-
        Class myself = this.getClass();
        boolean result = false;

        // for every parent
        for (Object parent : directParents) {

            // is my class = cls?
            if (parent.getClass().equals(cls)) {
                return true;
            }

            // is cls my parent's regular ancestor
            if (!cls.equals(OOPObject.class) && (cls).isAssignableFrom(parent.getClass())) {
                return true;
            }

            // if im a OOPObj - mulIn
            if ((OOPObject.class).isAssignableFrom(parent.getClass())){
                result = result || ((OOPObject)parent).multInheritsFrom(cls); //Inbal
            }
        }
        return result;
    }

    public Object definingObject(String methodName, Class<?>... argTypes)
            throws OOP4AmbiguousMethodException, OOP4NoSuchMethodException {

        boolean found = true;
        Object returned = null;
        Object returned_temp = null; //this object is used to compare the returned result, and the current result.
        //if the returned objects are the same, that means it is a result of virtual inheritance, and does not cause ambiguities.

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
                    if( parent != returned && maybeAmbiguous ){ //Inbal. if they are the same- its virtual inheritance!
                        throw new OOP4AmbiguousMethodException();
                    }
                    maybeAmbiguous = true;
                    found = true;
                    returned = parent;
                    
                } catch (NoSuchMethodException e) {

                    // if im a OOPObj - definingObject
                    if ((OOPObject.class).isAssignableFrom(parent.getClass())){
                        try {
                            returned_temp = ((OOPObject)parent).definingObject(methodName, argTypes);
                            if( returned_temp != returned && maybeAmbiguous ){ //should we check if it was already ambiguous?? Inbal
                                throw new OOP4AmbiguousMethodException();
                            }
                            returned = returned_temp;
                            maybeAmbiguous = true;
                            found = true;
                        }
                        catch (OOP4NoSuchMethodException OOPe){
                            //found = false; //Inbal if it was found before- leave it as it was.
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
            argTypes.add(arg.getClass());
        }

        Class[] typesClasses = new Class[callArgs.length];
        typesClasses = argTypes.toArray(typesClasses);
        Object defObj = this.definingObject(methodName, typesClasses );
        Object result;
        try {
            Method method = defObj.getClass().getMethod(methodName, typesClasses);
            result = method.invoke(defObj, callArgs);
        } catch (IllegalAccessException e) {
            throw new OOP4MethodInvocationFailedException();
        } catch (InvocationTargetException e) {
            throw new OOP4MethodInvocationFailedException();
        } catch (NoSuchMethodException e) {
            throw new OOP4NoSuchMethodException();
        }

        return result; //inbal should return result of function, and not the object that defines it.
    }
}
