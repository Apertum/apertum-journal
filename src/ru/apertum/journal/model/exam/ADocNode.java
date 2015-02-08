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
import java.util.LinkedList;
import javax.swing.tree.TreeNode;

/**
 *
 * @author Evgeniy Egorov, Aperum Projects
 */
public abstract class ADocNode implements TreeNode {
    
    abstract LinkedList<TreeNode> getVisibleChildren();
    
    @Override
    public TreeNode getChildAt(int childIndex) {
        return getVisibleChildren().get(childIndex);
    }

    @Override
    public int getChildCount() {
        return getVisibleChildren().size();
    }

    @Override
    public int getIndex(TreeNode node) {
        return getVisibleChildren().indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return getChildCount() == 0;
    }

    @Override
    public Enumeration children() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
