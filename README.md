# Entity-NXT

Entity-NXT is a mini ORM for small Java websites. You can create, update, delete and fetch the data from the entities without going into the technical side of the database. Currently support MySQL.

## Getting Started

You just need to put the Jar file in the lib folder of your web application. You can find the Jar file in the api directory.

## Examples

EntityQuery is the class you need to be familiar about to access the create, update, remove and fetching features of the api.

### To fetch all records from the entity:

```
List<Map<String, Object> result = EntityQuery.getAll("entity_name");
```
### To fetch first record from the entity:

Here, you can pass the parameters in a HashMap with which you want to fetch the reords.

For Example: If you want a unique record, you can pass the primary keys values of the entity. The api will fetch the records corresponding to the primary key.

```
Map<String, Object> getFirstCtx = new HashMap<>();
getFirstCtx.put("column_name", "value");
Map<String, Object> getFirst = EntityQuery.getFirst("entity_name", getFirstCtx);
```

### To insert record in the entity:

```
Map<String, Object> insertMap = new HashMap<>();
insert.put("column_one", "value_one");
insert.put("column_two", "value_two");

Map<String, Object> result = EntityQuery.insert("entity_name", insertMap);
```

### To update record in the entity:

To Update record you need to pass all the primary keys of the entity.

```
Map<String, Object> updateMap = new HashMap<>();
updateMap.put("column_one", "value_one");
updateMap.put("column_two", "value_two");

Map<String, Object> pkMap = new HashMap<>();
pkMap.put("column_one", "value_one");

Map<String, Object> updateResult = EntityQuery.update("entity_name", updateParams, primaryKeyParams);
```
### To remove record from the entity:

To remove a particular record, you need to pass all the primary keys of the entity.

```
Map<String, Object> pkMap = new HashMap<>();
pkMap.put("column_one", "value_one");
pkMap.put("column_two", "value_two");

Map<String, Object> result = EntityQuery.remove("entity_name", pkMap);
```

## Credits:

[gradle-tomcat-plugin](https://github.com/bmuschko/gradle-tomcat-plugin) -- Gradle Tomcat Server used for development purpose.
[Bootstrap 4](https://getbootstrap.com/) -- UI Console developed with bootstrap 4.