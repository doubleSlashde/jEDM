package de.doubleslash.jedm.generate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Subgraph;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BuildEntityGraph implements BuildEntityGraphInterface {
	private static XPath xPath;
	private static final Logger LOGGER = Logger.getLogger(BuildDozerMapping.class.getName());

	/**
	 * Builds an subgraph and connects it with the upper graph.
	 * 
	 * @param subgraph
	 *            current subgraph without any elements (graph is already
	 *            connected to upper-graph)
	 * @param graphName
	 *            name of the entity-graph
	 * @param upperElem
	 *            Element which references to the subgraph
	 * @param subgraphList
	 *            List with all defined subgraph-nodes
	 * @param doc
	 *            Document which is needed for the compilation of the xpath
	 * @return a new build subgraph
	 */
	private Subgraph<?> buildSubgraph(final Subgraph<?> subgraph, final String graphName, final Element upperElem,
			final NodeList subgraphList, final Document doc) {

		// searches for the right subgraph
		for (int j = 0; j < subgraphList.getLength(); ++j) {
			final Element subgraphElem = (Element) subgraphList.item(j);

			if (subgraphElem.getAttribute("name").equals(upperElem.getAttribute("subgraph"))) {
				final String attributesSubgraphListString = "//named-entity-graph[@name='" + graphName
						+ "']/subgraph[@name='" + subgraphElem.getAttribute("name") + "']/named-attribute-node";

				NodeList attributesSubgraphList = null;
				try {
					attributesSubgraphList = (NodeList) xPath.compile(attributesSubgraphListString).evaluate(doc,
							XPathConstants.NODESET);
				} catch (final XPathExpressionException e) {
					LOGGER.warning("Exception in BuildEntityGraph::buildSubgraph");
				}

				// reads all attributes of the subgraph
				for (int k = 0; k < attributesSubgraphList.getLength(); ++k) {
					final Element attributeSubgraphElem = (Element) attributesSubgraphList.item(k);

					// attributes of subgraph
					if ("".equals(attributeSubgraphElem.getAttribute("subgraph"))) {
						// adds the attributes to the subgraph
						subgraph.addAttributeNodes(attributeSubgraphElem.getAttribute("name"));
					}
					// references to other subgraphs
					else {
						Subgraph<?> deeperSubgraph = null;
						deeperSubgraph = subgraph.addSubgraph(attributeSubgraphElem.getAttribute("name"));
						buildSubgraph(deeperSubgraph, graphName, attributeSubgraphElem, subgraphList, doc);
					}
				}
			}
		}
		return subgraph;
	}

	/**
	 * Builds an entity graph from a XML file.
	 * 
	 * @param graphName
	 *            name of the entity graph
	 * @param classType
	 *            entity graph with rootElement of classType
	 * @return result generated entity graph with rootElement of classType - can
	 *         be null if an error occurs during entity graph initialization
	 */
	@Override
	public <T> EntityGraph<?> generateEntityGraphXPath(final EntityManager em, final String graphName,
			final Class<T> classType) {
		EntityGraph<?> graph = null;

		try {
			URL centralMappingFilePath = JEntityGraphDataMapper.class.getResource(FilePaths.CENTRAL_XML_NAME.path());
			File centralRules;

			centralRules = new File(centralMappingFilePath.toURI());

			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document doc = dBuilder.parse(centralRules);
			doc.getDocumentElement().normalize();

			xPath = XPathFactory.newInstance().newXPath();

			// list of attribute-nodes from the root class of the selected
			// entity graph
			final String attributeRootNodeListString = "//named-entity-graph[@name='" + graphName
					+ "']/named-attribute-node";
			final NodeList attributeRootNodeList = (NodeList) xPath.compile(attributeRootNodeListString).evaluate(doc,
					XPathConstants.NODESET);

			// list of all subgraphs
			final String subgraphListString = "//named-entity-graph[@name='" + graphName + "']/subgraph";
			final NodeList subgraphList = (NodeList) xPath.compile(subgraphListString).evaluate(doc,
					XPathConstants.NODESET);

			// create EntityGraph
			graph = em.createEntityGraph(classType);

			// reads all named-attribute-nodes below the named-entity-graph
			for (int i = 0; i < attributeRootNodeList.getLength(); ++i) {
				final Element nodeElem = (Element) attributeRootNodeList.item(i);

				// adds the attributes to the graph
				if ("".equals(nodeElem.getAttribute("subgraph"))) {
					graph.addAttributeNodes(nodeElem.getAttribute("name"));
				}
				// reference to a subgraph
				else {
					// create subgraph with a reference to the upper class
					final Subgraph<?> subgraph = graph.addSubgraph(nodeElem.getAttribute("name"));
					buildSubgraph(subgraph, graphName, nodeElem, subgraphList, doc);
				}
			}
		} catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException
				| URISyntaxException e) {
			LOGGER.warning("Exception in BuildEntityGraph::generateEntityGraphXPath");
		}
		return graph;
	}

	/**
	 * Requests data with an id over a defined entity graph.
	 * 
	 * @param graph
	 *            entity graph which helps to fetch the data
	 * @param classType
	 *            type of class where the entity manager should search for the
	 *            id
	 * @param id
	 *            specification of the database request
	 * @return result object of requested type
	 */
	public <T> T requestDataViaEntityGraph(final EntityManager em, final EntityGraph<?> graph, final Class<T> classType,
			final int id) {
		T result = null;

		final Map<String, Object> hints = new HashMap<String, Object>();
		hints.put("javax.persistence.loadgraph", graph);

		result = em.find(classType, id, hints);

		return result;
	}
}
