# filtragecv
This application is used to order and filter resumes based on keywords specified by the user

This application was generated using JHipster 6.1.2, you can find documentation and help at [https://www.jhipster.tech/documentation-archive/v6.1.2](https://www.jhipster.tech/documentation-archive/v6.1.2).

This is a "microservice" application intended to be part of a microservice architecture, please refer to the [Doing microservices with JHipster][] page of the documentation for more information.

This application is configured for Service Discovery and Configuration with the JHipster-Registry. On launch, it will refuse to start if it is not able to connect to the JHipster-Registry at [http://localhost:8761](http://localhost:8761). For more information, read our documentation on [Service Discovery and Configuration with the JHipster-Registry][].


## Development

To start your application in the dev profile, simply run:

    ./mvnw

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].
## API endpoints
The api is RESTFUL and returns results in JSON.

### Candidate
#### GET
get candidates without order if keywords and priorities not specified in the get request else get candidates in order
#### POST
add a new candidate by providing a pdf file or office word file and a functional id
#### PUT
update a candidate by providing a valid id and the fields that needs change
#### DELETE
delete a candidate by providing a valid id
when a candidate is deleted its file gets deleted

### Skill
get skills
#### POST
add a skill by providing a valid name and valid category id
if that skill already exist change the category of the existing skill to the new category
#### PUT
update a skill by providing a valid name and category id
#### DELETE
delete a skill by providing a valid id
when a skill is deleted it gets deleted from all the candidates that has it
and all synonyms of that skill gets deleted

### Category
#### GET
get categories
#### POST
add a category by providing a valid name
#### PUT
update a category by providing a valid name
#### DELETE
delete a category by providing a valid id
when a category gets deleted all the skills in that category gets deleted

### City
#### GET
get cites
#### POST
add a city by providing a valid country and a valid name
if a skill in the other category with the same name as the city to be added already exist that skill gets deleted
#### PUT
update a city by providing a valid id, name, country
#### DELETE
delete a city

### School
#### GET
get schools
#### POST
add a school by providing a valid name
if a skill in the other category with the same name as the school to be added already exist that skill gets deleted
#### PUT
update a school by providing a valid id, name
#### DELETE
delete a school by providing a valid id
when a school gets deleted all of it synonyms gets deleted

### Country
#### GET
get countries
#### POST
add a country by providing a valid name
#### PUT
update a country by providing a valid id and name
#### DELETE
delete a country by providing a valid id

### Skill Synonym
#### GET
get skill synonyms
#### POST
add a skill synonym by providing a valid name and skill id</br>
if a skill in the other category with the same name as the skill synonym already exist that skill gets deleted</br>
all the candidates that have that skill synonym and its corresponding skill
the count of that skill gets updated to be the sum of both the the skill occurrence and the synonym occurrence
#### PUT
update a skill synonym by providing a valid name and skill id
#### DELETE
delete a skill synonym by providing a valid id

### School Synonym
#### GET
get school synonyms
#### POST
add a school synonym by providing a valid name and school id </br>
if a skill in the other category with the same name as the school synonym already exist that skill gets deleted</br>
#### PUT
update a school synonym by providing a valid id, name, school id
#### DELETE
delete a school synonym by providing a valid id

## Building for production

### Packaging as jar

To build the final jar and optimize the filtragecv application for production, run:

    ./mvnw -Pprod clean verify

To ensure everything worked, run:

    java -jar target/*.jar

Refer to [Using JHipster in production][] for more details.

### Packaging as war

To package your application as a war in order to deploy it to an application server, run:

    ./mvnw -Pprod,war clean verify

## Testing

To launch your application's tests, run:

    ./mvnw verify

For more information, refer to the [Running tests page][].

### Code quality

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```
docker-compose -f src/main/docker/sonar.yml up -d
```

You can run a Sonar analysis with using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) or by using the maven plugin.

Then, run a Sonar analysis:

```
./mvnw -Pprod clean verify sonar:sonar
```

If you need to re-run the Sonar phase, please be sure to specify at least the `initialize` phase since Sonar properties are loaded from the sonar-project.properties file.

```
./mvnw initialize sonar:sonar
```

or

For more information, refer to the [Code quality page][].

## Using Docker to simplify development (optional)

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.

For example, to start a mysql database in a docker container, run:

    docker-compose -f src/main/docker/mysql.yml up -d

To stop it and remove the container, run:

    docker-compose -f src/main/docker/mysql.yml down

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

    ./mvnw -Pprod verify jib:dockerBuild

Then run:

    docker-compose -f src/main/docker/app.yml up -d

For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the docker-compose sub-generator (`jhipster docker-compose`), which is able to generate docker configurations for one or several JHipster applications.

## Continuous Integration (optional)

To configure CI for your project, run the ci-cd sub-generator (`jhipster ci-cd`), this will let you generate configuration files for a number of Continuous Integration systems. Consult the [Setting up Continuous Integration][] page for more information.

[jhipster homepage and latest documentation]: https://www.jhipster.tech
[jhipster 6.1.2 archive]: https://www.jhipster.tech/documentation-archive/v6.1.2
[doing microservices with jhipster]: https://www.jhipster.tech/documentation-archive/v6.1.2/microservices-architecture/
[using jhipster in development]: https://www.jhipster.tech/documentation-archive/v6.1.2/development/
[service discovery and configuration with the jhipster-registry]: https://www.jhipster.tech/documentation-archive/v6.1.2/microservices-architecture/#jhipster-registry
[using docker and docker-compose]: https://www.jhipster.tech/documentation-archive/v6.1.2/docker-compose
[using jhipster in production]: https://www.jhipster.tech/documentation-archive/v6.1.2/production/
[running tests page]: https://www.jhipster.tech/documentation-archive/v6.1.2/running-tests/
[code quality page]: https://www.jhipster.tech/documentation-archive/v6.1.2/code-quality/
[setting up continuous integration]: https://www.jhipster.tech/documentation-archive/v6.1.2/setting-up-ci/
