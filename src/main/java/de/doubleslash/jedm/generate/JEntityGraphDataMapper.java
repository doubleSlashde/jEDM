/*
 * 2015, doubleSlash Net-Business GmbH, http://www.doubleSlash.de
 */
package de.doubleslash.jedm.generate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.dozer.DozerBeanMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Sebastian Wild
 */
public class JEntityGraphDataMapper {

	/**
	 * Searches for an entity graph with a matching name
	 * 
	 * @param graphName
	 *            name of the entity graph
	 * @return true if found, false if not found
	 */
	public static boolean findEntityGraph(final String graphName) {
		URL centralMappingFilePath = JEntityGraphDataMapper.class.getResource(FilePaths.CENTRAL_XML_NAME.path());
		File centralRules;
		try {
			centralRules = new File(centralMappingFilePath.toURI());
		} catch (URISyntaxException e1) {
			throw new RuntimeException("Could not load mapping rules");
		}
		Document doc = null;

		XPath xPath = null;

		final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		String entity_graph_list = "";
		NodeList entityGraphList = null;

		try {
			dBuilder = dbFactory.newDocumentBuilder();

			doc = dBuilder.parse(centralRules);
			doc.getDocumentElement().normalize();

			xPath = XPathFactory.newInstance().newXPath();

			// list of all entity graphs
			entity_graph_list = "//named-entity-graph[@name='" + graphName + "']";
			entityGraphList = (NodeList) xPath.compile(entity_graph_list).evaluate(doc, XPathConstants.NODESET);
		} catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
			e.printStackTrace();
		}

		for (int pos = 0; pos < entityGraphList.getLength(); ++pos) {
			final Element graphElem = (Element) entityGraphList.item(pos);

			if (graphElem.getAttribute("name").equals(graphName)) {
				System.out.println("INFO: EntityGraph with name \'" + graphName + "\' found!");
				return true;
			}
		}
		System.out.println("WARN: No EntityGraph with name \'" + graphName + "\' found!");
		return false;
	}

	/**
	 * Combines using an entityGraph for accessing data from a database with
	 * dozer mapping and returns a decoupled data object
	 * 
	 * @param em
	 *            entity manager for database access
	 * @param classTypeBE
	 *            source object type
	 * @param classTypeDO
	 *            destination object type
	 * @param entityGraphName
	 *            name of the entity graph
	 * @param id
	 *            identification number for accessed entity
	 * @return data object of type classTypeDO
	 */
	public <BE, DO> DO useJEntityGraphDataMapper(final EntityManager em, final Class<BE> classTypeBE,
			final Class<DO> classTypeDO, final String entityGraphName, final int id) {
		final BuildDozerMapping dozerMappingBuilder = new BuildDozerMapping();
		final BuildEntityGraph entityGraphBuilder = new BuildEntityGraph();

		BE businessEntity = null;
		DO dataObject = null;
		try {
			dataObject = classTypeDO.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		@SuppressWarnings("unchecked")
		final EntityGraph<BE> xmlGraph = (EntityGraph<BE>) entityGraphBuilder.generateEntityGraphXPath(em,
				entityGraphName, classTypeBE);

		final Map<String, Object> hints = new HashMap<String, Object>();
		hints.put("javax.persistence.loadgraph", xmlGraph);

		businessEntity = em.find(classTypeBE, id, hints);

		final DozerBeanMapper mapper = dozerMappingBuilder.generateMappingRulesXPath(entityGraphName);
		mapper.map(businessEntity, dataObject);

		return dataObject;
	}

	public static void main(final String[] args) {
	}
}