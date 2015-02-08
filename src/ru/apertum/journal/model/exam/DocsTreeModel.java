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

import java.util.LinkedList;
import javax.swing.tree.DefaultTreeModel;
import ru.apertum.journal.model.Document;
import ru.apertum.journal.model.Visit;
import ru.apertum.qsystem.common.QLog;

/**
 *
 * @author Evgeniy Egorov, Aperum Projects
 */
public class DocsTreeModel extends DefaultTreeModel {
    
    final private Visit visit;
    //final private LinkedList<Document> nodes;

    public DocsTreeModel(Visit visit) {
        super(null, true);
        
        this.visit = visit;
        //this.nodes = visit.getDocs();
        //this.nodes = Document.getDocs(visit);
        //setRoot(new RootNodeDoc(nodes));
        final LinkedList<Document> docs = new LinkedList<>();
        visit.getDocs().stream().forEach((doc) -> {
            docs.addLast(doc);
        });
        setRoot(new RootNodeDoc(docs));
        QLog.l().logger().info("Создали дерево обследований.");
    }
}
