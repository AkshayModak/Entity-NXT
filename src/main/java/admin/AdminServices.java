package admin;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import javax.annotation.security.PermitAll;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import one.NextrrUtils;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminServices extends ResourceConfig implements Serializable {
    /**
     * Admin Services to call Helper Methods for Dashboard and related admin
     * modules. USES: Rest API (Jersey)
     */
    private static final long serialVersionUID = 1L;
    public final String className = AdminServices.class.getName();

    public AdminServices() {
        packages("admin");
        register(AuthenticationFilter.class);
        register(LoggingFilter.class);
        register(GsonMessageBodyHandler.class);
    }

    @POST
    @Path("/authenticateUser")
    @PermitAll
    public String authenticateUser(@Context UriInfo uriInfo) {
        DashboardHelper dbHelper = new DashboardHelper();
        String userName = uriInfo.getQueryParameters().getFirst("username");
        String password = uriInfo.getQueryParameters().getFirst("password");
        String result = dbHelper.authenticateUser(userName, password);
        return result;
    }

    @RolesAllowed("ADMIN")
    @POST
    @Path("/getVisits")
    public String getVisits() {
        DashboardHelper dbHelper = new DashboardHelper();
        String result = dbHelper.getVisits();
        return result;
    }

    @RolesAllowed("ADMIN")
    @POST
    @Path("/getTodayAndYesterdayVisits")
    public String getTodayAndYesterdayVisits() {
        DashboardHelper dbHelper = new DashboardHelper();
        Gson gson = new Gson();
        String todaysDate = NextrrUtils.getTodaysDate();
        String todaysResult = dbHelper.getVisitsByDate(todaysDate);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String yesterdaysResult = dbHelper.getVisitsByDate(dateFormat.format(cal.getTime()));

        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> todaysResultMap = gson.fromJson(todaysResult, type);
        Map<String, Object> yesterdaysResultMap = gson.fromJson(yesterdaysResult, type);

        List<Map<String, Object>> todaysResultList = (List<Map<String, Object>>) todaysResultMap.get("result");
        List<Map<String, Object>> yesterdaysResultList = (List<Map<String, Object>>) yesterdaysResultMap.get("result");

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("todaysVisits", todaysResultList.size());
        resultMap.put("yesterdaysVisits", yesterdaysResultList.size());

        return gson.toJson(resultMap);
    }

    @RolesAllowed("ADMIN")
    @POST
    @Path("/getVisitsByCountries")
    public String getVisitsByCountries() {
        DashboardHelper dbHelper = new DashboardHelper();
        String result = dbHelper.getVisitsByCountries();
        return result;
    }

    @RolesAllowed("ADMIN")
    @POST
    @Path("/getModulesDetails")
    public String getModulesDetails() {
        DashboardHelper dbHelper = new DashboardHelper();
        String result = dbHelper.getModulesDetails();
        return result;
    }

    @RolesAllowed("ADMIN")
    @POST
    @Path("/getContent")
    public String getAllContent() {
        DashboardHelper dbHelper = new DashboardHelper();
        String result = dbHelper.getAllContent();
        return result;
    }

    @RolesAllowed("ADMIN")
    @POST
    @Path("/createContent")
    public String createContent(@Context UriInfo uriInfo) {
        DashboardHelper dbHelper = new DashboardHelper();
        String result = dbHelper.createContent(uriInfo.getQueryParameters());
        return result;
    }

    @RolesAllowed("ADMIN")
    @POST
    @Path("/updateContent")
    public String updateContent(@Context UriInfo uriInfo) {
        DashboardHelper dbHelper = new DashboardHelper();
        String result = dbHelper.updateContent(uriInfo.getQueryParameters());
        return result;
    }

    @RolesAllowed("ADMIN")
    @POST
    @Path("/removeContent")
    public String removeContent(@Context UriInfo uriInfo) {
        DashboardHelper dbHelper = new DashboardHelper();
        String result = dbHelper.removeContent(uriInfo.getQueryParameters());
        return result;
    }

    @RolesAllowed("ADMIN")
    @POST
    @Path("/getUserMessages")
    public String getUserMessages() {
        DashboardHelper dbHelper = new DashboardHelper();
        String result = dbHelper.getUserMessages();
        return result;
    }

    @RolesAllowed("ADMIN")
    @POST
    @Path("/markMessageRead")
    public String markMessageRead(@Context UriInfo uriInfo) {
        DashboardHelper dbHelper = new DashboardHelper();
        String result = dbHelper.markMessageRead(uriInfo.getQueryParameters());
        return result;
    }

    @RolesAllowed("ADMIN")
    @POST
    @Path("/removeUserMessage")
    public String removeUserMessage(@Context UriInfo uriInfo) {
        DashboardHelper dbHelper = new DashboardHelper();
        String result = dbHelper.removeUserMessage(uriInfo.getQueryParameters());
        return result;
    }

    @RolesAllowed("ADMIN")
    @POST
    @Path("/getUnreadMessagesCount")
    public String getUnreadMessagesCount() {
        DashboardHelper dbHelper = new DashboardHelper();
        String result = dbHelper.getUnreadMessagesCount();
        return result;
    }
}