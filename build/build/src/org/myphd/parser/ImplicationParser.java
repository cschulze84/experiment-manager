package org.myphd.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ImplicationParser {
	
	List<Data> data = new ArrayList<>();

	public static void main(String[] args) {
		ImplicationParser parser = new ImplicationParser();
		List<Data> data = parser.parse("mo_output.csv");
	for (Data data2 : data) {
		System.out.println(data2.toString());
	}
	}
	
	public List<Data> getData() {
		return data;
	}

	public List<Data> parse(String file) {
		List<Data> data = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			for(String line; (line = br.readLine()) != null; ) {
				line = line.trim();
				if(line.isEmpty()){
					continue;
				}
				
				Data dataPoint = Data.parse(line);
				data.add(dataPoint);
				//System.out.println(dataPoint);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
		
	}
	
	
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}

	public List<Data> parseZ3(String file) {
		List<Data> data = new LinkedList<>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(file)));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String implication= null;
		
		//String content = readFile(file, Charset.defaultCharset());
		
		try {
			for(String line; (line = br.readLine()) != null; ) {
				line = line.trim();
				if(line.startsWith("-----")){
					continue;
				}
				else if(line.startsWith("Implies(")){
					if(implication != null){
						//System.out.println(implication);
						Data dataPoint = Data.parseZ3(implication);
						data.add(dataPoint);
						implication = "";
						//System.out.println(dataPoint.toString());
					}
					else{
						implication = "";
					}
					implication += line;
				}
				else if(line.isEmpty()){
					if(implication != null){
						//System.out.println("test2: " + implication);
						Data dataPoint = Data.parseZ3(implication);
						data.add(dataPoint);
						implication = "";
					}
					else{
						implication = "";
					}
					break;
				}
				else{
					implication += line;
				}	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
}
