/*******************************************************************************
 * Copyright (c) 2010, 2015 Kiel University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kiel University - initial API and implementation
 *******************************************************************************/
package org.eclipse.elk.core.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.elk.core.service.DiagramLayoutEngine;
import org.eclipse.elk.core.service.ElkServicePlugin;
import org.eclipse.elk.core.ui.ElkUiPlugin;
import org.eclipse.elk.core.ui.LayoutHandler;
import org.eclipse.elk.core.ui.views.LayoutViewPart;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Preference initializer for the ELK plugins.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore servicePrefStore = ElkServicePlugin.getInstance().getPreferenceStore();
        IPreferenceStore uiPrefStore = ElkUiPlugin.getInstance().getPreferenceStore();
        
        uiPrefStore.setDefault(LayoutHandler.PREF_ANIMATION, true);
        uiPrefStore.setDefault(LayoutHandler.PREF_ZOOM, false);
        uiPrefStore.setDefault(LayoutHandler.PREF_PROGRESS, false);
        uiPrefStore.setDefault(LayoutViewPart.PREF_CATEGORIES, true);
        uiPrefStore.setDefault(LayoutViewPart.PREF_ADVANCED, false);
        servicePrefStore.setDefault(DiagramLayoutEngine.PREF_DEBUG_STORE, false);
        servicePrefStore.setDefault(DiagramLayoutEngine.PREF_DEBUG_LOGGING, false);
        servicePrefStore.setDefault(DiagramLayoutEngine.PREF_DEBUG_EXEC_TIME, false);
    }

}
