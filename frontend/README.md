# Frontend

### Docker
1. Build
```shell
docker build -t frontend .
```
2. Run
```shell
docker run -it -p 8080:8080 --rm --name frontend-app-local frontend
```


### Project setup
```
npm install
```

#### Compiles and hot-reloads for development
```
npm run serve
```

#### Compiles and minifies for production
```
npm run build
```

#### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).
