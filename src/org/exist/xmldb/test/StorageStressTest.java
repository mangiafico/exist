/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2001-04 The eXist Project
 *  http://exist-db.org
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *  
 *  $Id$
 */
package org.exist.xmldb.test;

import java.io.File;

import org.exist.xmldb.RemoteCollection;
import org.exist.xmldb.test.concurrent.DBUtils;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

import junit.framework.TestCase;
import junit.textui.TestRunner;

public class StorageStressTest extends TestCase {

//    protected final static String URI = "xmldb:exist://localhost:8088/xmlrpc";
    
    protected final static String URI = "xmldb:exist://";
    
    public final static String DB_DRIVER = "org.exist.xmldb.DatabaseImpl";

    private final static String COLLECTION_NAME = "unit-testing-collection";
    
    private Collection collection = null;
    
    public void testStore() throws Exception {
        String[] wordList = DBUtils.wordList(collection);
        for (int i = 0; i < 1250; i++) {
            File f = DBUtils.generateXMLFile(50, 1, wordList, false);
            System.out.println("Storing file: " + f.getName() + "; size: " + (f.length() / 1024) + "kB");
            XMLResource res = (XMLResource)
                collection.createResource("test_" + i, "XMLResource");
            res.setContent(f);
            collection.storeResource(res);
            f.delete();
        }
    }
    
    protected void setUp() throws Exception {
        setUpRemoteDatabase();
    }
    
    protected void setUpRemoteDatabase() throws Exception, ClassNotFoundException, InstantiationException,
    IllegalAccessException, XMLDBException {
        Class cl = Class.forName(DB_DRIVER);
        Database database = (Database) cl.newInstance();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);
        
        Collection rootCollection = DatabaseManager.getCollection(URI + "/db", "admin", null);
        
        Collection childCollection = rootCollection.getChildCollection(COLLECTION_NAME);
        if (childCollection == null) {
            CollectionManagementService cms = (CollectionManagementService) rootCollection.getService(
                    "CollectionManagementService", "1.0");
            this.collection = cms.createCollection(COLLECTION_NAME);
        } else {
            this.collection = childCollection;
        }
        
        File f = new File("samples/shakespeare/hamlet.xml");
        Resource res = (Resource) collection.createResource("test.xml", "XMLResource");
        res.setContent(f);
        collection.storeResource(res);
    }
    
    public static void main(String[] args) {
        TestRunner.run(StorageStressTest.class);
    }
}
