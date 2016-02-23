package de.doubleslash.jedm.generate;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;

public interface BuildEntityGraphInterface {

   /**
    * This method needs to select data from a XML file and build a dynamic entity-graph with it.
    * 
    * @param em
    *           EntityManager for database access
    * @param graphName
    *           name of the entity graph (in XML)
    * @param classType
    *           type of root class-element of the entity graph
    * @return generated entity graph with same type as the parameter classType
    */
   <T> EntityGraph<?> generateEntityGraphXPath(EntityManager em, String graphName, Class<T> classType);
}
