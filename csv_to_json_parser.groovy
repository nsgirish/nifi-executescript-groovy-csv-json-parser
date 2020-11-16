//IMPORTANT NOTE: ***THIS PARSER ASSUMES ALL FIELDS OF CSV RECORD(S) IS OF STRING DATA TYPE ***

import org.apache.commons.io.IOUtils
import java.nio.charset.StandardCharsets
import groovy.json.JsonOutput 

//get current flowfile
def flowFile = session.get()
if(!flowFile) return

def rowInfo = "" //to store current row being processed

try 
{
  def rowDelimiter = rowDelimiter.value // dynamic executescript attribute containing regex pattern. eg /\n/
  def colDelimiter = colDelimiter.value //dynamic executescript attribute containing regex pattern.
  
  flowFile = session.write(flowFile,{inputStream,outputStream -> 
    //read csv data from flow file
    def csvData = IOUtils.toString(inputStream,StandardCharsets.UTF_8)
    
    //split csv data by row delimiter
    def rows = csvData.split(rowDelimiter)
        
    //get columsn headers
    def colHeaders = rows[0].split(colDelimiter)
    
    def lstOutput = [] //init groovy list object
    
    for(rowIndex=1;rowIndex <rows.size();rowIndex++)
    {
      //current record
      def row = rows[rowIndex].split(colDelimiter)
      
      //store current row being processed for debugging purpose (optional)
      rowInfo = "current row ${rowIndex} - ${rows[rowIndex]}"
      
      def mp = [:] // groovy map object to hold current record in key / value pairs
      
      //iterate columsn of current row
      for(colIndex=0;colIndex < colHeaders.size();colIndex ++) 
      {
        //current column name
        def colName = colHeaders[colIndex]
        
        if(colIndex < row.size()) //to handle bug in groovy split() which ignores last column if its empty
        {
          mp[colName] =  row[colIndex]
        }
        else 
        {
          mp[colName] = ""
        } 
      }
      
      //add current record to groovy list
      lstOutput.add(mp)
    }
    
    //convert groovy list object to json string
    def jsonOutput = JsonOutput.toJson(lstOutput)
    
    //pretty print json 
    jsonOutput = JsonOutput.prettyPrint(jsonOutput)
    
    //write json string to output stream of flow file
    outputStream.write(jsonOutput.getBytes(StandardCharsets.UTF_8)
    
  } as StreamCallback)
  
  //transfer flow file to success 
  session.transfer(flowFile,REL_SUCCESS)
  
}
catch(e)
{
  //add attributes containing error details 
  flowFile = session.putAttribute(flowFile,"csv_json_parse_error",e.toString())
  flowFile = session.putAttribute(flowFile,"current_row",rowInfo)
  
  //transfer flowfile to failure
  session.transfer(flowFile,REL_FAILURE)
}
