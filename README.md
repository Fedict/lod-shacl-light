# lod-shacl-light
Partial implementation of [W3C SHACL validation](https://www.w3.org/TR/shacl/), using RDF4J library.

Violations will be written to the log file

## Usage


```
File shacl = "shacl.ttl";
File data = "data.ttl";

ShaclValidation validator = new ShaclValidator(shacl);
boolean errors = validator.validate(data);
if (errors) {
...
}
```

## Supported targets

- sh:targetClass

## Supported paths

- sh:path

## Supported constraints

- Cardinality: sh:minCount, sh:maxCount
- String-based: sh:minLength, sh:maxLength, sh:languageIn, sh:uniqueLang
- Other: sh:hasValue

## Other

- sh:deactivated