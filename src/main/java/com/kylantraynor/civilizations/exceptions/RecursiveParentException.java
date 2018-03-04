package com.kylantraynor.civilizations.exceptions;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.utils.Identifier;

import java.util.UUID;

public class RecursiveParentException extends Exception {

    final Identifier child;
    final Identifier parent;

    public RecursiveParentException(Group child, Group parent){
        super("Attempted to set " + child.getIdentifier().toString() + " as a parent of " + parent.getIdentifier().toString() + " while the latter is already a parent of the former.");
        this.child = child.getIdentifier();
        this.parent = parent.getIdentifier();
    }

    public Identifier getChild(){
        return child;
    }

    public Identifier getParent(){
        return parent;
    }

}
