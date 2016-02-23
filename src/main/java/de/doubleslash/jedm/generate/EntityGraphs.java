package de.doubleslash.jedm.generate;

/**
 * Contains all possible names for requesting data with an entity graph.
 * 
 * @author swild
 */
public enum EntityGraphs {
   // Fetches Customer-Data
   CUSTOMER_ULTRA_LIGHT("EntityGraph_CustomerUltraLight_CentralXML"),
   // Fetches Customer- and Address-Data
   CUSTOMER_LIGHT("EntityGraph_CustomerLight_CentralXML"),
   // Fetches Customer-, Address, and Bill-Data
   CUSTOMER_BASIC("EntityGraph_CustomerBasic_CentralXML"),
   // Fetches Customer-, Address, Bill and Item-Data
   CUSTOMER_FULL("EntityGraph_CustomerFull_CentralXML");

   private String graphName;

   EntityGraphs(final String graphName) {
      this.graphName = graphName;
   }

   public String graphName() {
      return graphName;
   }

}
