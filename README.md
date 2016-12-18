ManyWho Provisioning Service
============================

[![Build Status](https://travis-ci.org/manywho/service-provisioning.svg)](https://travis-ci.org/manywho/service-provisioning)

> This service is currently in development, and not yet recommended for use in production environments

This service is used to perform some tasks when a new tenant is provisioned in ManyWho.

## Usage

If you need to, it's easy to spin up your own instance of the service if you follow these instructions:

### Database

You will find the required schemas for the supported databases in the [`src/main/sql`](src/main/sql) folder.

### Configuring

The available configuration settings for the application are:

* **DATABASE_HOSTNAME:**
* **DATABASE_SUPERUSER_USERNAME**
* **DATABASE_SUPERUSER_PASSWORD**

#### Environment Variables

You will have to configure the application at runtime by using environment variables, so you'll need to run the
application like this:

```bash
$ DATABASE_HOSTNAME=localhost DATABASE_SUPERUSER_USERNAME=postgres DATABASE_SUPERUSER_PASSWORD=password java -jar target/provisioning-*.jar
```

### Building

To build the application, you will need to have Maven 3 and a Java 8 implementation installed (OpenJDK and Oracle Java SE
are both supported).

Now you can build the runnable shaded JAR:

```bash
$ mvn clean package
```

### Running

The application is a RestEASY JAX-RS application, that by default is run under the Undertow server on port 8080 (if you
use the packaged JAR).

#### Defaults

Running the following command will start the service listening on `0.0.0.0:8080`:

```bash
$ java -jar target/provisioning-*.jar
```

#### Heroku

The service is compatible with Heroku, and can be deployed by clicking the button below:

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/manywho/service-provisioning)

## Contributing

Contributions are welcome to the project - whether they are feature requests, improvements or bug fixes! Refer to 
[CONTRIBUTING.md](CONTRIBUTING.md) for our contribution requirements.

## License

This service is released under the [MIT License](https://opensource.org/licenses/MIT).