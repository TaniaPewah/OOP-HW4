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
    private HashMap<String, Object> virtualAncestor;



    public OOPObject() throws OOP4ObjectInstantiationFailedException {
        // TODO: Implement
        directParents = new ArrayList<Object>();
        virtualAncestor = new HashMap<>();

        // get all my annotations - runtime reflection
        Class myself = OOPObject.class;
        Annotation[] annotations = myself.getAnnotations();

        // for every parent annotation
        for(Annotation annotation : annotations){
            if(annotation instanceof OOPParents){
                for(OOPParent parentAnnot : ((OOPParents) annotation).parents()){
                directParents.add( parentAnnot.parent() ) ;
                // how to create instance of the class in the aprent attribute
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
