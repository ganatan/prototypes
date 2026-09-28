# Python Starter CRUD

## Installation

```bash
python -m venv .venv
```

### Windows PowerShell

```powershell
.\.venv\Scripts\Activate.ps1
```

### Linux

```bash
source .venv/bin/activate
```

```bash
pip install -r requirements.txt
```

Copier `.env.example` vers `.env` et adapter `DATABASE_URL`.

## Lancement

```bash
python -m src.main
```

Application :

```text
http://localhost:3000
```

Swagger :

```text
http://localhost:3000/docs
```

## Endpoints

```text
GET    /persons
GET    /persons/{id}
POST   /persons
PUT    /persons/{id}
DELETE /persons/{id}
```
