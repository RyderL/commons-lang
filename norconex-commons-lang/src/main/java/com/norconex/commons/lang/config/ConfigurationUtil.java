/* Copyright 2010-2013 Norconex Inc.
 * 
 * This file is part of Norconex Commons Lang.
 * 
 * Norconex Commons Lang is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Norconex Commons Lang is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Norconex Commons Lang. If not, see <http://www.gnu.org/licenses/>.
 */
package com.norconex.commons.lang.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;

/**
 * Utility methods when dealing with configuration files.
 * @author <a href="mailto:pascal.essiembre@norconex.com">Pascal Essiembre</a>
 */
public final class ConfigurationUtil {
    private static final Logger LOG = 
            LogManager.getLogger(ConfigurationUtil.class);

    private ConfigurationUtil() {
        super();
    }
   
    /**
     * Creates a new instance of the class represented by the "class" attribute
     * on the given node.  The class must have an empty constructor.
     * If the class is an instance of {@link IXMLConfigurable}, the object 
     * created will be automatically populated by invoking the 
     * {@link IXMLConfigurable#loadFromXML(Reader)} method, 
     * passing it the node XML automatically populated.
     * @param node the node representing the class to instantiate.
     * @return a new object.
     * @throws ConfigurationException if instance cannot be created/populated
     */
    public static <T extends Object> T newInstance(
            HierarchicalConfiguration node)
            throws ConfigurationException {
        return newInstance(node, null, true);
    }

    /**
     * Creates a new instance of the class represented by the "class" attribute
     * on the given node.  The class must have an empty constructor.
     * If the class is an instance of {@link IXMLConfigurable} and 
     * <code>supportXMLConfigurable</code> is true, the object created
     * will be automatically populated by invoking the 
     * {@link IXMLConfigurable#loadFromXML(Reader)} method, 
     * passing it the node XML automatically populated.
     * @param node the node representing the class to instantiate.
     * @param supportXMLConfigurable automatically populates the object from XML
     *        if it is implementing {@link IXMLConfigurable}.
     * @return a new object.
     * @throws ConfigurationException if instance cannot be created/populated
     */
    public static <T extends Object> T newInstance(
            HierarchicalConfiguration node,
            boolean supportXMLConfigurable)
            throws ConfigurationException {
        return newInstance(node, null, supportXMLConfigurable);
    }
    
    
    /**
     * Creates a new instance of the class represented by the "class" attribute
     * on the given node.  The class must have an empty constructor.
     * If the class is an instance of {@link IXMLConfigurable}, the object 
     * created will be automatically populated by invoking the 
     * {@link IXMLConfigurable#loadFromXML(Reader)} method, 
     * passing it the node XML automatically populated.
     * @param node the node representing the class to instantiate.
     * @param defaultObject if returned object is null or undefined,
     *        returns this default object.
     * @return a new object.
     * @throws ConfigurationException if instance cannot be created/populated
     */
    public static <T extends Object> T newInstance(
            HierarchicalConfiguration node, T defaultObject)
            throws ConfigurationException {
        return newInstance(node, defaultObject, true);
    }
    
    /**
     * Creates a new instance of the class represented by the "class" attribute
     * on the given node.  The class must have an empty constructor.
     * If the class is an instance of {@link IXMLConfigurable} and 
     * <code>supportXMLConfigurable</code> is true, the object created
     * will be automatically populated by invoking the 
     * {@link IXMLConfigurable#loadFromXML(Reader)} method, 
     * passing it the node XML automatically populated.
     * @param node the node representing the class to instantiate.
     * @param defaultObject if returned object is null or undefined,
     *        returns this default object.
     * @param supportXMLConfigurable automatically populates the object from XML
     *        if it is implementing {@link IXMLConfigurable}.
     * @return a new object.
     * @throws ConfigurationException if instance cannot be created/populated
     */
    @SuppressWarnings("unchecked")
    public static <T extends Object> T newInstance(
            HierarchicalConfiguration node, T defaultObject,
            boolean supportXMLConfigurable)
            throws ConfigurationException {
        T obj = null;
        String clazz = null;
        if (node == null) {
            return defaultObject;
        } else {
            clazz = node.getString("[@class]", null);
            if (clazz != null) {
                try {
                    obj = (T) Class.forName(clazz).newInstance();
                } catch (Exception e) {
                    throw new ConfigurationException(
                            "This class could not be instantiated: \""
                            + clazz + "\".", e);
                }
            } else {
                LOG.debug("A configuration entry was found without class "
                       + "reference where one was needed; "
                       + "using default value:" + defaultObject);
                obj = defaultObject;
            }
        }
        if (obj == null) {
            return defaultObject;
        }
        if (obj instanceof IXMLConfigurable) {
            try {
                ((IXMLConfigurable) obj).loadFromXML(newReader(node));
            } catch (IOException e) {
                throw new ConfigurationException(
                        "Could not load new instance from XML \""
                        + clazz + "\".", e);
            }
        }
        return obj;
    }

    /**
     * Creates a new instance of the class represented by the "class" attribute
     * on the sub-node of the node argument, matching the key provided.
     * The class must have an empty constructor.
     * If the class is an instance of {@link IXMLConfigurable}, the object 
     * created will be automatically populated by invoking the 
     * {@link IXMLConfigurable#loadFromXML(Reader)} method, 
     * passing it the node XML automatically populated.
     * @param node the node representing the class to instantiate.
     * @param key sub-node name/hierarchical path
     * @return a new object.
     * @throws ConfigurationException if instance cannot be created/populated
     */
    public static <T extends Object> T newInstance(
            HierarchicalConfiguration node, String key) {
        return newInstance(node, key, null, true);
    }
    /**
     * Creates a new instance of the class represented by the "class" attribute
     * on the sub-node of the node argument, matching the key provided.
     * The class must have an empty constructor.
     * If the class is an instance of {@link IXMLConfigurable} and 
     * <code>supportXMLConfigurable</code> is true, the object created
     * will be automatically populated by invoking the 
     * {@link IXMLConfigurable#loadFromXML(Reader)} method, 
     * passing it the node XML automatically populated.
     * @param node the node representing the class to instantiate.
     * @param key sub-node name/hierarchical path
     * @param supportXMLConfigurable automatically populates the object from XML
     *        if it is implementing {@link IXMLConfigurable}.
     * @return a new object.
     * @throws ConfigurationException if instance cannot be created/populated
     */
    public static <T extends Object> T newInstance(
            HierarchicalConfiguration node, String key, 
            boolean supportXMLConfigurable) {
        return newInstance(node, key, null, supportXMLConfigurable);
    }
    /**
     * Creates a new instance of the class represented by the "class" attribute
     * on the sub-node of the node argument, matching the key provided.
     * The class must have an empty constructor.
     * If the class is an instance of {@link IXMLConfigurable}, the object 
     * created will be automatically populated by invoking the 
     * {@link IXMLConfigurable#loadFromXML(Reader)} method, 
     * passing it the node XML automatically populated.
     * @param node the node representing the class to instantiate.
     * @param defaultObject if returned object is null or undefined,
     *        returns this default object.
     * @param key sub-node name/hierarchical path
     * @return a new object.
     * @throws ConfigurationException if instance cannot be created/populated
     */
    public static <T extends Object> T newInstance(
            HierarchicalConfiguration node, String key, 
            T defaultObject) {
        return newInstance(node, key, defaultObject, true);
    }
    /**
     * Creates a new instance of the class represented by the "class" attribute
     * on the sub-node of the node argument, matching the key provided.
     * The class must have an empty constructor.
     * If the class is an instance of {@link IXMLConfigurable} and 
     * <code>supportXMLConfigurable</code> is true, the object created
     * will be automatically populated by invoking the 
     * {@link IXMLConfigurable#loadFromXML(Reader)} method, 
     * passing it the node XML automatically populated.
     * @param node the node representing the class to instantiate.
     * @param defaultObject if returned object is null or undefined,
     *        returns this default object.
     * @param key sub-node name/hierarchical path
     * @param supportXMLConfigurable automatically populates the object from XML
     *        if it is implementing {@link IXMLConfigurable}.
     * @return a new object.
     * @throws ConfigurationException if instance cannot be created/populated
     */
    public static <T extends Object> T newInstance(
            HierarchicalConfiguration node, String key, 
            T defaultObject, boolean supportXMLConfigurable) {
        if (node == null) {
            return defaultObject;
        }
        
        try {
            HierarchicalConfiguration subconfig = node.configurationAt(key);
            return ConfigurationUtil.newInstance(
                    subconfig, defaultObject, supportXMLConfigurable);
        } catch (Exception e) {
            return defaultObject;
        }
    }
    
    
    /**
     * Creates a new {@link Reader} from a {@link XMLConfiguration}.
     * Do not forget to close the reader instance when you are done with it.
     * @param node the xml configuration to convert to a reader instance.
     * @return reader
     * @throws ConfigurationException cannot read configuration
     * @throws IOException cannot read configuration
     */
    public static Reader newReader(HierarchicalConfiguration node)
            throws ConfigurationException, IOException {
        XMLConfiguration xml;
        if (node != null && node instanceof XMLConfiguration) {
            xml = (XMLConfiguration) node;
        } else {
            xml = new XMLConfiguration(node);
            xml.setDelimiterParsingDisabled(true);
        }
        StringWriter w = new StringWriter();
        try {
            xml.save(w);
        } catch (org.apache.commons.configuration.ConfigurationException e) {
            throw new ConfigurationException(
                    "Could transform XML node to reader.", e);
        }
        StringReader r = new StringReader(w.toString());
        w.close();
        return r;
    }
    
    /**
     * This method is the same as 
     * {@link HierarchicalConfiguration#configurationAt(String)}, except that
     * it first checks if the key exists before attempting to retrieve it, 
     * and returns <code>null</code> on missing keys instead of an 
     * <code>IllegalArgumentException</code>
     * @param node the tree to extract a sub tree from
     * @param key the key that selects the sub tree
     * @return a XML configuration that contains this sub tree
     */
    public static XMLConfiguration getXmlAt(
            HierarchicalConfiguration node, String key) {
        if (node == null || !node.containsKey(key)) {
            return null;
        }
        return new XMLConfiguration(node.configurationAt(key));
    }
    
    /**
     * Convenience class for testing that a {@link IXMLConfigurable} instance
     * can be written, and read into an new instance that is equal as per
     * {@link #equals(Object)}.
     * @param xmlConfiurable the instance to test if it writes/read properly
     * @throws IOException Cannot read/write
     * @throws ConfigurationException Cannot load configuration
     */
    public static void assertWriteRead(IXMLConfigurable xmlConfiurable)
            throws IOException, ConfigurationException {
        
        File tempFile = File.createTempFile("XMLConfigurableTester", ".xml");
        
        // Write
        FileWriter out = new FileWriter(tempFile);
        xmlConfiurable.saveToXML(out);
        out.close();
        
        // Read
        XMLConfiguration xml = new ConfigurationLoader().loadXML(tempFile);
        IXMLConfigurable readConfigurable = 
                (IXMLConfigurable) ConfigurationUtil.newInstance(xml);

        tempFile.delete();

        Assert.assertEquals("Saved and loaded XML are not the same.", 
                xmlConfiurable, readConfigurable);
    }
}
