package com.schuster.monthlyBills.jersey;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

//import com.att.smp.jbce.bridge.HttpBridge.NakedRestBasics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Path("/")
//public class MonthlyBillsRest extends NakedRestBasics {
public class MonthlyBillsRest {
	private static 		Logger	log = LogManager.getLogger();
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Path("/something")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "- Get a list of categories", description = "Get a list of categories", security = {@SecurityRequirement(name="apiKey")})
	public Response getSomething() {
		
		log.trace("SOMETHING");
		
		mapper.registerModule(new JavaTimeModule());
		
			
		
		return Response.status(Response.Status.EXPECTATION_FAILED)
				.build();
	}
	
	
	@Path("/categories")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "- Get a list of categories", description = "Get a list of categories", security = {@SecurityRequirement(name="apiKey")})
	public Response getCategories() {
		
		log.trace("MBILLS - Rest - get categories - NEWWWWWWW");
		
		mapper.registerModule(new JavaTimeModule());
		
		ArrayList<MBillsCategory> categories;
		categories = MonthlyBills.getAllCategories();
			
		try {
			
			return Response.status(Response.Status.OK)
					//.entity(viewArray.toJSONString())
					.entity(mapper.writeValueAsString(categories))
					.build();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return Response.status(Response.Status.EXPECTATION_FAILED)
				.build();
	}
	
	@Path("/entries/{year}/{month}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "- Get a list of entries for a month", description = "Get a list of entries for given month", security = {@SecurityRequirement(name="apiKey")})
	public Response getEntries(@PathParam("year") int year, @PathParam("month") int month) {
		
		log.trace("MBILLS - Rest - get entries - NEWWWWW");
		
		LocalDate yearMonth = LocalDate.of(year, month, 1);
		
		ArrayList<MBillsEntry> entries = MonthlyBillsDB.getEntriesForMonth(yearMonth);
		
			
		try {
			
			return Response.status(Response.Status.OK)
					//.entity(viewArray.toJSONString())
					.entity(mapper.writeValueAsString(entries))
					.build();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return Response.status(Response.Status.EXPECTATION_FAILED)
				.build();
	}
	
	@Path("/entries")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "- Create an entry", security = {@SecurityRequirement(name="basic")})
	@RequestBody(description = "Create ", required = true, 
					content = @Content(schema=@Schema(implementation = JSONObject.class)))
	
	public Response addRservation(String joString) {
		
		log.trace("CONFERENCE - In addRservation jostring = " + joString);
		
		MBillsEntryInput entryIn;
		
		log.info(joString);
		try {
			entryIn = mapper.readValue(joString,  MBillsEntryInput.class);
			log.trace("Entry = " + entryIn.getKey());
			// Call DB addEntry with EntryIn Object
			MonthlyBillsDB.addEntry(entryIn);
			
			
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			log.error(e.toString());
		}
		
		return Response.status(Response.Status.OK)
				//.entity(viewArray.toJSONString())
				.entity("{}")
				.build();
		
		

	}
	
	
}