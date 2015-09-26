package com.phm.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.phm.dao.UserDAO;
import com.phm.model.User;

/**
 * Restfull API for User Retrieval
 * @author Paulo
 *
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
	
	UserDAO dao = new UserDAO();
	
	/**
	 * Method HTTP GET to retrieve a list of accounts
	 * @param startAt Start index to filter from (Pages)
	 * @param size Size of the page to filter (default = 10)
	 * @return List of Users
	 */
	@GET
    public List<User> getUsers(@QueryParam("startAt") int startAt, @QueryParam("size") int size) {
		return dao.list(startAt, size);
    }
	
	/**
	 * Method HTTP GET to retrieve the row count of a query
	 * @return size of result of the query
	 */
	@GET
	@Path("/rowCount")
	@Produces(MediaType.TEXT_PLAIN)
    public long rowCount() {
		return dao.rowCountlist();
    }
	
	/**
	 * Method HTTP GET to validate the User credentials
	 * @param username Username of the User
	 * @param password Password of the User
	 * @return User that match the login conditions
	 */
	@GET
	@Path("/{username}")
    public User getUser(@PathParam("username") String username, @QueryParam("password") String password) {
		if (password == null){
			return dao.findByUsername(username);
		}
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		return dao.login(user);
    }

}
