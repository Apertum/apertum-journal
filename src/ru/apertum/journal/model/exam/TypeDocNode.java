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
import ru.apertum.journal.IDocController;
import ru.apertum.journal.model.Document;
import ru.apertum.qsystem.common.exceptions.ServerException;

/**
 *
 * @author Evgeniy Egorov, Aperum Projects
 */
public class TypeDocNode extends ADocNode {

    @Override
    public String toString() {
        return blank.getId() + " " + blank.getName() + " " + blank.getDocDescription();
    }
    final private IDocController blank;

    public TypeDocNode(IDocController blank, RootNodeDoc root, LinkedList<Document> nodes) {
        this.blank = blank;
        this.parent = root;
        this.nodes = nodes;
        nodes.stream().filter((doc) -> (doc.getDocId().equals(blank.getId()))).forEach((doc) -> {
            final DocNode dn = new DocNode(doc, this);
            docs.add(dn);
            docsVisible.add(dn);
        });
    }

    final private RootNodeDoc parent;
    final private LinkedList<Document> nodes;
    final private LinkedList<DocNode> docs = new LinkedList<>();
    final private LinkedList<TreeNode> docsVisible = new LinkedList<>();

    @Override
    public TreeNode getChildAt(int childIndex) {
        return docs.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return docs.size();
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        return docs.indexOf(node);
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

    public DocNode getDocNodeByDoc(Document doc) {
        for (DocNode node : docs) {
            if (node.getDoc().getId().equals(doc.getId())) {
                return node;
            }
        }
        throw new ServerException("Н нашли обследование в листах дерева.");
    }

    @Override
    LinkedList<TreeNode> getVisibleChildren() {
        return docsVisible;
    }

}
