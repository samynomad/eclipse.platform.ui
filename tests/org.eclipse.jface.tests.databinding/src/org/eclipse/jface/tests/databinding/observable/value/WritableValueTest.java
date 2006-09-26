/*******************************************************************************
 * Copyright (c) 2006 Brad Reynolds
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brad Reynolds - bug 158687
 ******************************************************************************/

package org.eclipse.jface.tests.databinding.observable.value;

import junit.framework.TestCase;

import org.eclipse.jface.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.observable.value.IValueChangeListener;
import org.eclipse.jface.databinding.observable.value.ValueDiff;
import org.eclipse.jface.databinding.observable.value.WritableValue;

/**
 * @since 3.2
 */
public class WritableValueTest extends TestCase {
    /**
     * Asserts that ValueChange events are only fired when the value changes.
     * 
     * @throws Exception
     */
    public void testValueChangeOnlyFiresOnChange() throws Exception {
        WritableValue writableValue = new WritableValue(null);
        ValueChangeCounter counter = new ValueChangeCounter();
        writableValue.addValueChangeListener(counter);
        
        assertEquals(0, counter.count);
        //set same
        writableValue.setValue(null);
        assertEquals(0, counter.count);
        
        //set different
        writableValue.setValue("value");
        assertEquals(1, counter.count);
        
        //set same
        writableValue.setValue("value");
        assertEquals(1, counter.count);
        
        //set different
        writableValue.setValue(null);
        assertEquals(2, counter.count);
    }
    
    private static class ValueChangeCounter implements IValueChangeListener {
        int count;

        public void handleValueChange(IObservableValue source, ValueDiff diff) {
            count++;
        }
    }
}
