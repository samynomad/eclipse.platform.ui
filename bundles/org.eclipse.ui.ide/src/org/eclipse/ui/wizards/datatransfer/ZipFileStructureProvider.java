/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.wizards.datatransfer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

/**
 * This class provides information regarding the context structure and
 * content of specified zip file entry objects.
 */
public class ZipFileStructureProvider implements IImportStructureProvider {
    private ZipFile zipFile;

    private ZipEntry root = new ZipEntry("/");//$NON-NLS-1$

	private Map<ZipEntry, List<ZipEntry>> children;

	private Map<IPath, ZipEntry> directoryEntryCache = new HashMap<>();

    /**
     * Creates a <code>ZipFileStructureProvider</code>, which will operate
     * on the passed zip file.
     *
     * @param sourceFile the zip file used to create this structure provider
     */
    public ZipFileStructureProvider(ZipFile sourceFile) {
        super();
        zipFile = sourceFile;
    }

    /**
     * Adds the specified child to the internal collection of the parent's children.
     */
    protected void addToChildren(ZipEntry parent, ZipEntry child) {
		List<ZipEntry> childList = children.get(parent);
        if (childList == null) {
			childList = new ArrayList<>();
            children.put(parent, childList);
        }

        childList.add(child);
    }

    /**
     * Creates a new container zip entry with the specified name, iff
     * it has not already been created.
     */
    protected void createContainer(IPath pathname) {
        if (directoryEntryCache.containsKey(pathname)) {
			return;
		}

        ZipEntry parent;
        if (pathname.segmentCount() == 1) {
			parent = root;
		} else {
			parent = directoryEntryCache.get(pathname
                    .removeLastSegments(1));
		}

        ZipEntry newEntry = new ZipEntry(pathname.toString());
        directoryEntryCache.put(pathname, newEntry);
        addToChildren(parent, newEntry);
    }

    /**
     * Creates a new file zip entry with the specified name.
     */
    protected void createFile(ZipEntry entry) {
        IPath pathname = new Path(entry.getName());
        ZipEntry parent;
        if (pathname.segmentCount() == 1) {
			parent = root;
		} else {
			parent = directoryEntryCache.get(pathname
                    .removeLastSegments(1));
		}

        addToChildren(parent, entry);
    }

    @Override
	public List getChildren(Object element) {
        if (children == null) {
			initialize();
		}

        return (children.get(element));
    }

    @Override
	public InputStream getContents(Object element) {
        try {
            return zipFile.getInputStream((ZipEntry) element);
        } catch (IOException e) {
        	IDEWorkbenchPlugin.log(e.getLocalizedMessage(), e);
            return null;
        }
    }

    @Override
	public String getFullPath(Object element) {
        return ((ZipEntry) element).getName();
    }

    @Override
	public String getLabel(Object element) {
        if (element.equals(root)) {
			return ((ZipEntry) element).getName();
		}

        return new Path(((ZipEntry) element).getName()).lastSegment();
    }

    /**
     * Returns the entry that this importer uses as the root sentinel.
     *
     * @return java.util.zip.ZipEntry
     */
    public ZipEntry getRoot() {
        return root;
    }

    /**
     * Returns the zip file that this provider provides structure for.
     *
     * @return the zip file this provider provides structure for
     */
    public ZipFile getZipFile() {
        return zipFile;
    }

    /**
     * Initializes this object's children table based on the contents of
     * the specified source file.
     */
    protected void initialize() {
		children = new HashMap<>(1000);

		Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory()) {
                IPath path = new Path(entry.getName()).addTrailingSeparator();
                int pathSegmentCount = path.segmentCount();

                for (int i = 1; i < pathSegmentCount; i++) {
					createContainer(path.uptoSegment(i));
				}
                createFile(entry);
            }
        }
    }

    @Override
	public boolean isFolder(Object element) {
        return ((ZipEntry) element).isDirectory();
    }
}
