package OOP.Solution;

import OOP.Provided.OOP4AmbiguousMethodException;
import OOP.Provided.OOP4MethodInvocationFailedException;
import OOP.Provided.OOP4NoSuchMethodException;
import OOP.Provided.OOP4ObjectInstantiationFailedException;

public class OOPObject {
    private List<Object> directParents;
    private HashMap<String, Object> virtualAncestor;



    public OOPObject() throws OOP4ObjectInstantiationFailedException {
        // TODO: Implement
        directParents = new ArrayList<Object> ();
        virtualAncestor = new HashMap<>();

        // get all my annotations - runtime reflection
        Class myself = OOPObject.class;
        Annotation[] annotations = myself.getAnnotations();

        // for every parent annotation
        for(Annotation annotation : annotations){
            if(annotation instanceof OOPParent){
                OOPParent parentAnnot = (OOPParent) annotation;
                directParents.add( new parentAnnot.parent());
            }
        }
    }

    public boolean multInheritsFrom(Class<?> cls) {
        // TODO: Implement
        return false;
    }

    public Object definingObject(String methodName, Class<?> ...argTypes)
            throws OOP4AmbiguousMethodException, OOP4NoSuchMethodException {
        // TODO: Implement
        return null;
    }

    public Object invoke(String methodName, Object... callArgs) throws
            OOP4AmbiguousMethodException, OOP4NoSuchMethodException, OOP4MethodInvocationFailedException {
        // TODO: Implement
        return null;
    }
}
