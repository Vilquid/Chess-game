# Chess-game
Another chess game (sever-side)

## Versions

- Postgresql 14
- Java 17

## Avant de lancer le projet

```postgresql
CREATE DATABASE springchess;
CREATE ROLE springchess WITH LOGIN PASSWORD 'springchess';
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO springchess;
```

ou

```bash
sudo psql -f test.sql -U postgres
```

## Installation

- Importer en temps que projet Gradle (Intellij ou Eclipse)
- Lancer le projet
