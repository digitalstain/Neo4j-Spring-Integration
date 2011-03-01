package org.neo4j.spring.example.proteins;

import org.apache.commons.collections.EnumerationUtils;
import org.springframework.data.annotation.Indexed;
import org.springframework.data.graph.annotation.NodeEntity;
import org.springframework.data.graph.annotation.RelatedTo;
import org.springframework.data.graph.core.Direction;

@NodeEntity
public class Protein
{
    @Indexed( indexName = "proteins" )
    private String name;

    @RelatedTo( type = "BELONGS_TO_CLASS", elementClass = ProteinClass.class, direction = Direction.OUTGOING )
    private ProteinClass belongsToFamily;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }
}
