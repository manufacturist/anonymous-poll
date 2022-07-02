### Table of Contents

* [What does it do?](#what-does-it-do)
* [Tech stack](#tech-stack)
* [How to run](#how-to-run)
* [Personal notes](#personal-notes)

### What does it do?

This project represents an anonymous poll solution. One can:

* Create polls with 3 different kind of questions (`Number`, `Choice`, `OpenEnd`)
* Invite voters via email 
* Offers a results link
* Optionally provide weights to each individual invited voter (TODO)
  * 

All data lives in-memory and nothing is stored forever, since [H2](https://www.h2database.com/html/main.html) is
used as a main data store. On every server restart or every couple of days (cron job), the data is wiped clean.

### Tech stack

| Library             | Description                                     |
|---------------------|-------------------------------------------------|
| cats-effect-3       | Functional goodies                              |
| munit-cats-effect-3 | Testing                                         |
| http4s              | HTTP Server / Client                            |
| tapir               | HTTP API server, client & documentation wrapper |
| scalajs             | What is says on the tin                         |
| scalatags           | HTML                                            |
| scalacss            | CSS                                             |
| h2                  | In-memory database                              |
| doobie              | SQL library                                     |
| flywaydb            | Database migration                              | 
| ciris               | Configuration                                   |
| log4cats            | Logging                                         |
| circe               | JSON                                            |
| newtypes            | Type aliases                                    |

| Plugin                   | Description                                                         |
|--------------------------|---------------------------------------------------------------------|
| sbt-scalajs              | Compiling, running and testing with Scala.js                        |
| sbt-scalajs-crossproject | Cross-platform compilation support for sbt (Scala, ScalaJS, Native) |
| sbt-web-scalajs          | fastOptJS / fullLinkJS                                              |
| sbt-web-scalajs-bundler  | npm / webpack                                                       |
| sbt-hepek                | Generates files from Scala code (used for index.html at build step) |

*^I'm still in the dark when it comes to how people do Scala.js development; if any of these 
plugins are obsolete or no longer used, please open a PR* 

### How to run

To run the backend, simply:

```bash
sbt backend/run
```

For a development build ([http://localhost:8000](http://localhost:8000)):

```bash
sbt fastOptJS/webpack
cd frontend && python -m http.server
``` 

For a production-grade build ([http://localhost:8000/index-prod.html](http://localhost:8000/index-prod.html)):

```bash
sbt fullOptJS/webpack
cd frontend && python -m http.server
```

### Personal notes

Subjected to random updates over time :shrug: :

* DRY can be extended to frontend as well now, since there's no need to write any TS/JS definitions for types, objects
  or API calls *(2022.07.02)*
* It's a nuisance at first to follow the code path / structure, especially when it comes to shared code
  (common/shared directory) *(2022.07.02)*
* I've been working with the IntelliJ EAP version & Scala nightly builds for this project. Decent Scala 3 support
  is still far, far away :sob: *(2022.07.02)*
* About `ciris`:
  * No longer must the developer worry about .conf files & bugs that result from their merger (e.g. in tests
    or local setups) *(2022.07.02)*
  * A default working local config can be provided from the code itself, without the need for any initial setup,
    such as setting environment variables *(2022.07.02)*
* About `tapir`:
  * Since tapir can be leveraged for 3 different purposes (documentation, API definition, client libraries),
    this makes it an excellent choice for a Scala & Scala.js project *(2022.07.02)*
  * Being able to share the endpoint definition between backend & frontend just feels great *(2022.07.02)*
  * Had to inline a tapir code snippet (http4s) that was not built for Scala.js & also to strip away any fs2 mention
    since it greatly encumbers the size of the javascript files (output) *(2022.07.02)*
* About `doobie` & `h2`:
  * h2 can easily be replaced with PostgreSQL, since SQL compatible code was written (works with both) *(2022.07.02)*
  * Had to do a custom intervention for `Meta[Array[A]]` since it was not working *(2022.07.02)*
  * *Psst. Pass the doobie, Scooby.* *(2022.07.02)*
