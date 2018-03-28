# DSSwag

Generates a DSLink for a REST API from an OpenAPI document, using swagger codegen.

## Usage
```
        python .\ds-generator-swagger.py
                (-i <spec file>)
                [(-o <output directory>)]
                [(-n <dslink name>)]
                [(-i <swagger codegen version>)]
```
### Examples

`python .\ds-generator-swagger.py -i C:\Users\Daniel\Documents\example_openapi.json -o C:\Users\Daniel\git\dslink-java-v2-swagger-sample -n dslink-java-v2-swagger-sample`

`python .\ds-generator-swagger.py -i example_openapi.json -o C:\Users\Daniel\git\dslink-java-v2-swagger-sample -n dslink-java-v2-swagger-sample -v 3`

### Swagger Codegen Version

By default, this project uses the current stable version of swagger codegen (2.2.1). However, this version only supports version 2 of OpenAPI. If you set the flag `-v 3`, it'll use a beta version (3.0.0-20180323.182452-44) of the codegen, which is supposed to support OpenAPI 3. 
