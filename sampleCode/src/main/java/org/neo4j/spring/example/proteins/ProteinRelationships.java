package org.neo4j.spring.example.proteins;

import org.neo4j.graphdb.RelationshipType;

public enum ProteinRelationships implements RelationshipType
{
    SUBCLASS_OF, BELONGS_TO_CLASS, INTERACTS_WITH_CLASS 
}
