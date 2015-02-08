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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import javax.swing.tree.TreeNode;
import ru.apertum.journal.IDocController;
import ru.apertum.journal.model.DocControllersList;
import ru.apertum.journal.model.Document;

/**
 *
 * @author Evgeniy Egorov, Aperum Projects
 */
public class RootNodeDoc extends ADocNode {

    @Override
    public String toString() {
        return "Документы по посещению";
    }
    final private LinkedList<Document> nodes;
    final private HashMap<Long, TypeDocNode> docTypesNodeAll = new HashMap<>();
    final private LinkedList<TreeNode> docTypesNodeVisible = new LinkedList<>();

    @Override
    public LinkedList<TreeNode> getVisibleChildren() {
        return docTypesNodeVisible;
    }

    public RootNodeDoc(LinkedList<Document> nodes) {
        this.nodes = nodes;
        createChildren();
    }

    private void createChildren() {
        for (IDocController blank : DocControllersList.getInstance().getItems()) {
            final TypeDocNode node = new TypeDocNode(blank, this, nodes);
            docTypesNodeAll.put(blank.getId(), node);
            for (Document doc : nodes) {
                if (doc.getDocId().equals(blank.getId())) {
                    docTypesNodeVisible.add(node);
                    break;
                }
            }
        }
    }

    @Override
    public TreeNode getParent() {
        return null;
    }

    public TreeNode addDoc(Document doc) {
        nodes.addFirst(doc);
        docTypesNodeAll.clear();
        docTypesNodeVisible.clear();
        createChildren();
        return docTypesNodeAll.get(doc.getDocId()).getDocNodeByDoc(doc);
    }

    public TreeNode removeDoc(Document doc) {
        nodes.remove(doc);
        docTypesNodeAll.clear();
        docTypesNodeVisible.clear();
        createChildren();
        return docTypesNodeAll.get(doc.getDocId());
    }
}
