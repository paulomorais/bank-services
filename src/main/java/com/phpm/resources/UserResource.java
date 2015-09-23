package com.phpm.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.phpm.model.Transaction;
import com.phpm.model.User;
import com.phpm.services.BankServices;

@Path("/users")
public class UserResource {
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
    public List<User> getUsers() {
		
		return BankServices.getAllUsers();
    }
	
	@GET
	@Path("/{username}")
	@Produces(MediaType.APPLICATION_XML)
    public User getUser(@PathParam("username") String username) {
		
        return BankServices.getUser(username);
    }

	@GET
	@Path("/id/{userId}")
	@Produces(MediaType.APPLICATION_XML)
    public User getUserByUserId(@PathParam("userId") String userId) {
		
        return BankServices.getUserById(userId);
    }
}
