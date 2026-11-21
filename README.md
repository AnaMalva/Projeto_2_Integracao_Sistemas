# Project 2 – Integração de Sistemas

A Jakarta EE application, running on WildFly, that integrates two simulated
databases built from a Google Scholar XML file:

- **Researchers** (name, universities, interests, homepage, email)
- **Publications** (title, year, publisher, citations, DOI, authors)

| Part | What | Module |
| --- | --- | --- |
| a) | Standalone loader: validates the XML against an XSD and stores it in PostgreSQL | `loader` |
| b) | SOAP web service for researchers, backed by its own EJB | `soap` + `ResearcherBean` |
| c) | REST web service for publications, backed by its own EJB | `rest` + `PublicationBean` |
| d) | Command-line client that uses every SOAP and REST operation, plus two extra operations | `client` |
| e) | Web front-end: list of publication titles, plus researcher profiles (our own feature) | `web` |

---

## 1. Architecture

```
XML ──▶ loader ──▶ PostgreSQL ◀── EJBs ◀── SOAP  ◀── client
                                       ◀── REST  ◀── client
                                       ◀── web   ◀── browser
```

The loader reads the XML once and saves it in PostgreSQL.
Inside WildFly, two EJBs read the database: ResearcherBean for researchers and PublicationBean for publications.
The SOAP service (researchers) and the REST service (publications) each use their own EJB. The web pages use both.
The client only talks to SOAP and REST, never to the database.

The two "separate databases" are two PostgreSQL schemas, researchers_db and publications_db. They have no link between them: a publication stores its authors by name.

## 2. Requirements

| Tool | Version used |
| --- | --- |
| Java (JDK) | 21 or later |
| Maven | 3.9 |
| WildFly | 42.0.0.Final, started with `standalone-full.xml` |
| PostgreSQL | 18, on `localhost:5432`, database `postgres`, user `postgres` |
| PostgreSQL JDBC driver | `postgresql-42.7.8.jar` |

The commands below are for macOS or Linux. `WILDFLY_HOME` must point to the
WildFly folder, for example:

```bash
export WILDFLY_HOME=~/wildfly-42.0.0.Final
```

## 3. Build, load and deploy

From the project root, with WildFly running:

```bash
# 1. Build every module
mvn clean install

# 2. Part a) – load the XML into PostgreSQL
java -jar loader/target/loader.jar data/googlescholar.xml

# 3. Deploy the EAR (SOAP + REST + web)
mvn -pl ear package -Doutput=$WILDFLY_HOME/standalone/deployments
```

After a few seconds, `$WILDFLY_HOME/standalone/deployments/` should contain
`project2.ear.deployed`. If it contains `project2.ear.failed`, that file holds
the reason.

---

## 4. Using each part

### a) Loader

```bash
java -jar loader/target/loader.jar [file.xml]      # default: data/googlescholar.xml
java -jar loader/target/loader.jar --help
```

1. Validates the file against `googlescholar_val.xsd`. If it is invalid, it stops and nothing is loaded.
2. Reads it with JAXB. `<author ref="…">` is resolved to the researcher through `@XmlID` / `@XmlIDREF`.
3. Rejects files with a repeated researcher name or paper title (ignoring case), and lists every duplicate.
4. Recreates the tables and stores the data in one transaction.

Expected output: `Loaded 21 researchers and 17 publications into the database.`

### b) SOAP – Researchers

Endpoint: `http://localhost:8080/soap/ResearchersWS`

| Operation | Parameter | Returns |
| --- | --- | --- |
| `getAllResearchers` | – | All researchers, sorted by name |
| `getResearcherByName` | `name` | One researcher (case-insensitive), or empty |
| `getResearchersByInterest` | `interest` | Researchers with that interest (case-insensitive) |

A missing or empty parameter returns an empty answer instead of a SOAP fault.
Example:

```bash
curl -s -X POST http://localhost:8080/soap/ResearchersWS -H "Content-Type: text/xml;charset=UTF-8" -d '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ws="http://ws/"><soapenv:Body><ws:getResearcherByName><name>Marco Vieira</name></ws:getResearcherByName></soapenv:Body></soapenv:Envelope>'
```

### c) REST – Publications

Base URL: `http://localhost:8080/rest/api/publications`. All answers are JSON.

| Operation | URL |
| --- | --- |
| All publications | `GET /publications` |
| By exact title | `GET /publications/title?title=…` |
| By part of the title (extra) | `GET /publications/search?title=…` |
| By researcher | `GET /publications/researcher?name=…` |
| By journal/conference (publisher) | `GET /publications/venue?name=…` |
| By publisher and researcher | `GET /publications/venue-researcher?venue=…&researcher=…` |

- A missing required parameter returns **400 Bad Request** with a short message.
- A title that does not exist returns **404**.
- A valid search with no results returns **200** with `[]`.

Example:

```bash
curl "http://localhost:8080/rest/api/publications/venue-researcher?venue=IEEE&researcher=Nuno%20Laranjeiro"
```

### d) Command-line client

```bash
java -jar client/target/client.jar
```

| Option | Operation | Uses |
| --- | --- | --- |
| 1 | All researchers | SOAP |
| 2 | Researcher by name | SOAP |
| 3 | Researchers by interest | SOAP |
| 4 | All publications | REST |
| 5 | Publication by title | REST |
| 6 | Publications of a researcher | REST |
| 7 | Publications of a journal/conference | REST |
| 8 | Journal/conference + researcher | REST |
| 9 | **Extra:** citations per year of a researcher | REST |
| 10 | **Extra:** publications of an institution in a period | SOAP + REST |
| 0 | Exit | |

- **Option 9** adds up the citations of the researcher's papers, grouped by
  year of publication. The XML only has each paper's total citations, not a
  history per year.
- **Option 10** asks SOAP for all researchers, keeps those whose university
  contains the text typed, asks REST for each one's publications, keeps those
  inside the year range, and removes repeated papers (papers written by
  several researchers of the same institution).

### e) Web front-end

Open **http://localhost:8080/web/**

- **Publications** (`/web/titles`) lists every publication title (the required
  feature). The list can be filtered by publisher and by year range. Each
  author links to their profile.
- **Researchers** (`/web/researcher`) is our own feature. It has a search box
  and a profile page with universities, interests (each one links to the other
  researchers who share it), homepage, email, publications, total citations, a
  chart of citations per year of publication, and the **most cited year**.

**Most cited year.** Each publication year's citations are divided by the
number of years since then (that year included). Older papers have had more
time to collect citations, so this way they are not favoured just for being
older. Example: Marco Vieira's 2007 papers have 50 citations in 20 years (2.5
per year), and his 2020 papers have 31 in 7 years (4.4 per year), so his most
cited year is 2020.

---

## 5. Testing

`test-services.sh` checks every SOAP and REST operation against the expected
counts from `data/googlescholar.xml`:

```bash
./test-services.sh
```

Expected: `18 passed, 0 failed`.

Some values to check by hand:

| Check | Expected |
| --- | --- |
| Researchers / publications | 21 / 17 |
| Interest "Software Security" | Bruno Jesus, Naghmeh Ivaki, Nuno Laranjeiro |
| Publications of Marco Vieira | 4 (81 citations) |
| Publications of Nuno Laranjeiro | 11 (903 citations) |
| IEEE + Nuno Laranjeiro | 7 |

---

## 6. Project structure

```
project2/
├── pom.xml                 parent (modules, versions, plugins)
├── test-services.sh        SOAP + REST tests
├── data/googlescholar.xml  input data
├── jpa/                    entities + project2PU
├── loader/                 part a)
├── ejbs/                   ResearcherBean, PublicationBean
├── soap/                   part b)
├── rest/                   part c)
├── client/                 part d)
├── web/                    part e)
└── ear/                    project2.ear
```