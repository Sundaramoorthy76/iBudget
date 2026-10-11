# iBudget

iBudget is a home budgeting app with an Angular frontend, Spring Boot REST API, and PostgreSQL storage.

The Employees module separately maintains employee details, current monthly salaries, and dated salary advances and deductions. It does not create or modify budget income or expense transactions. Deactivate employees instead of deleting them to retain their records.

## Run locally

Start PostgreSQL with Docker Compose from the repository root:

```bash
docker compose up -d
```

Start the backend using the PostgreSQL profile:

```bash
cd ibudget-backend
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

In another terminal, start the frontend:

```bash
cd ibudget-frontend
npm install
npm start
```

Open `http://localhost:4200`. Local PostgreSQL defaults are database `ibudget`, username `ibudget`, and password `ibudget!2#`; override them with `SPRING_DATASOURCE_*` environment variables as needed.

## Deploy to Render with Neon

The Angular UI and Spring Boot API run on Render. PostgreSQL is hosted separately on Neon.

The existing test data will not be migrated. The backend creates/updates its tables in the empty Neon database on startup.

1. In Neon, open the `ibudget-production` project and click **Connect**. Select the default branch, database `neondb`, and role `neondb_owner`. Turn **Connection pooling on** and copy the pooled connection details. The password was reset, so use the current password. Keep the connection string private.
2. In the connection string, note the hostname (the part beginning with `ep-` and containing `-pooler`), database name, and role. Do not put the complete connection string or password in source files.
3. In Render, open the existing `ibudget-api` service and go to **Environment**. Add or update these variables:
   - `SPRING_DATASOURCE_URL` = `jdbc:postgresql://<pooled-host>/neondb?sslmode=require&channel_binding=require`
   - `SPRING_DATASOURCE_USERNAME` = `neondb_owner`
   - `SPRING_DATASOURCE_PASSWORD` = the current Neon role password

   Replace `<pooled-host>` with the hostname from the pooled Neon connection string. Enter the password directly in Render's environment editor; do not share it or commit it. The API supports the full JDBC URL through `SPRING_DATASOURCE_URL`.
4. Save the environment changes and redeploy `ibudget-api`. On the first startup, Hibernate creates or updates the application tables in Neon.
5. Confirm `https://ibudget-api.onrender.com/api/budget/summary` responds successfully. Then open the Render static-site URL and test creating and listing budget entries, employees, and advances/deductions.
6. Once verified, the old Render database can be deleted from the Render dashboard. The Blueprint no longer provisions a Render PostgreSQL database.

If the Render service is managed by the Blueprint, push this repository's updated `render.yaml` to GitHub and apply the Blueprint changes. Review the Blueprint diff carefully: it removes the old Render database resource. This is expected only because its test data is disposable. Set the three Neon environment variables in the API service after applying the Blueprint if they are not already present. The static site rewrites app routes to `index.html` so browser refreshes work.

The production frontend API URL is configured in `ibudget-frontend/src/environments/environment.prod.ts`. If Render assigns the API a URL other than `https://ibudget-api.onrender.com`, update that setting and push the change to trigger a frontend redeploy.

The Blueprint uses Render's free web-service plans. Free web services can take about a minute to wake after inactivity.

## API endpoints

- `GET /api/budget` and `GET /api/budget/{id}`
- `POST /api/budget` and `PUT /api/budget/{id}`
- `DELETE /api/budget/{id}`
- `GET /api/budget/summary`
- `GET /api/employees` and `POST /api/employees`
- `PUT /api/employees/{employeeId}`
- `GET`, `POST /api/employees/{employeeId}/salary-records`
- `PUT`, `DELETE /api/employees/{employeeId}/salary-records/{recordId}`
