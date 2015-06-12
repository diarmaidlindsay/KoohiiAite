package com.diarmaidlindsay.koohii.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent an entry in the primitive table
 */
public class Primitive {
    private int id;
    private String primitiveText;

    public Primitive(int id, String primitiveText) {
        this.id = id;
        this.primitiveText = primitiveText;
    }

    public int getId() {
        return id;
    }

    public String getPrimitiveText() {
        return primitiveText;
    }

    /**
     * Given the complete ordered list of primitives, get the primitive text of
     * the ids which are in the HeisigToPrimitive objects
     */
    public static List<String> getPrimitiveText(List<Primitive> primitives, List<Integer> primitiveIds)
    {
        List<String> primitiveText = new ArrayList<>();

        for(Integer id : primitiveIds)
        {
            //resolve the primitive id to the actual text
            //convert to 0-based index from db 1-based
            primitiveText.add(primitives.get(id-1).getPrimitiveText());
        }

        return primitiveText;
    }

    public static List<Integer> getPrimitiveIdsContaining(String primitiveText, List<Primitive> primitives)
    {
        List<Integer> primitiveIds = new ArrayList<>();

        for(Primitive primitive : primitives)
        {
            if(primitive.getPrimitiveText().contains(primitiveText))
            {
                primitiveIds.add(primitive.getId());
            }
        }

        return primitiveIds;
    }

    public static int getPrimitiveIdWhichMatches(String primitiveText, List<Primitive> primitives)
    {
        for(Primitive primitive : primitives)
        {
            if(primitive.getPrimitiveText().equals(primitiveText))
            {
                return primitive.getId();
            }
        }

        return -1;
    }
}
