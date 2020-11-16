# A Groovy script to parse CSV data to JSON

## Summary

### Apache NIFI provides controller services such as CSVReader, JSONRecordSetWriter which help to parse CSV data to JSON records. But CSVReader has following attributes which give runtime errors when the CSV data happens to contain the value assigned to them

- Quote Character (default value is ")
- Escape Character (default value is \)

### These attributes cannot be assigned as empty strings. 

### In these scenarios we can use the native libraries of groovy language to write a simple CSV to JSON parser and run it via NIFI ExecuteScript processor 

