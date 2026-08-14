import asyncio
import os

import asyncpg
import uvicorn
from dotenv import load_dotenv
from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from src.api.person.controller import routes as person_routes
from src.config import DATABASE_URL_KEY, SERVER_HOST, SERVER_PORT


async def create_application() -> tuple[FastAPI, asyncpg.Pool]:
    load_dotenv()

    database_url = os.getenv(DATABASE_URL_KEY)

    if not database_url:
        raise RuntimeError("DATABASE_URL absent")

    pool = await asyncpg.create_pool(
        dsn=database_url,
        min_size=1,
        max_size=5,
    )

    print("Connexion à la database réussie")

    persons = await pool.fetch(
        "SELECT id, first_name, last_name, city_id FROM person ORDER BY id"
    )

    print(f"Persons trouvées : {len(persons)}")

    for person in persons:
        print(
            f'{person["id"]} - '
            f'{person["first_name"]} '
            f'{person["last_name"]} - '
            f'city_id: {person["city_id"]}'
        )

    app = FastAPI()

    @app.exception_handler(RequestValidationError)
    async def validation_exception_handler(
        request,
        exc,
    ):
        return JSONResponse(
            status_code=400,
            content={"detail": exc.errors()},
        )

    app.include_router(person_routes(pool))

    return app, pool


async def main() -> None:
    app, pool = await create_application()

    config = uvicorn.Config(
        app,
        host=SERVER_HOST,
        port=SERVER_PORT,
        log_level="info",
    )

    server = uvicorn.Server(config)

    print(
        f"Application disponible sur "
        f"http://{SERVER_HOST}:{SERVER_PORT}"
    )

    try:
        await server.serve()
    finally:
        await pool.close()


if __name__ == "__main__":
    asyncio.run(main())
