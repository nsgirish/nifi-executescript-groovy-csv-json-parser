# A Groovy script to parse CSV data to JSON

## Summary

### Apache NIFI provides controller services such as CSVReader, JSONRecordSetWriter which help to parse CSV data to JSON records. But CSVReader has following attributes which give issues when the CSV data happens to contain the character assigned to them

- Quote Character
- Escape Character 
