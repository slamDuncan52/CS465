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
      try{
         TreeMap postingsList = new TreeMap();
         File inFile = new File(args[0]);
         fileScan(inFile, postingsList);
         writeToDisk(postingsList);
         retreiveData("Francisco", true);
         retreiveData("midway", true);
         retreiveData("paddy", true);
         retreiveData("Kremlin", false);
         retreiveData("KGB", false);
         retreiveData("Khrushchev", false);
      }
      catch(Exception ex)
      {
         System.err.println(ex);
      }
   }

   public static void fileScan(File corpus, TreeMap postingsList) throws Exception
   {
      BufferedReader br = new BufferedReader(new FileReader(corpus));
      int curDoc = 0;
      String docText = "";
      String curLine = "";
      Pattern endDoc = Pattern.compile("</P>");
      Matcher docCheck;
      br.readLine();
      while((curLine = br.readLine()) != null)
      {
         docCheck = endDoc.matcher(curLine);
         if(docCheck.find())
         { 
            docProcess(docText.split(" "), curDoc, postingsList);
            curDoc++;
            docText = "";
            br.readLine();
         } 
         else
         {
            docText = docText.concat(" ").concat(curLine);
         }
      }
   }
   public static void docProcess(String[] body, int docNum, TreeMap postingsList)
   {
      String validToken = "";
      int validTokenHash = validToken.hashCode();
      int freq = 1;
      int count = 0;
      int[] bodyHash = new int[body.length];
      for(count = 0; count < body.length; count++)
      {
         bodyHash[count] = body[count].hashCode();
      }
      Arrays.sort(bodyHash);
      count = 0;
      while(count < bodyHash.length)
      {
         if((count+1 < bodyHash.length) && (bodyHash[count] == bodyHash[count+1]))
         {
            freq++;
            count++;
         } 
         else
         {
            catalogueToken(bodyHash[count], docNum, freq, postingsList);
            freq = 1;
            count++;
         }
      }
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

   public static void retreiveData(String query, boolean freqAndPost) throws Exception
   {
      TreeMap postings = new TreeMap();
      LinkedList data = new LinkedList();
      byte[] invListBytes = read("invList");
      int queryHash = query.hashCode();
      int[] tempHolder = new int[2];
      postings = (TreeMap)deserialize(invListBytes);
      data = (LinkedList)postings.get(queryHash);
      System.out.println("Data For: " + query);
      System.out.println("Document Frequency: "+ data.size());
      if(freqAndPost)
      {
         for(Object cur: data)
         {
            tempHolder = (int[])cur;
            System.out.println("Posting for document " + tempHolder[0] + " appearing " + tempHolder[1] + " times");
         }
      }
   }

   public static byte[] read(String aInputFileName){
    //System.out.println("Reading in binary file named : " + aInputFileName);
      File file = new File(aInputFileName);
    //System.out.println("File size: " + file.length());
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
         //System.out.println("Num bytes read: " + totalBytesRead);
         }
         finally {
         //System.out.println("Closing input stream.");
            input.close();
         }
      }
      catch (FileNotFoundException ex) {
      //System.out.println("File not found.");
      }
      catch (IOException ex) {
      //System.out.println(ex);
      }
      return result;
   }
  
   public static void write(byte[] aInput, String aOutputFileName){
      //System.out.println("Writing binary file...");
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
         //System.err.println("File not found.");
      }
      catch(IOException ex){
         //System.err.println(ex);
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
