/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.contexts;

import java.util.Collections;
import java.util.List;

abstract class AbstractRegistry implements IRegistry {

	protected List contextDefinitions = Collections.EMPTY_LIST;
	
	protected AbstractRegistry() {
		super();
	}

	public List getContextDefinitions() {
		return contextDefinitions;
	}
}	
