# Setting up Neo4j database
The entire database can be downloaded ready-for-use here: https://mega.nz/#!Tl4TDYLI!wCv-KZIUWhRdHhm8N6a4P039h_XW5EZYyjMs61Ogmhs  
Username: neo4j  
Password: 1234

When Neo4j boots it asks the location of the database, simply move the folder to that location. By default on Windows this is: `C:\Users\<User>\Documents\Neo4j`  
Neo4j 3.0.1 community edition can be downloaded straight from their own site. Further setup is not required.

Alternatively, continue reading for the full guide on setting up the Neo4j database from scratch.

### Step 1: Extracting PostgreSQL data
Extracted using standard copy to CSV commands, listed below (of course, paths will need to be adjusted based on OS):

```
COPY acted_in TO 'C:\acted_in.csv' WITH (FORMAT CSV, HEADER);
COPY actors TO 'C:\actors.csv' WITH (FORMAT CSV, HEADER);
COPY aka_names TO 'C:\aka_names.csv' WITH (FORMAT CSV, HEADER);
COPY aka_titles TO 'C:\aka_titles.csv' WITH (FORMAT CSV, HEADER);
COPY genres TO 'C:\genres.csv' WITH (FORMAT CSV, HEADER);
COPY keywords TO 'C:\keywords.csv' WITH (FORMAT CSV, HEADER);
COPY movies TO 'C:\movies.csv' WITH (FORMAT CSV, HEADER);
COPY movies_genres TO 'C:\movies_genres.csv' WITH (FORMAT CSV, HEADER);
COPY movies_keywords TO 'C:\movies_keywords.csv' WITH (FORMAT CSV, HEADER);
COPY series TO 'C:\series.csv' WITH (FORMAT CSV, HEADER);
```

These files should then be moved into the `import` folder in the Neo4j database location, which should be created manually if needed (Neo4j's default setting is to only read from this folder).

### Step 2: Importing data into Neo4j
Fairly straight-forward, we import using `PERIODIC COMMIT` statements (note that the number can be left out to let it decide itself when to commit).  
Important to not forget is to `toInt` or `toFloat` statements, forgetting them means the database is extremely unoptimized as it will import everything as strings.  
Additionally, one thing you'll notice is that the `acted_in`, `movies_genres` and `movies_keywords` tables aren't imported. These will be represented entirely as relations, not as nodes. More details on this in the final step.  
All import statements are listed below:

```
USING PERIODIC COMMIT 1000 LOAD CSV WITH HEADERS FROM "file:///C:\\actors.csv" AS row CREATE (:actors{
idactors:toInt(row.idactors),
lname:row.lname,
fname:row.fname,
mname:row.mname,
gender:toInt(row.gender),
number:toInt(row.number)
});

USING PERIODIC COMMIT 1000 LOAD CSV WITH HEADERS FROM "file:///C:\\aka_names.csv" AS row CREATE (:aka_names{
idaka_names:toInt(row.idaka_names),
name:row.name
});

USING PERIODIC COMMIT 1000 LOAD CSV WITH HEADERS FROM "file:///C:\\aka_titles.csv" AS row CREATE (:aka_titles{
idaka_titles:toInt(row.idaka_titles),
title:row.title,
location:row.location,
year:toInt(row.year)
});

USING PERIODIC COMMIT 1000 LOAD CSV WITH HEADERS FROM "file:///C:\\genres.csv" AS row CREATE (:genres{
idgenres:toInt(row.idgenres),
genre:row.genre
});

USING PERIODIC COMMIT 1000 LOAD CSV WITH HEADERS FROM "file:///C:\\keywords.csv" AS row CREATE (:keywords{
idkeywords:toInt(row.idkeywords),
keyword:row.keyword
});

USING PERIODIC COMMIT 1000 LOAD CSV WITH HEADERS FROM "file:///C:\\movies.csv" AS row CREATE (:movies{
idmovies:toInt(row.idmovies),
title:row.title,
year:toInt(row.year),
number:toInt(row.number),
type:toInt(row.type),
location:row.location,
language:row.language
});

USING PERIODIC COMMIT 1000 LOAD CSV WITH HEADERS FROM "file:///C:\\series.csv" AS row CREATE (:series{
idseries:toInt(row.idseries),
name:row.name,
season:toInt(row.season),
number:toInt(row.number)
});
```

### Step 3: Setting up indices
Next, in order to speed up the database we set up indices.  Indices are only set up for the primary indices of the SQL tables. This is because things such as the foreign keys of the tables will be set up using relationships.  
The indices/constraints set up are listed below:

```
CREATE CONSTRAINT ON (t:actors) ASSERT t.idactors IS UNIQUE;
CREATE CONSTRAINT ON (t:aka_names) ASSERT t.idaka_names IS UNIQUE;
CREATE CONSTRAINT ON (t:aka_titles) ASSERT t.idaka_titles IS UNIQUE;
CREATE CONSTRAINT ON (t:genres) ASSERT t.idgenres IS UNIQUE;
CREATE CONSTRAINT ON (t:keywords) ASSERT t.idkeywords IS UNIQUE;
CREATE CONSTRAINT ON (t:movies) ASSERT t.idmovies IS UNIQUE;
CREATE CONSTRAINT ON (t:series) ASSERT t.idseries IS UNIQUE;
```

### Step 4: Creating the relationships
Relationships are created using the CSV files as in-between file. This is because of two reasons.  
Firstly, because the foreign keys weren't stored within the Neo4j nodes, this is because after relationships have been created, we will never need them. Therefore, we use the CSV files which contain these foreign keys to set up the relationships instead of storing the useless data in Neo4j.  
Secondly, because Neo4j loads the actions of the entire operation into RAM before executing it, when dealing with the `acted_in` table/relationships the RAM will be insufficient and the query will get stuck.  
The queries required to create the relationships are listed below:

```
USING PERIODIC COMMIT LOAD CSV WITH HEADERS FROM "file:///C:\\acted_in.csv" AS row
MATCH (a:actors {idactors:toInt(row.idactors)})
MATCH (b:movies {idmovies:toInt(row.idmovies)})
MERGE (a)-[:ACTED_CHARACTER {
character:coalesce(row.character, "N/A"),
billing_position:coalesce(toInt(row.billing_position), 0)
}]->(b);

USING PERIODIC COMMIT LOAD CSV WITH HEADERS FROM "file:///C:\\acted_in.csv" AS row
MATCH (a:actors {idactors:toInt(row.idactors)})
MATCH (b:series {idseries:toInt(row.idseries)})
MERGE (a)-[:ACTED_CHARACTER {
character:coalesce(row.character, "N/A"),
billing_position:coalesce(toInt(row.billing_position), 0)
}]->(b);

USING PERIODIC COMMIT LOAD CSV WITH HEADERS FROM "file:///C:\\series.csv" AS row
MATCH (a:movies {idmovies:toInt(row.idmovies)})
MATCH (b:series {idseries:toInt(row.idseries)})
MERGE (a)-[:RELATED_TO_SERIES]->(b);

USING PERIODIC COMMIT LOAD CSV WITH HEADERS FROM "file:///C:\\aka_titles.csv" AS row
MATCH (a:movies {idmovies:toInt(row.idmovies)})
MATCH (b:aka_titles {idaka_titles:toInt(row.idaka_titles)})
MERGE (a)-[:ALSO_KNOWN_AS]->(b);

USING PERIODIC COMMIT LOAD CSV WITH HEADERS FROM "file:///C:\\aka_names.csv" AS row
MATCH (a:actors {idactors:toInt(row.idactors)})
MATCH (b:aka_names {idaka_names:toInt(row.idaka_names)})
MERGE (a)-[:NICKNAME]->(b);

USING PERIODIC COMMIT LOAD CSV WITH HEADERS FROM "file:///C:\\movies_genres.csv" AS row
MATCH (a:movies {idmovies:toInt(row.idmovies)})
MATCH (b:genres {idgenres:toInt(row.idgenres)})
MERGE (a)-[:MOVIE_GENRE]->(b);

USING PERIODIC COMMIT LOAD CSV WITH HEADERS FROM "file:///C:\\movies_keywords.csv" AS row
MATCH (a:movies {idmovies:toInt(row.idmovies)})
MATCH (b:keywords {idkeywords:toInt(row.idkeywords)})
MERGE (a)-[:MOVIE_KEYWORD]->(b);
```

And you're done! All data and relationships are now successfuly imported.