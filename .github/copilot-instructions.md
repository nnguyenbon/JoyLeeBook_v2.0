## Quick repo summary

- Language & packaging: Java 17 Maven project packaged as a WAR (see `pom.xml`).
- Runtime: Jakarta Servlet/JSP style webapp under `src/main/webapp/` (static HTML pages present). The project expects deployment to a servlet container (Tomcat/Jetty) or an application server.
- Data layer: plain JDBC DAOs in `src/main/java/dao/` talking to Microsoft SQL Server (mssql-jdbc dependency in `pom.xml`). Models live in `src/main/java/model/`.

## High level architecture / why it is structured like this

- Thin webapp static UI + server-side Java controllers/servlets (not using Spring). Static pages and client scripts live in `src/main/webapp/` and are served from the WAR.
- DAO layer encapsulates all SQL and result-set mapping. Example: `SeriesDAO.java` uses helper `extractSeriesFromResultSet(...)` to build `model.Series`.
- Soft-delete convention: most tables use an `is_deleted` boolean flag rather than physical deletes (see queries in `SeriesDAO`).

## Build / run / debug (what actually works here)

- Build artifact: run Maven to create a WAR. Typical commands (used by maintainers):

  mvn clean package

- Java version: 17 (see `<maven.compiler.*>` in `pom.xml`).
- The project depends on the servlet API as "provided" — you must deploy the generated WAR to a servlet container (Tomcat/Jetty) which provides the servlet implementation.
- DB driver: MS SQL Server driver is included as a normal dependency; the app expects a SQL Server instance reachable by the server runtime.

## Project-specific conventions & patterns for editors/AI

- SQL & dialect: SQL Server T-SQL idioms are used (e.g., `SELECT TOP (n)`, `DATEPART(WEEK, ...)`, `GETDATE()`); queries assume MS SQL semantics. When modifying SQL, use T-SQL constructs.
- PreparedStatements everywhere: DAOs use prepared statements and close resources with try-with-resources. Follow the same pattern: prepare, set parameters, execute, map results, and close resources.
- Timestamp handling: DAOs convert between `LocalDateTime` and `Timestamp` using `Timestamp.valueOf(LocalDateTime.now())` and `rs.getTimestamp(...).toLocalDateTime()`.
- Soft deletes: respect `is_deleted = false` in queries when reading or updating domain records (see `SeriesDAO.getAll()` and `findById(...)`).
- Result mapping helpers: use or add `extract...FromResultSet(ResultSet rs)` style helpers in DAOs to keep mapping consistent (see `SeriesDAO.extractSeriesFromResultSet`).

## Cross-cutting issues and gotchas observed (useful for fixes)

- A few methods have small bugs / inconsistencies to watch for:
  - `User.setUserId(int userId)` has an empty body — setting id is a no-op in that class. Search `src/main/java/model/User.java` to inspect and fix.
  - Some SQL strings have minor syntax issues (e.g., missing parentheses in `UserDAO.selectTopUserPoints` query). Run local compile/tests to catch these.
  - Frontend JS in `ChapterContent.html` has a typo: `chapterListBtnl` vs `chapterListBtn` — fixed by editing the DOM id usage.

## Integration points & where to look when changing behavior

- Database: connection creation is not in the DAO files shown; search for a `Connection` provider or servlet that calls `new SeriesDAO(conn)` to understand lifecycle and connection pooling.
- Web entry: `web.xml` sets `home.html` as the welcome page. Look under `src/main/webapp/` for the front-end pages and assets.
- Third-party services: no external APIs are visible in the repo; the app relies on the MS SQL Server JDBC driver and JSTL for server-side rendering.

## How to make safe code changes (concrete examples)

- Add a new DAO method: follow the pattern in `SeriesDAO`:
  - Create SQL string using T-SQL idioms.
  - Use try-with-resources for PreparedStatement and ResultSet.
  - Map result rows with an `extract...FromResultSet` helper.
  - Respect `is_deleted` and update `updated_at` with `Timestamp.valueOf(LocalDateTime.now())` when mutating.

- Add a small UI change to `src/main/webapp/*.html`: these are static pages in the WAR. Update HTML/CSS/JS and repackage the WAR.

## Files worth inspecting when diagnosing issues

- `pom.xml` — Java version, packaging (WAR), and key dependencies (jakarta servlet, JSTL, mssql-jdbc).
- `src/main/webapp/WEB-INF/web.xml` — servlet config and welcome file.
- `src/main/java/dao/SeriesDAO.java`, `UserDAO.java` — canonical DAO patterns and SQL idioms.
- `src/main/java/model/*` — domain objects and common getters/setters (watch for small bugs like `setUserId`).
- `src/main/webapp/ChapterContent.html` — example of frontend + inline JS patterns and small known typo.

## Suggested tasks for an AI code agent (prioritized)

1. Run `mvn -q package` and report compilation errors; fix small bugs in models and DAO SQL that block build.
2. Search for connection creation to add or improve connection pooling or resources handling.
3. Fix obvious UI JS typos (for example in `ChapterContent.html`) and re-verify the static UI behaves as expected.

---
If any of the above areas are unclear or incomplete I can expand a section or add precise search targets (for example: the servlet that wires DAOs to Connection objects). Feedback? 
