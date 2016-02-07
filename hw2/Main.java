import java.util.*;
import java.util.regex.*;
import java.io.*;

public class Main
{
   //Open File
   //Scan a document
   //Process a document
   //Insert into sorted structure
   //When no more docs, Write to disk
   //Retreive Data asked for
    
   public static void main(String [] args)
   {
   TreeMap postingsList = new TreeMap();
      File inFile = new File(args[0]);
      fileScan(inFile, postingsList);
   }

   public static void fileScan(File corpus, TreeMap postingsList)
   {
   try{
      Scanner sc = new Scanner(corpus);
      int curDoc = 0;
      String docText = "";
      String curLine = "";
      Pattern endDoc = Pattern.compile("</P>");
      Matcher docCheck;
      while(sc.hasNextLine())
      {
         curLine = sc.nextLine();
         docCheck = endDoc.matcher(curLine);
         if(docCheck.find())
         {
            docProcess(docText, curDoc, postingsList);
            curDoc++;
            docText = "";
            sc.nextLine();
         } 
         else
         {
            docText.concat(curLine);
         }
      }
   }
   catch(Exception ex)
   {
   System.err.println(ex);
   }
}
   public static void docProcess(String body, int docNum, TreeMap postingsList)
   {
      String validToken = "";
      int validTokenHash = validToken.hashCode();
      int freq = 0;
   
      catalogueToken(validTokenHash, docNum, freq, postingsList);
   }

   public static void catalogueToken(int hashToken, int docNum, int tokenFreq, TreeMap postingsList)
   {
      LinkedList tokenPostings = (LinkedList)postingsList.get(hashToken);
      int[] posting = {docNum, tokenFreq};
      if(tokenPostings == null)
      {
         tokenPostings = new LinkedList();
      }
      tokenPostings.push(posting);
      postingsList.put(hashToken, tokenPostings);
   }

   public static void writeToDisk(TreeMap postings) throws Exception
   {
      byte[] results = serialize(postings);
      write(results, "invList");
   }

   public static void retreiveData(String query) throws Exception
   {
      TreeMap postings = new TreeMap();
      LinkedList data = new LinkedList();
      byte[] invListBytes = read("invList");
      int queryHash = query.hashCode();
      postings = (TreeMap)deserialize(invListBytes);
      data = (LinkedList)postings.get(queryHash);
   }
   
   public static byte[] read(String aInputFileName){
    System.out.println("Reading in binary file named : " + aInputFileName);
    File file = new File(aInputFileName);
    System.out.println("File size: " + file.length());
    byte[] result = new byte[(int)file.length()];
    try {
      InputStream input = null;
      try {
        int totalBytesRead = 0;
        input = new BufferedInputStream(new FileInputStream(file));
        while(totalBytesRead < result.length){
          int bytesRemaining = result.length - totalBytesRead;
          //input.read() returns -1, 0, or more :
          int bytesRead = input.read(result, totalBytesRead, bytesRemaining); 
          if (bytesRead > 0){
            totalBytesRead = totalBytesRead + bytesRead;
          }
        }
        /*
         the above style is a bit tricky: it places bytes into the 'result' array; 
         'result' is an output parameter;
         the while loop usually has a single iteration only.
        */
        System.out.println("Num bytes read: " + totalBytesRead);
      }
      finally {
        System.out.println("Closing input stream.");
        input.close();
      }
    }
    catch (FileNotFoundException ex) {
      System.out.println("File not found.");
    }
    catch (IOException ex) {
      System.out.println(ex);
    }
    return result;
  }
  
   public static void write(byte[] aInput, String aOutputFileName){
      System.out.println("Writing binary file...");
      try {
         OutputStream output = null;
         try {
            output = new BufferedOutputStream(new FileOutputStream(aOutputFileName));
            output.write(aInput);
         }
         finally {
            output.close();
         }
      }
      catch(FileNotFoundException ex){
         System.err.println("File not found.");
      }
      catch(IOException ex){
         System.err.println(ex);
      }
   }

   public static byte[] serialize(Object obj) throws IOException {
      try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
         try(ObjectOutputStream o = new ObjectOutputStream(b)){
            o.writeObject(obj);
         }
         return b.toByteArray();
      }
   }

   public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
      try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
         try(ObjectInputStream o = new ObjectInputStream(b)){
            return o.readObject();
         }
      }
   }
}