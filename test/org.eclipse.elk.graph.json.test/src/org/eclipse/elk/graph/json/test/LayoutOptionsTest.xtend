/*******************************************************************************
 * Copyright (c) 2017 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.json.test

import org.eclipse.elk.graph.json.ElkGraphJson
import org.junit.Test

import static org.junit.Assert.*
import org.eclipse.elk.core.options.CoreOptions
import org.eclipse.elk.core.options.Direction
import org.eclipse.elk.graph.util.ElkGraphUtil
import com.google.common.base.CharMatcher

/**
 */
class LayoutOptionsTest {
    
    @Test
    def void testImportLayoutOptions() {
        val graph = '''
        {
          "id": "root",
          "layoutOptions": {
            "direction": "DOWN"
          }
        }
        '''
        val root = ElkGraphJson.forGraph(graph).toElk
        assertTrue(root.hasProperty(CoreOptions.DIRECTION))
        assertEquals(Direction.DOWN, root.getProperty(CoreOptions.DIRECTION))        
    }

    @Test
    def void testImportPropertiesLegacy() {
        val graph = '''
        {
          "id": "root",
          "properties": {
            "direction": "DOWN"
          }
        }
        '''
        val root = ElkGraphJson.forGraph(graph).toElk
        assertTrue(root.hasProperty(CoreOptions.DIRECTION))
        assertEquals(Direction.DOWN, root.getProperty(CoreOptions.DIRECTION))        
    }    

    @Test
    def void testImportLayoutOptionsHavePriority() {
        val graph = '''
        {
          "id": "root",
          "layoutOptions": {
            "direction": "UP"
          },
          "properties": {
            "direction": "DOWN"
          }
        }
        '''
        val root = ElkGraphJson.forGraph(graph).toElk
        assertTrue(root.hasProperty(CoreOptions.DIRECTION))
        assertEquals(Direction.UP, root.getProperty(CoreOptions.DIRECTION))        
    } 
 
    @Test
    def void exportLayoutOptions() {
        val root = ElkGraphUtil.createGraph
        root.setProperty(CoreOptions.DIRECTION, Direction.UP)
        
        val json = ElkGraphJson.forGraph(root).toJson
        
        val expected = '''{"id":"n0","layoutOptions":{"direction":"UP"}}'''
        val cleanJson = CharMatcher.whitespace.removeFrom(json)
        
        assertEquals(expected, cleanJson)
    }
    
}