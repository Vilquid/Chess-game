# Chess-game
Another chess game (sever-side)

## Versions

- Postgresql 14
- Java 17

## Avant de lancer le projet

```postgresql
CREATE DATABASE chess;
CREATE ROLE chess WITH LOGIN PASSWORD 'chess';
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO chess;
```

ou

```bash
sudo psql -f chess.sql -U postgres
```

## Installation

- Importer en temps que projet Gradle (Intellij ou Eclipse)
- Lancer le projet
