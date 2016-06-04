# Setting up Neo4j database
The entire database can be downloaded ready-for-use here: https://mega.nz/#!axhlDaCb!xT_UGlCu4ZTbRgHS9ebzRldgk_oSgwR6RNnENSu4_FQ

When Neo4j boots it asks the location of the database, simply move the folder to that location. By default on Windows this is: `C:\Users\<User>\Documents\Neo4j`

Neo4j 3.0.1 community edition can be downloaded straight from their own site. Further setup is not required.

Setting up the database from scratch is detailed below.

### Step 1: Extracting PostgreSQL data
Extracted using standard copy into csv commands, listed below (paths will need to be adjusted based on OS):

```
COPY acted_in TO 'C:\acted_in.csv' WITH (FORMAT CSV, HEADER);
COPY actors TO 'C:\actors.csv' WITH (FORMAT CSV, HEADER);
COPY aka_names TO 'C:\aka_names.csv' WITH (FORMAT CSV, HEADER);
COPY aka_titles TO 'C:\aka_titles.csv' WITH (FORMAT CSV, HEADER);
COPY genres TO 'C:\genres.csv' WITH (FORMAT CSV, HEADER);
COPY keywords TO 'C:\keywords.csv' WITH (FORMAT CSV, HEADER);
COPY movies TO 'C:\movies.csv' WITH (FORMAT CSV, HEADER);
COPY movies_genres TO 'C:\movies_genres.csv'  WITH (FORMAT CSV, HEADER);
COPY movies_keywords TO 'C:\movies_keywords.csv' WITH (FORMAT CSV, HEADER);
COPY series TO 'C:\series.csv' WITH (FORMAT CSV, HEADER);
```

### Step 2: Importing data into Neo4j
Fairly straight-forward, we import everything using `PERIODIC COMMIT` statements (note that the number can be left out to let it decide itself when to commit).  
Important to not forget is to `toInt` or `toFloat` statements, forgetting them means the database is extremely unoptimized as it will import everything as strings.  
All import statements are listed below:

```
USING PERIODIC COMMIT 1000 LOAD CSV WITH HEADERS FROM "file:///C:\\acted_in.csv" AS row CREATE (:acted_in{
idacted_in:toInt(row.idacted_in),
idmovies:toInt(row.idmovies),
idseries:toInt(row.idseries),
idactors:toInt(row.idactors),
character:toInt(row.character),
billing_position:toInt(row.billing_position)
});

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
idactors:toInt(row.idactors),
name:row.name
});

USING PERIODIC COMMIT 1000 LOAD CSV WITH HEADERS FROM "file:///C:\\aka_titles.csv" AS row CREATE (:aka_titles{
idaka_titles:toInt(row.idaka_titles),
idmovies:toInt(row.idmovies),
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

USING PERIODIC COMMIT 1000 LOAD CSV WITH HEADERS FROM "file:///C:\\movies_genres.csv" AS row CREATE (:movies_genres{
idmovies_genres:toInt(row.idmovies_genres),
idmovies:toInt(row.idmovies),
idgenres:toInt(row.idgenres)
});

USING PERIODIC COMMIT 1000 LOAD CSV WITH HEADERS FROM "file:///C:\\movies_keywords.csv" AS row CREATE (:movies_keywords{
idmovies_keywords:toInt(row.idmovies_keywords),
idmovies:toInt(row.idmovies),
idkeywords:toInt(row.idkeywords)
});

USING PERIODIC COMMIT 1000 LOAD CSV WITH HEADERS FROM "file:///C:\\series.csv" AS row CREATE (:series{
idseries:toInt(row.idseries),
idmovies:toInt(row.idmovies),
name:row.name,
season:toInt(row.season),
number:toInt(row.number)
});
```

### Step 3: Setting up indices
Next, in order to speed up the database we set up indices.  Indices were, in essence, set up for anything which is an index in any table.
Note that the statement will often complete within a second, but the database will use up a significant amount of CPU in the background as it creates the index.
Furthermore, anything set to `UNIQUE` automatically has an index made for it.

```
CREATE CONSTRAINT ON (t:idacted_in) ASSERT t.idacted_in IS UNIQUE;
CREATE CONSTRAINT ON (t:actors) ASSERT t.idactors IS UNIQUE;
CREATE CONSTRAINT ON (t:aka_names) ASSERT t.idaka_names IS UNIQUE;
CREATE CONSTRAINT ON (t:aka_titles) ASSERT t.idaka_titles IS UNIQUE;
CREATE CONSTRAINT ON (t:genres) ASSERT t.idgenres IS UNIQUE;
CREATE CONSTRAINT ON (t:keywords) ASSERT t.idkeywords IS UNIQUE;
CREATE CONSTRAINT ON (t:movies) ASSERT t.idmovies IS UNIQUE;
CREATE CONSTRAINT ON (t:movies_genres) ASSERT t.idmovies_genres IS UNIQUE;
CREATE CONSTRAINT ON (t:movies_keywords) ASSERT t.idmovies_keywords IS UNIQUE;
CREATE CONSTRAINT ON (t:series) ASSERT t.idseries IS UNIQUE;
```

```
CREATE INDEX ON :acted_in(idactors);
CREATE INDEX ON :acted_in(idmovies);
CREATE INDEX ON :acted_in(idseries);
CREATE INDEX ON :aka_names(idactors);
CREATE INDEX ON :aka_titles(idmovies);
CREATE INDEX ON :movies_genres(idmovies);
CREATE INDEX ON :movies_genres(idgenres);
CREATE INDEX ON :movies_genres(idseries);
CREATE INDEX ON :movies_keywords(idmovies);
CREATE INDEX ON :movies_keywords(idkeywords);
CREATE INDEX ON :movies_keywords(idseries);
CREATE INDEX ON :series(idmovies);
```

### Step 4: Creating the relationships
Neo4j nodes require relationships to other nodes to be efficient. Note that Neo4j loads any statement's actions completely into memory before executing them, in general this means that Neo4j requires several GB of RAM to execute a statement like this. My own PC was able to manage most of these without needing work-arounds, but weaker PC's might need to use the work around for more of them. The last 3, in which workarounds were used, require *at least* over 12 GB of RAM.

The following 7 relationships were made on RAM, each relationship is expected to take between 30 seconds and 5 minutes.

```
MATCH (a:movies), (b:aka_titles)
WHERE a.idmovies=b.idmovies
CREATE (a)-[x:ALSO_KNOWN_AS]->(b);

MATCH (a:movies), (b:series)
WHERE a.idmovies=b.idmovies
CREATE (a)-[:ADAPTED_AS_SERIES]->(b);

USING PERIODIC COMMIT 1000 MATCH (a:acted_in), (b:movies)
WHERE a.idmovies=b.idmovies
CREATE (a)-[:CHARACTER_IN_MOVIE]->(b);

MATCH (a:acted_in), (b:series)
WHERE a.idseries=b.idseries
CREATE (a)-[:CHARACTER_IN_SERIES]->(b);

MATCH (a:actors), (b:acted_in)
WHERE a.idactors=b.idactors
CREATE (a)-[:AS_CHARACTER]->(b);

MATCH (a:actors), (b:aka_names)
WHERE a.idactors=b.idactors
CREATE (a)-[:NICKNAME]->(b);

MATCH (a:movies), (b:movies_genres)
WHERE a.idmovies=b.idmovies
CREATE (a)-[:BELONGS_TO_GENRE]->(b);

MATCH (a:movies_genres), (b:genres)
WHERE a.idgenres=b.idgenres
CREATE (a)-[:GENRE_DEFINITION]->(b);

MATCH (a:movies), (b:movies_keywords)
WHERE a.idmovies=b.idmovies
CREATE (a)-[:HAS_KEYWORDS]->(b);

MATCH (a:movies_keywords), (b:keywords)
WHERE a.idkeywords=b.idkeywords
CREATE (a)-[:KEYWORD_DEFINITION]->(b);
```

Finally, these 3 relationships were made outside of RAM, by using a CSV as a way to store part of the query outside of RAM and allow usage of `PERIODIC COMMIT`. This time, no number was used and Neo4j was instead allowed to pick itself when to commit. Note that these queries are expected to take a minimum of 20 minutes and likely more.

```
USING PERIODIC COMMIT LOAD CSV WITH HEADERS FROM "file:///C:\\movies.csv" AS row
MATCH (a:acted_in {idmovies:toInt(row.idmovies)})
MATCH (b:movies {idmovies:toInt(row.idmovies)})
MERGE (a)-[:CHARACTER_IN_MOVIE]->(b);

USING PERIODIC COMMIT LOAD CSV WITH HEADERS FROM "file:///C:\\series.csv" AS row
MATCH (a:acted_in {idseries:toInt(row.idseries)})
MATCH (b:series {idseries:toInt(row.idseries)})
MERGE (a)-[:CHARACTER_IN_SERIES]->(b);

USING PERIODIC COMMIT LOAD CSV WITH HEADERS FROM "file:///C:\\actors.csv" AS row
MATCH (a:actors {idactors:toInt(row.idactors)})
MATCH (b:acted_in {idactors:toInt(row.idactors)})
MERGE (a)-[:AS_CHARACTER]->(b);
```

And you're done! All data and relationships are now successfuly imported.