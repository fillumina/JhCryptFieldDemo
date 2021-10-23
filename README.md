# JhCryptFieldDemo

Demo application to show how to manage encrypted JSON feilds. This would protect against compromised, externalized or leaked db. Of course the downside is that the encrypted data cannot be searched or queried and takes more CPU cycles to be accessed.

### Features

- fully encrypted JSON objects can contain whatever structure needed
- the UI is mostly untouched: the encrypted 'entity' are provided transparently (use a HTTPS transport of course) and can still be list, edit, modified as usual and the swagger (open-api) API is still fully available
- the encrypted data cannot be searched or queried (but you can use [blind index](https://itnext.io/indexing-encrypted-database-field-for-searching-e50e7bcfbd80), not implemented here)

### Description

The secrets (password and salt) are in the `application.yml` configuration file but should be moved in a more protected location on production or otherwise provided. There is a quite convoluted mechanism (static fields plus lazy initialization) in place to provide secrets to the entity at runtime but it's working fine.

The container JPA entity (`Customer`) uses a `TextBlob` field to contain an encrypted, base64 encoded, JSON enclosed object `CustomerAddress` (a binary `Blob` would do great too, just skip the base64 encoding phase).

The generated web application (UI and API) is mostly untouched and only the back-end needs to be modified. The only UI changes regard the fact that the enclosed entity cannot be created if the container is not already persisted so the enclosed object edit operation has been moved to the view page instead of the edit page of the container (that's just the easiest solution with minimal impact on the UI, of course there are other ways).

The fastest way to implement that is to write down the `JDL` file with the enclosed object in a one-to-one relationship with the container and import that as usual with `jhipster jdl entities.jh`. Then the enclosed entity (`CustomerAddress`)can be cleaned off its JPA annotations and its changelogs removed from liquibase (don't forget to remove the relationship in the owner entity `Customer` too).

The repository must be removed (the enclosed object is no more a db entity in its own right), the rest service needs to be modified to use the container repository instead of the removed enclosed one.

Check the git commits for further details. Here is the `JDL` file used:

```
// this is the container entity (the only one persisted on db)
entity Customer {
    firstName String,
    lastName String,
    email String,
    telephone String,
    // this field contains the encrypted JSON address
    addressRaw TextBlob
}

// this is the enclosed entity.
// it is an entity here to allow the generation of the ui.
// the jpa annotations and db liquibase instructions will be removed.
entity CustomerAddress {
    street String,
    city String,
    postcode String required maxlength(10),
    country String required maxlength(2)
}

// the id of enclosed object is the id of the container.
relationship OneToOne {
    Customer{address(street)} to CustomerAddress
}

paginate Customer, CustomerAddress with pagination

```

## JHipster

This application was generated using JHipster 7.3.1, you can find documentation and help at [https://www.jhipster.tech/documentation-archive/v7.3.1](https://www.jhipster.tech/documentation-archive/v7.3.1).

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:

1. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools.
You will only need to run this command when dependencies change in [package.json](package.json).

```
npm install
```

We use npm scripts and [Angular CLI][] with [Webpack][] as our build system.

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

```
./mvnw
npm start
```

Npm is also used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in [package.json](package.json). You can also run `npm update` and `npm install` to manage dependencies.
Add the `help` flag on any command to see how you can use it. For example, `npm help update`.

The `npm run` command will list all of the scripts available to run for this project.

### PWA Support

JHipster ships with PWA (Progressive Web App) support, and it's turned off by default. One of the main components of a PWA is a service worker.

The service worker initialization code is disabled by default. To enable it, uncomment the following code in `src/main/webapp/app/app.module.ts`:

```typescript
ServiceWorkerModule.register('ngsw-worker.js', { enabled: false }),
```

### Managing dependencies

For example, to add [Leaflet][] library as a runtime dependency of your application, you would run following command:

```
npm install --save --save-exact leaflet
```

To benefit from TypeScript type definitions from [DefinitelyTyped][] repository in development, you would run following command:

```
npm install --save-dev --save-exact @types/leaflet
```

Then you would import the JS and CSS files specified in library's installation instructions so that [Webpack][] knows about them:
Edit [src/main/webapp/app/app.module.ts](src/main/webapp/app/app.module.ts) file:

```
import 'leaflet/dist/leaflet.js';
```

Edit [src/main/webapp/content/scss/vendor.scss](src/main/webapp/content/scss/vendor.scss) file:

```
@import '~leaflet/dist/leaflet.css';
```

Note: There are still a few other things remaining to do for Leaflet that we won't detail here.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

### Using Angular CLI

You can also use [Angular CLI][] to generate some custom client code.

For example, the following command:

```
ng generate component my-component
```

will generate few files:

```
create src/main/webapp/app/my-component/my-component.component.html
create src/main/webapp/app/my-component/my-component.component.ts
update src/main/webapp/app/app.module.ts
```

### JHipster Control Center

JHipster Control Center can help you manage and control your application(s). You can start a local control center server (accessible on http://localhost:7419) with:

```
docker-compose -f src/main/docker/jhipster-control-center.yml up
```

## Building for production

### Packaging as jar

To build the final jar and optimize the JhCryptFieldDemo application for production, run:

```
./mvnw -Pprod clean verify
```

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

```
java -jar target/*.jar
```

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

Refer to [Using JHipster in production][] for more details.

### Packaging as war

To package your application as a war in order to deploy it to an application server, run:

```
./mvnw -Pprod,war clean verify
```

## Testing

To launch your application's tests, run:

```
./mvnw verify
```

### Client tests

Unit tests are run by [Jest][]. They're located in [src/test/javascript/](src/test/javascript/) and can be run with:

```
npm test
```

For more information, refer to the [Running tests page][].

### Code quality

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```
docker-compose -f src/main/docker/sonar.yml up -d
```

Note: we have turned off authentication in [src/main/docker/sonar.yml](src/main/docker/sonar.yml) for out of the box experience while trying out SonarQube, for real use cases turn it back on.

You can run a Sonar analysis with using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) or by using the maven plugin.

Then, run a Sonar analysis:

```
./mvnw -Pprod clean verify sonar:sonar
```

If you need to re-run the Sonar phase, please be sure to specify at least the `initialize` phase since Sonar properties are loaded from the sonar-project.properties file.

```
./mvnw initialize sonar:sonar
```

For more information, refer to the [Code quality page][].

## Using Docker to simplify development (optional)

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.

For example, to start a postgresql database in a docker container, run:

```
docker-compose -f src/main/docker/postgresql.yml up -d
```

To stop it and remove the container, run:

```
docker-compose -f src/main/docker/postgresql.yml down
```

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

```
./mvnw -Pprod verify jib:dockerBuild
```

Then run:

```
docker-compose -f src/main/docker/app.yml up -d
```

For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the docker-compose sub-generator (`jhipster docker-compose`), which is able to generate docker configurations for one or several JHipster applications.

## Continuous Integration (optional)

To configure CI for your project, run the ci-cd sub-generator (`jhipster ci-cd`), this will let you generate configuration files for a number of Continuous Integration systems. Consult the [Setting up Continuous Integration][] page for more information.

[jhipster homepage and latest documentation]: https://www.jhipster.tech
[jhipster 7.3.1 archive]: https://www.jhipster.tech/documentation-archive/v7.3.1
[using jhipster in development]: https://www.jhipster.tech/documentation-archive/v7.3.1/development/
[using docker and docker-compose]: https://www.jhipster.tech/documentation-archive/v7.3.1/docker-compose
[using jhipster in production]: https://www.jhipster.tech/documentation-archive/v7.3.1/production/
[running tests page]: https://www.jhipster.tech/documentation-archive/v7.3.1/running-tests/
[code quality page]: https://www.jhipster.tech/documentation-archive/v7.3.1/code-quality/
[setting up continuous integration]: https://www.jhipster.tech/documentation-archive/v7.3.1/setting-up-ci/
[node.js]: https://nodejs.org/
[npm]: https://www.npmjs.com/
[webpack]: https://webpack.github.io/
[browsersync]: https://www.browsersync.io/
[jest]: https://facebook.github.io/jest/
[leaflet]: https://leafletjs.com/
[definitelytyped]: https://definitelytyped.org/
[angular cli]: https://cli.angular.io/
