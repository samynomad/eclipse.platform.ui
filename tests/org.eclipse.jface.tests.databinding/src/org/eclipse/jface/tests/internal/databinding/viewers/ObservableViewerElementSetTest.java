/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 215531)
 *     Matthew Hall - bug 213145
 ******************************************************************************/

package org.eclipse.jface.tests.internal.databinding.viewers;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.jface.databinding.conformance.MutableObservableSetContractTest;
import org.eclipse.jface.databinding.conformance.delegate.AbstractObservableCollectionContractDelegate;
import org.eclipse.jface.internal.databinding.viewers.ObservableViewerElementSet;
import org.eclipse.jface.viewers.IElementComparer;

import junit.framework.TestSuite;

public class ObservableViewerElementSetTest {
	public static void addConformanceTest(TestSuite suite) {
		suite.addTest(MutableObservableSetContractTest.suite(new Delegate()));
	}

	private static class Delegate extends
			AbstractObservableCollectionContractDelegate {

		@Override
		public IObservableCollection createObservableCollection(Realm realm,
				int elementCount) {
			ObservableViewerElementSet set = new ObservableViewerElementSet(realm,
					Object.class, new IdentityElementComparer());
			for (int i = 0; i < elementCount; i++)
				set.add(createElement(set));
			return set;
		}

		@Override
		public Object createElement(IObservableCollection collection) {
			return new Object();
		}

		@Override
		public void change(IObservable observable) {
			IObservableSet set = (IObservableSet) observable;
			set.add(createElement(set));
		}

		@Override
		public Object getElementType(IObservableCollection collection) {
			return Object.class;
		}
	}

	private static class IdentityElementComparer implements IElementComparer {
		@Override
		public boolean equals(Object a, Object b) {
			return a == b;
		}

		@Override
		public int hashCode(Object element) {
			return System.identityHashCode(element);
		}
	}
}
