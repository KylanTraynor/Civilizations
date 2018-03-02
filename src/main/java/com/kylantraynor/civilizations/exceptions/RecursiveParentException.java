package com.kylantraynor.civilizations.exceptions;

import com.kylantraynor.civilizations.groups.Group;

import java.util.UUID;

public class RecursiveParentException extends Exception {

    final UUID child;
    final UUID parent;

    public RecursiveParentException(Group child, Group parent){
        super("Attempted to set " + child.getUniqueId().toString() + " as a parent of " + parent.getUniqueId().toString() + " while the latter is already a parent of the former.");
        this.child = child.getUniqueId();
        this.parent = parent.getUniqueId();
    }

    public UUID getChild(){
        return child;
    }

    public UUID getParent(){
        return parent;
    }

}
