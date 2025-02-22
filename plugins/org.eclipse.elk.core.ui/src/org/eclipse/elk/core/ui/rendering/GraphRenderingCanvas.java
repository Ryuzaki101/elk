/*******************************************************************************
 * Copyright (c) 2008, 2016 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.ui.rendering;

import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * A canvas that is able to paint ELK layout graphs. Colors and fonts used for painting can be
 * customized by supplying a subclass of {@link GraphRenderingConfigurator}.
 * 
 * @author msp
 */
public class GraphRenderingCanvas extends Canvas implements PaintListener {
    
    private static final double ZOOM_PER_WHEEL_UNIT = 1.01;

    /** the painted layout graph. */
    private ElkNode layoutGraph;
    /** the graph renderer used for painting. */
    private final GraphRenderer graphRenderer;
    
    /** background color. */
    private Color backgroundColor;

    /**
     * Creates a layout graph canvas using the default drawing style.
     * 
     * @param parent
     *            the parent widget
     */
    public GraphRenderingCanvas(final Composite parent) {
        this(parent, new GraphRenderingConfigurator(parent.getDisplay()));
    }

    /**
     * Creates a layout graph canvas.
     * 
     * @param parent
     *            the parent widget
     * @param configurator
     *            the rendering configurator that defines the colors and fonts used for drawing. The
     *            configurator must have been initialized with the same display this component is
     *            created for.
     * @throws IllegalArgumentException if the displays are not the same.
     */
    public GraphRenderingCanvas(final Composite parent, final GraphRenderingConfigurator configurator) {
        super(parent, SWT.NONE);
        
        if (!configurator.getDisplay().equals(parent.getDisplay())) {
            throw new IllegalArgumentException(
                    "configurator and parent composite must use the same display.");
        }
        
        addPaintListener(this);
        graphRenderer = new GraphRenderer(configurator);
        
        // SUPPRESS CHECKSTYLE NEXT MagicNumber
        backgroundColor = new Color(parent.getDisplay(), 255, 255, 255);
        setBackground(backgroundColor);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        graphRenderer.dispose();
        backgroundColor.dispose();
    }
    
    @Override
    public Point computeSize(final int wHint, final int hHint, final boolean changed) {
        if (layoutGraph == null) {
            return super.computeSize(wHint, hHint, changed);
        }
        checkWidget();
        int width = (int) (layoutGraph.getWidth() + 0.5);   // SUPPRESS CHECKSTYLE MagicNumber
        int height = (int) (layoutGraph.getHeight() + 0.5); // SUPPRESS CHECKSTYLE MagicNumber
        if (wHint > width) {
            width = wHint;
        }
        if (hHint > height) {
            height = hHint;
        }
        int border = getBorderWidth();
        width += border * 2;
        height += border * 2;
        return new Point(width, height);
    }
    
    /**
     * Returns the KGraph renderer used for painting.
     */
    public GraphRenderer getRenderer() {
        return graphRenderer;
    }

    /**
     * Returns the currently painted layout graph.
     */
    public ElkNode getLayoutGraph() {
        return layoutGraph;
    }

    /**
     * Sets the given layout graph as the painted graph.
     * 
     * @param thelayoutGraph layout graph to be painted
     */
    public void setLayoutGraph(final ElkNode thelayoutGraph) {
        this.layoutGraph = thelayoutGraph;
        if (thelayoutGraph != null) {
            setSize(computeSize(SWT.DEFAULT, SWT.DEFAULT));
        }
        redraw();
    }
    
    /**
     * Set up mouse interaction: wheel for zooming, drag for panning.
     */
    public void setupMouseInteraction() {
        // Add a mouse wheel listener for zooming
        addListener(SWT.MouseWheel, event -> {
            double zoom = Math.pow(ZOOM_PER_WHEEL_UNIT, -event.count);
            this.zoom(zoom, new KVector(event.x, event.y));
            this.redraw();
            event.doit = false;
        });
        
        // Add a mouse drag listener for panning
        Listener dragListener = new Listener() {
            private boolean isDragging;
            private int lastX;
            private int lastY;
            @Override
            public void handleEvent(final Event event) {
                switch (event.type) {
                case SWT.MouseDown:
                    isDragging = true;
                    break;
                case SWT.MouseUp:
                    isDragging = false;
                    break;
                case SWT.MouseMove:
                    if (isDragging) {
                        KVector delta = new KVector(event.x - lastX, event.y - lastY);
                        GraphRenderingCanvas.this.pan(delta);
                        GraphRenderingCanvas.this.redraw();
                    }
                    break;
                }
                lastX = event.x;
                lastY = event.y;
            }
        };
        addListener(SWT.MouseDown, dragListener);
        addListener(SWT.MouseUp, dragListener);
        addListener(SWT.MouseMove, dragListener);
    }
    
    /**
     * Zoom the canvas by adjusting the scale value by the given factor.
     */
    protected void zoom(final double zoom, final KVector focusPoint) {
        double newScale = graphRenderer.getScale() * zoom;
        graphRenderer.setScale(newScale);
        // Adjust the offset so the focus point stays the same
        KVector absFocus = KVector.diff(focusPoint, graphRenderer.getBaseOffset());
        graphRenderer.setBaseOffset(absFocus.scale(zoom).sub(focusPoint).negate());
    }
    
    /**
     * Pan the canvas by adjusting the base offset by the given delta.
     */
    protected void pan(final KVector delta) {
        KVector offset = graphRenderer.getBaseOffset().add(delta);
        // Though the offset is modified in place, it needs to be reset in order to flush the cache
        graphRenderer.setBaseOffset(offset);
    }

    /**
     * This method is called when the canvas is requested to paint.
     * 
     * @param event
     *            paint event
     */
    public void paintControl(final PaintEvent event) {
        if (layoutGraph != null) {
            Rectangle area = new Rectangle(event.x, event.y, event.width, event.height);
            // reset paint information
            graphRenderer.markDirty(area);
            // paint the top layout nodes with their children
            graphRenderer.render(layoutGraph, event.gc, area);
        }
    }

}
