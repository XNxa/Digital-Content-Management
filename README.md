## Features

TODO

## Architecture

Project Stack :
- Angular 17
- Java 17 with Spring boot 3.x
- [Postgres](https://github.com/postgres/postgres)
- [MinIO](https://github.com/minio/minio)
- [Keycloak](https://github.com/keycloak/keycloak)
- [Elasticsearch](https://github.com/elastic/elasticsearch)


<img width=50% src="https://github.com/user-attachments/assets/33a86e71-f8be-417e-a1f9-d06ef847b316">

## Usage

> These instructions are for a developpment environment.

### Backend

First, start the docker containers:
```bash
docker-compose up
```

For the first time lauching the backend, you should configure keycloak using:
```bash
java com.dcm.backend.init.InitKeycloak
```

Finnaly, start spring :
```bash
mvn spring-boot:run
```

### Frontend

```bash
ng serve
```

## Gallery

| ![Login](https://github.com/user-attachments/assets/0827928a-391f-4f41-a77c-a700969aecd8) | ![Home](https://github.com/user-attachments/assets/a9df78b5-fc14-44f3-a393-9fb82b255c9e) |
| -- | -- |
| Login page served by the keycloak server | User home page with some stats |
| ![Gallerie](https://github.com/user-attachments/assets/d374cfa7-2a6d-4aae-84d5-b7c530c3bc4a) | ![Filtres](https://github.com/user-attachments/assets/0ad0e98c-da69-4545-928d-3f4f08ecfbfe) |
| Gallery view for each project / types of files | Search on every field |
| ![Detail](https://github.com/user-attachments/assets/47a02228-0f55-4597-b500-f236eafd1f22) | ![Detail zip](https://github.com/user-attachments/assets/7408016b-97fe-4088-b456-e707c3d1cfd4) |
| Detail about the file, possibility to share by email, link, to download and modify the metadatas | Preview inside zip files |
| ![Recherche](https://github.com/user-attachments/assets/a6ffc9ad-ffe6-4813-a425-3e9e580feda0) | ![Roles](https://github.com/user-attachments/assets/e0bc50c9-5b6b-4f28-a0b5-3fea8e38e827) |
| Global, fuzzy and phonetic search powered by elasticsearch | Ability to manage roles and users within the app | 

## License

This work is unlicensed and hence protected by copyright law. Please contact me if you have any questions.
