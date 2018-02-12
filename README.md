# Entity-NXT

Entity-NXT is a mini ORM for small Java websites. You can create, update, delete and fetch the data from the entities without going into the technical side of the database. Currently support MySQL.

## Getting Started

You just need to put the Jar file in the lib folder of your web application.

## Examples

EntityQuery is the Class you need to be familiar about to access the create, update, remove and fetching feature of the api.

### To Fetch all records from the entity:

```
List<Map<String, Object> result = EntityQuery.getAll("test_entity");
```
### To Fetch First record from the entity:

```
Map<String, Object> getFirstCtx = new HashMap<>();
getFirstCtx.put("column_name", "value");
Map<String, Object> getFirst = EntityQuery.getFirst("test_entity", getFirstCtx);
```

In the above example you can pass the parameters in a HashMap with which you want to fetch the record with.


