# iBudget

iBudget is a home budgeting app with an Angular frontend, Spring Boot REST API, and PostgreSQL storage.

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

## Deploy to Render

1. Push this repository to GitHub.
2. In Render, choose **New +** then **Blueprint**, and connect the GitHub repository.
3. Review the services and plans in `render.yaml`, then apply the blueprint. It creates the PostgreSQL database, Docker-based Spring Boot API, and Angular static site.
4. After deployment, check `https://ibudget-api.onrender.com/api/budget/summary` and open the `ibudget-ui` static-site URL to test CRUD flows. The static site rewrites app routes to `index.html` so browser refreshes work.

The production frontend API URL is configured in `ibudget-frontend/src/environments/environment.prod.ts`. If Render assigns the API a URL other than `https://ibudget-api.onrender.com`, update that setting and push the change to trigger a frontend redeploy.

The Blueprint uses Render's free plans. Free web services can take about a minute to wake after inactivity. The free PostgreSQL database expires 30 days after creation; after a further 14-day grace period, Render deletes it and its data. Use a paid database plan for data you need to keep.

## API endpoints

- `GET /api/budget` and `GET /api/budget/{id}`
- `POST /api/budget` and `PUT /api/budget/{id}`
- `DELETE /api/budget/{id}`
- `GET /api/budget/summary`
