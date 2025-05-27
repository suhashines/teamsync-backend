## TeamSync - one stop solution for tech companies


* clone the project

```bash
git clone <repo-url>
```

* change directory
```bash
cd teamsync-backend
```

* run the docker container - it will create all the necessary tables and populate them

```bash
docker compose up -d

```

* verify table creation

- connect with the container
```bash
docker exec -it teamsync-db-1 psql -U postgres -d postgres

```
it will open psql in command line like this-
```code
postgres=# 
```
 To see the tables -

```sql
\dt
```
You should see something like this-

```sql
                  List of relations
 Schema |          Name           | Type  |  Owner   
--------+-------------------------+-------+----------
 public | appreciations           | table | postgres
 public | channels                | table | postgres
 public | channels_members        | table | postgres
 public | comments                | table | postgres
 public | events                  | table | postgres
 public | events_participants     | table | postgres
 public | feed_posts              | table | postgres
 public | feed_posts_media_urls   | table | postgres
 public | feed_posts_poll_options | table | postgres
 public | feedposts               | table | postgres
 public | flyway_schema_history   | table | postgres
 public | messages                | table | postgres
 public | poll_votes              | table | postgres
 public | pollvotes               | table | postgres
```

* install the dependecies (optional, usually while indexing ide downloads all the dependencies)

```bash
./mvnw clean install
```

* run the tomcat server

```bash
./mvnw spring-boot:run
```

* healthcheck

**send a get request to http://localhost:8080/api/v1/health**

You should see 

```bash
I am healthy
```


