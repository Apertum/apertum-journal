/*
 *  Copyright (C) 2015 Apertum{Projects}. web: http://apertum.ru Е-mail:  info@apertum.ru
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.apertum.journal.model.exam;

import java.util.Enumeration;
import javax.swing.tree.TreeNode;
import ru.apertum.journal.model.Document;

/**
 *
 * @author Evgeniy Egorov, Aperum Projects
 */
public class DocNode implements TreeNode {

    @Override
    public String toString() {
        return doc.toString();
    }
    final private Document doc;

    public Document getDoc() {
        return doc;
    }
    final private TypeDocNode parent;

    public DocNode(Document doc, TypeDocNode parent) {
        this.doc = doc;
        this.parent = parent;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return null;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        return -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public Enumeration children() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
