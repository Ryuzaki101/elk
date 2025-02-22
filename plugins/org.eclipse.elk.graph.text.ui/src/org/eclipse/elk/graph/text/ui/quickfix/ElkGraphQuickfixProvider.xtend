/*******************************************************************************
 * Copyright (c) 2016, 2019 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.elk.graph.text.ui.quickfix

import org.eclipse.elk.graph.text.validation.IssueCodes
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.xtext.ui.editor.quickfix.DefaultQuickfixProvider
import org.eclipse.xtext.ui.editor.quickfix.Fix
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor
import org.eclipse.xtext.validation.Issue

/**
 * Custom quickfixes.
 * 
 * See https://www.eclipse.org/Xtext/documentation/310_eclipse_support.html#quick-fixes
 */
class ElkGraphQuickfixProvider extends DefaultQuickfixProvider {
    
	@Fix(IssueCodes.OPTION_NOT_APPLICABLE)
	def removeOption(Issue issue, IssueResolutionAcceptor acceptor) {
		acceptor.accept(issue, 'Remove option', 'Remove this layout option.', null) [ element, context |
			EcoreUtil.remove(element)
		]
	}
	
}
