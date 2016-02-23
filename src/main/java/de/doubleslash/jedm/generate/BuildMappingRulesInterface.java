package de.doubleslash.jedm.generate;

public interface BuildMappingRulesInterface<Mapper> {
   /**
    * This method needs to select data from a XML file and build a dynamic entity-mapping with it. Which attributes are
    * getting mapped depends on the entity graph. Possible implementations might use the mapping-frameworks Dozer or
    * Orika.
    * 
    * @param -
    *           name of the entity graph
    */
   Mapper generateMappingRulesXPath(String graphName);

}
