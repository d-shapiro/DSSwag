# DSSwag

Generates a DSLink for a REST API from an OpenAPI document, using swagger codegen.

## Usage
```
        python .\ds-generator-swagger.py
                (-i <spec file>)
                [(-o <output directory>)]
                [(-n <dslink name>)]
```
### Example

`python .\ds-generator-swagger.py -i C:\Users\Daniel\Documents\example_openapi.json -o C:\Users\Daniel\git\dslink-java-v2-swagger-sample -n dslink-java-v2-swagger-sample`
