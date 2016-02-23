# Java EntityGraph Data Mapper

This Framework is a thesis project with the translated name "Central data mapping definition in Java EE environment".

It is a prototype that combines JPA EntityGraphs with the Object-to-Object mapping framework "Dozer"

# Abstract

Software developers commonly use architectural models to structure a system in logical and sometimes even physical components. Usually each component has its own model of data. To transfer data between two components a set of mapping rules is needed. Depending on the program size and the choice of the architectural model there are a lot of those "mappings". Every single one of them has their own definition of rules.

When using a multilayered architecture you might need to transfer data between more than two layers. This means that the mappings occur in a special order which leads to a dependency on each other.

The main problem with dependent mappings is, that you need to modify each mapping in particular which leads to an additional effort of work. Other problems like a bad tuning between the mappings or an unexpected program crash might occur when changes are not made consequently in all mappings.

A solution for those problems might be the combination of mappings to a central definition of rules. 

This framework implements a possibility of combining two mappings using a three-tier-architecture model. 

# Advantages

Using this framework guarantees 
* for less efford of work, cause changes need just to be made at a single point of code
* a perfect tuning between requested data over an entity graph and the object to object mapping with "Dozer"
* avoidance of exceptions caused of lazy loading

# How to use this framework

Assuming you already have a database and two data models, the next step is to create a xml file with the name "central-rules.xml" that contains all object to object mapping rules and all specified entity graphs. 
After you filled that file correctly you can use the program API to start the framework.

There is an example with customer data implmemented that gets loaded from a H2 database over an entity graph and mapped into the frontend. 
