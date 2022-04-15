### Usage

To run the backend, simply:
```bash
sbt backend/run
```

For a development build ([http://localhost:8000](http://localhost:8000)):
```bash
sbt fastOptJS/webpack
cd frontend && python -m http.server
``` 

For a production-grade build ([http://localhost:8000/index-prod.html](http://localhost:8000/index-prod.html)):
```bash
sbt fullOptJS/webpack
cd frontend && python -m http.server
```