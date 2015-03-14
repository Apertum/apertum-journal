
### Flexible CRM for managing clients, visits and documents. ###

### Form the list of documents for using by plugins. ###

### Organize files for documents, events and persons. ###

The Journal CRM is useful to anyone who needs to keep and manage by clients and their visits. The Journal CRM has a flexible and personalized set of documents for use during the visit.

[![](http://qsystem.info/images/stories/journal_forms-en.png)](http://qsystem.info/index.php/en/)

---

## What is a Journal CRM. ##

Journal CRM is app to keep of customers list and his documents. Customers or clients or some kind of visitors does not matter. We will call all registered persons as clients. Journal maintains a list. Set of properties for client depends on the editor plugin. The editor is a plugin. Any plugin is connected by placing it in a folder plugins. If you do not have any plug-in editor, default client can use standard attributes such as name, date of birth and a comment. Further, all of our clients come to us regularly for some cases. For each parish in the Journal we register visit. The visit also has several options available for filling. During the visit, we create documents. By default, unless you have your own documents as plugins, you can use just one type of document, which specifies the number of the document and text comment. A set of documents can be extended by plugins. In the new document, you can specify the options that you require. A document can be printed or exported. All these details are  determine within the plug-ins. For developing of plugins you should contact the developers of the Journal at info@apertum.ru

Also to customer records you can attach files such as scanned copies of documents. The same can be applied to the files of each visit to each document. You can rid yourself of the necessity to keep everything in paper form or in files.


---

## Journal CRM and QMS QSystem ##

The Journal can be used as a standalone application for clients management. Also Journal can be used in conjunction with QMS QSystem. The Journal is connected to the QMS as a plugin. Just put Journal.jar in the folder  < qsystem >/plugins/ on operator workplace. Just need a configuration file journal.properties for database connection, which should be placed in the folder < qsystem >/config/. In this case you still can use the Journal as a standalone application. A QSystem operator after calling the next visitor can open Journal by clicking mouse on the number of invited client. And if invited client typed some info before queuing, then these data fall into Journal  filter for quick find the visitor in the Journal.  Also in the Journal will appear a button  to jump into the program operator in QSystem.

The Journal can be configure to the same database as the QMS QSystem. In this case, it will be easier to create backups. Journal as well as QSystem can be used with MySQL or H2.


---

## How to download ##

Journal CRM is a part of QMS QSystem since 1.4.x version. But you can download more new version.

[download](http://goo.gl/aQxLkB)


---

## Get start ##

Administration is not required. For starting you need Java 8 at all. Download, launch. Interface and functionality is simply elementary. Immediately add the new visitor. A fairly simple operation. Press the corresponding button - adds a new client, fill out the form. Sort, filter, edit. It's only three buttons - is not possible to get lost.


---

## Visits of clients ##

Do you have a list of clients. Each visitor has a set of visits to you. Adding a new visit is as simple as a new client. Just press one button.


---

## Sets of documents ##

If visiting in all cases is a simply fact, a set of documents for this visit can be completely different. Set of documents depends on a company and a business processes. Document in the Journal is plugin. The plugin can be implemented absolutely different, it is always an individual approach to a user. In principle, the document can printed and exported as you want. Enough place a plugin to folder and all. By the way, we must remember that Java8 must be installed.



---

## Configuring Journal CRM ##

Configuring Journal only need to connect to the database. The configuration file < journal >/config/journal.properties. If you did not configure access, default config will be used H2 database and create a database file in a folder of current user. It happens that the security prohibits the creation of files there that will cause failure of the Journal. In < journal >/config/journal.properties specify options for MySQL or H2. If you are using H2, then the database will be created completely automatically on its specified parameters. If you are using MySQL, you'll need to install MySQL on your computer, start the database with some name, create a user and give him the rights to your database for the Journal. Then specify the settings for your new and existing database to the file settings. You can use an existing database in MySQL, such as a database for QMS QSystem. In this case, specify the settings for base of the QMS. In  this case a structure for the Journal database will be created automatically as well.
