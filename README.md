## TeamSync - one stop solution for tech companies


* clone the project

```bash
git clone <repo-url>
```

* change directory
```bash
cd teamsync-backend
```

* install the dependecies

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


