# Digirati Taxonomy Manager

Digirati Taxonomy Manager is a cross-platform application for managing and using a taxonomy to enrich web content e.g. by automatically generating tags for an input piece of content.

## Getting Started

Digirati Taxonomy Manager is built against Java 11 via gradle.
To build the Digirati Taxonomy Manager, use the included gradle wrapper:

```
./gradlew build
```

Once you have built the project locally you can use docker-compose to get an environment up, with a database and sample configuration:
```
docker-compose up -d
```

and bring it back down with:
```
docker-compose stop
```

To only run the database you can run:
```
docker-compose up postgres
```

## Key Dependencies

The following key dependencies are used in this application:

- [**aho-corasick**](https://github.com/robert-bor/aho-corasick): an implementation library of the [Aho-Corasick algorithm](https://en.wikipedia.org/wiki/Aho%E2%80%93Corasick_algorithm) for use in looking up taxonomy terms within a piece of input text.
- [**Stanford CoreNLP**](https://github.com/stanfordnlp/CoreNLP): a natural language processing library for use in normalising both taxonomy terms and query text.

## Contributing

Please read [`CONTRIBUTING.md`](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

Contributors should ensure that their code is formatted in a style that is as close to the existing style as possible.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/digirati-co-uk/digirati-taxonomy-manager/tags).

## License

This project is licensed under the Apache 2.0 License - see the [`LICENSE`](LICENSE) file for details
