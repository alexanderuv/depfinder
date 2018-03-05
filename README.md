# depfinder
Finds dependencies in a jar/class file at the method level

You can specify class or jar files as input, and it will output a CSV files with dependencies found based on the parameters passed to the program.

### Usage

``java -jar depfinder.jar (input file or folder) [additional flags]``

### Flags

| Flag |Description|
|------|------------|
|**-t**|Target package name or class name to look for|
|**-e**|Exclusions (excludes jars or classes from input)|
|**-i**|Ignore usages of a specific package name or class prefix|
|**-v**|Verbose output|
|**-o**|Output file (csv)|
