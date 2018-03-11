package com.mkyong.rest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.lucene.queryParser.ParseException;

//http://localhost:8080/MERCURYFORMS/rest/
@Path("/")
public class MessageRestService {
    
    private static final String URLFORMSPREENCHIDOS =  "/home/afonso/RESTfulExample/src/main/resources/forms.preenchidos";
    private static final String URLFORMSVAZIOS = "/home/afonso/RESTfulExample/src/main/resources/forms.vazios";
    private static final String URLFORMSINDEX = "/home/afonso/RESTfulExample/src/main/resources/forms.index";

    private LuceneTester tester = new LuceneTester(URLFORMSPREENCHIDOS, URLFORMSINDEX);
//    private boolean flag = true;
    
    private void index(){
            LuceneTester tester2 = new LuceneTester(URLFORMSPREENCHIDOS, URLFORMSINDEX);
        try {
            tester.createIndex();
        } catch (IOException ex) {
            Logger.getLogger(MessageRestService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @Path("/welcome")
    @GET
    public Response welcome() throws IOException{
        index();
        return Response.ok("<h1>Seja bem-vindo(a) ao Servidor do MercuryForms (COM INDEXAÇÃO MODIFICADA)</h1>", MediaType.TEXT_HTML).build();
    }
    
    
    @Path("teste/{param}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getTesteResponse(@PathParam("param") String id) throws ParseException, IOException {
        HashMap<Integer, String> hm = new HashMap<Integer, String>();
        if(id.equals("123")){
            hm.put(1, "ok - "+id);
        }else{
//            hm.put(1, null);
                hm=null;
        }
        String json = new Gson().toJson(hm);
        return Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
    }
    
    @Path("search/{param}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getSearchResponse(@PathParam("param") String query) throws ParseException, IOException {
        HashMap<Integer, HashMap> hm = tester.pesquisar(query);
        String json = new Gson().toJson(hm);
        return Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
    }

    @Path("/download")
    @POST
    public Response downloadForm(String jsonString) {
        JsonElement root = new JsonParser().parse(jsonString);    
        String path = root.getAsJsonObject().get("path").getAsString();
        File fileToSend = new File(path);
        return Response.ok(fileToSend, "application/zip;charset=UTF-8").build();

    }
    
    @Path("/gettemplates")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getEmptyForms(){
        List<String> results = new ArrayList<String>();
        File[] files = new File(URLFORMSVAZIOS).listFiles();        
        for (File file : files) {
            if (file.isFile()) {
                boolean temp = file.getName().endsWith(".xfdl");
                if(temp) results.add(file.getName());
            }
        }
        HashMap<Integer, String> hm = new HashMap();
        for(int i=0; i<results.size(); i++){
            hm.put(i, results.get(i));
        }
        String json = new Gson().toJson(hm);
        return Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
    }
    
    @Path("/template")
    @POST
    public Response addTemplate (String content) throws IOException {
            String[] lista = content.split(",");
            BufferedWriter output = null;
            String ok = URLFORMSVAZIOS+"/"+lista[0];
            try {
                File file = new File(ok);
                if(file.exists() && !file.isDirectory()) { 
                    ok = "already_exist_name";
                    HashMap<String, String> hm = new HashMap();
                    hm.put("response", ok);
                    String json = new Gson().toJson(hm);
                    return Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
                }
                output = new BufferedWriter(new FileWriter(file));
                output.write(lista[1]);
            } catch ( IOException e ) {
                e.printStackTrace();
            } finally {
                if ( output != null ) output.close();
        }
            HashMap<String, String> hm = new HashMap();
            hm.put("response", ok);
            String json = new Gson().toJson(hm);
            return Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
    }
    
    @Path("/upload")
    @POST
    public Response uploadForm(String content) throws IOException {
            String[] lista = content.split(",");
            BufferedWriter output = null;
            String ok = URLFORMSPREENCHIDOS+"/"+new Date().getTime()+"-"+lista[0];
            try {
                File file = new File(ok);
                if(file.exists() && !file.isDirectory()) { 
                    ok = "error";
//                      file = new File(file.getName()+"");
                }
                output = new BufferedWriter(new FileWriter(file));
                output.write(lista[1]);
            } catch ( IOException e ) {
                e.printStackTrace();
            } finally {
                if ( output != null ) output.close();
        }
            HashMap<String, String> hm = new HashMap();
            hm.put("response2", ok);
            String json = new Gson().toJson(hm);
            index();
            return Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
    }
    @Path("/uploadTemplate")
    @POST
    public Response uploadForm2(String content) throws IOException {
            String[] lista = content.split(",");
            BufferedWriter output = null;
            String ok = URLFORMSVAZIOS+"/"+new Date().getTime()+"-"+lista[0];
            try {
                File file = new File(ok);
                if(file.exists() && !file.isDirectory()) { 
                    ok = "error";
//                      file = new File(file.getName()+"");
                }
                output = new BufferedWriter(new FileWriter(file));
                output.write(lista[1]);
            } catch ( IOException e ) {
                e.printStackTrace();
            } finally {
                if ( output != null ) output.close();
        }
            HashMap<String, String> hm = new HashMap();
            hm.put("response2", ok);
            String json = new Gson().toJson(hm);
            index();
            return Response.ok(json, MediaType.APPLICATION_JSON_TYPE).build();
    }
}
