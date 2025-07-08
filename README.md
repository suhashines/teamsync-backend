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
docker exec -it <container-name> psql -U postgres -d postgres

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

## Testing and Code Coverage

### Running Unit Tests

To run all unit tests:

```bash
./mvnw clean test
```

This command will:
- Clean the previous build artifacts
- Compile the source code
- Run all unit tests
- Generate JaCoCo coverage data

### Generating JaCoCo Coverage Report

To generate a detailed HTML coverage report:

```bash
./mvnw jacoco:report
```

This will create an HTML report at: `target/site/jacoco/index.html`

### Running Tests and Generating Coverage Report in One Command

To run tests and generate the coverage report in a single command:

```bash
./mvnw clean test jacoco:report
```

### Viewing Coverage Reports

After running the tests and generating the report, you can:

1. **Open the HTML report**: Navigate to `target/site/jacoco/index.html` in your browser
2. **View coverage metrics**: The report shows:
   - **Instructions**: Bytecode instructions covered by tests
   - **Branches**: Decision points (if/else, switch) covered by tests
   - **Lines**: Source code lines covered by tests
   - **Methods**: Methods called by tests
   - **Classes**: Classes touched by tests

### Coverage Report Structure

The JaCoCo report provides:
- **Package-level coverage**: Overall coverage for each package
- **Class-level coverage**: Detailed coverage for each class
- **Method-level coverage**: Line-by-line coverage within methods
- **Color-coded coverage**: Green (covered), red (not covered), yellow (partially covered)

### Test Results

Test results are also available in:
- `target/surefire-reports/` - Detailed test execution reports
- Console output - Summary of test execution


