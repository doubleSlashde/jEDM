package de.doubleslash.jedm.generate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.TypeMappingBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Sebastian Wild
 */
public class BuildDozerMapping extends Exception implements BuildMappingRulesInterface<DozerBeanMapper> {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(BuildDozerMapping.class.getName());
	private static DozerBeanMapper mapper = null;

	/**
	 * Default constructor.
	 */
	public BuildDozerMapping() {
		super();
	}

	/**
	 * Constructor for Exception handling.
	 * 
	 * @param msg
	 *            Error message
	 */
	public BuildDozerMapping(final String msg) {
		super(msg);
	}

	/**
	 * Help method: maps all primitive attributes of the class and excludes
	 * subgraphs, if they are not mentioned in the entity graph.
	 * 
	 * @param builder
	 *            - contains the classes between which is mapped
	 * @param attributeNodeList
	 *            - list of attributes defined in the entity graph
	 * @param classNodeList
	 *            - list of all attributes of the class (for mapping)
	 */
	private void mapAttributes(final TypeMappingBuilder builder, final NodeList attributeNodeList,
			final NodeList classNodeList) {
		final String maptoString = "mapto";
		final String nameString = "name";
		for (int i = 0; i < classNodeList.getLength(); ++i) {
			final Element nodeElem = (Element) classNodeList.item(i);

			// maps primitive data types
			if ("".equals(nodeElem.getAttribute("subclass"))) {
				String mapto = nodeElem.getAttribute(maptoString);
				if ("".equals(nodeElem.getAttribute(maptoString))) {
					mapto = nodeElem.getAttribute(nameString);
				}
				builder.fields(nodeElem.getAttribute(nameString), mapto);
			}
			// found a reference to a subgraph
			else {
				boolean subgraphFound = false;

				// searching for the element in the nodes of the entity graph
				for (int j = 0; j < attributeNodeList.getLength(); ++j) {
					final Element attributeElem = (Element) attributeNodeList.item(j);

					// if this node is mentioned in the entity graph -> include
					// attribute
					if (attributeElem.getAttribute(nameString).equals(nodeElem.getAttribute(nameString))) {
						subgraphFound = true;
						String mapto = nodeElem.getAttribute(maptoString);
						if ("".equals(nodeElem.getAttribute(maptoString))) {
							mapto = nodeElem.getAttribute(nameString);
						}
						builder.fields(nodeElem.getAttribute(nameString), mapto);
					}
				}
				// exclude, if the subgraph is not explicit named in the entity
				// graph
				if (!subgraphFound) {
					builder.exclude(nodeElem.getAttribute(nameString));
				}
			}
		}
	}

	/**
	 * Help method: searches for a match between the classes defined in an
	 * entity graph and the classes for the mapping rules.
	 * 
	 * @param classList
	 *            - list of all defined classes below the mapping-classes tag
	 * @param className
	 *            - name of the class, defined in the entity graph
	 * @return classElem - found Element of the list of classes or null
	 */
	private Element classExistsInMappingRules(final NodeList classList, final String className) {
		// searches for a match between the entity-graph class and
		// the classes defined for the mapping
		for (int i = 0; i < classList.getLength(); ++i) {
			final Element classElem = (Element) classList.item(i);

			if (classElem.getAttribute("source").equals(className)) {
				return classElem;
			}
		}
		return null;
	}

	/**
	 * Builds mapping rules for Dozer on the base of a EntityGraph definition.
	 * 
	 * @param graphName
	 *            Name of the EntityGraph, defined in XML
	 * @return complete mapper of Type DozerBeanMapper
	 * @see de.doubleslash.jedm.generate.BuildMappingRulesInterface#mapEntitiesXPath()
	 */
	@Override
	public DozerBeanMapper generateMappingRulesXPath(final String graphName) {
		mapper = new DozerBeanMapper();

		URL centralMappingFilePath = JEntityGraphDataMapper.class.getResource(FilePaths.CENTRAL_XML_NAME.path());
		File centralRules;
		try {
			centralRules = new File(centralMappingFilePath.toURI());
		} catch (URISyntaxException e1) {
			throw new RuntimeException("Could not load mapping rules");
		}

		final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();

			final Document doc = dBuilder.parse(centralRules);
			doc.getDocumentElement().normalize();

			final XPath xPath = XPathFactory.newInstance().newXPath();

			// list of all classes in <mapping-classes>
			final String classListString = "/entity-structure/mapping-classes/class";
			final NodeList classList = (NodeList) xPath.compile(classListString).evaluate(doc, XPathConstants.NODESET);

			// node of the entity graph
			final String rootNodeString = "//named-entity-graph[@name='" + graphName + "']";
			final NodeList rootNode = (NodeList) xPath.compile(rootNodeString).evaluate(doc, XPathConstants.NODESET);

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

			final BeanMappingBuilder mappingBuilder = new BeanMappingBuilder() {
				@Override
				protected void configure() {
					// classname of the entity graph
					final Element rootElem = (Element) rootNode.item(0);
					Element classElem = null;

					// checks, if there is a matching class in the mapping rules
					if (classExistsInMappingRules(classList, rootElem.getAttribute("classname")) == null) {
						final String msg = "Error: the classname " + rootElem.getAttribute("classname")
								+ " defined in the entity-graph-tag " + graphName
								+ " has no matching classname in mapping-classes-tag";
						try {
							throw new BuildDozerMapping(msg);
						} catch (final Exception e) {
							LOGGER.warn(
									"Exception in BuildDozerMapping::generateMappingRulesXPath, full stack trace follows: "
											+ msg,
									e);
						}
					} else {
						classElem = classExistsInMappingRules(classList, rootElem.getAttribute("classname"));
					}

					// list of all attributes of the specified class
					String classNodeListString = "//class[@source='" + classElem.getAttribute("source") + "']/node";
					NodeList classNodeList = null;

					try {
						classNodeList = (NodeList) xPath.compile(classNodeListString).evaluate(doc,
								XPathConstants.NODESET);
					} catch (final XPathExpressionException e) {
						LOGGER.warn(
								"XPathExpressionException in BuildDozerMapping::generateMappingRulesXPath, full stack trace follows: ",
								e);
					}

					TypeMappingBuilder builder = mapping(classElem.getAttribute("source"),
							classElem.getAttribute("destination"));

					mapAttributes(builder, attributeRootNodeList, classNodeList);

					// each subgraph
					for (int i = 0; i < subgraphList.getLength(); ++i) {
						final Element subgraphElem = (Element) subgraphList.item(i);

						// list of all attributes of a subgraph
						final String subgraphName = subgraphElem.getAttribute("classname");
						final String attributeSubgraphListString = "//named-entity-graph[@name='" + graphName
								+ "']/subgraph[@classname='" + subgraphName + "']/named-attribute-node";

						classNodeListString = "//class[@source='" + subgraphName + "']/node";

						classNodeList = null;
						NodeList attributeSubgraphList = null;
						try {
							classNodeList = (NodeList) xPath.compile(classNodeListString).evaluate(doc,
									XPathConstants.NODESET);
							attributeSubgraphList = (NodeList) xPath.compile(attributeSubgraphListString).evaluate(doc,
									XPathConstants.NODESET);
						} catch (final XPathExpressionException e) {
							LOGGER.warn(
									"XPathExpressionException in BuildDozerMapping::generateMappingRulesXPath, full stack trace follows: ",
									e);
						}

						if (classExistsInMappingRules(classList, subgraphElem.getAttribute("classname")) == null) {
							final String msg = "Error: no match found for " + subgraphElem.getAttribute("classname")
									+ ". You need to define a class-tag below the mapping-classes!";

							try {
								throw new BuildDozerMapping(msg);
							} catch (final Exception e) {
								LOGGER.warn(
										"Exception in BuildDozerMapping::generateMappingRulesXPath, full stack trace follows: ",
										e);
							}

						} else {
							final Element elem = classExistsInMappingRules(classList,
									subgraphElem.getAttribute("classname"));
							// new mapping with different classes
							builder = mapping(elem.getAttribute("source"), elem.getAttribute("destination"));
							mapAttributes(builder, attributeSubgraphList, classNodeList);
						}
					}
				}
			};
			mapper.addMapping(mappingBuilder);
		} catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
			LOGGER.warn("Exception in BuildDozerMapping::generateMappingRulesXPath, full stack trace follows: ", e);
		}
		return mapper;
	}
}
