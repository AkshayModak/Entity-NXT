package one;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.servlet.http.HttpServletRequest;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.QueryParam;
import com.google.gson.Gson;

import com.nextrr.helper.Formula1Helper;
import com.nextrr.helper.MoviesServices;

import com.nextrr.helper.ContentHelper;
import com.nextrr.helper.CricketHelper;
import com.nextrr.helper.FantasyCricketHelper;
import com.nextrr.helper.GenericHelper;
import one.DatabaseUtils;

@Path("/UserService")
public class UserService extends ResourceConfig implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public final String className = UserService.class.getName();

    public UserService(@Context HttpServletRequest requestContext, @Context SecurityContext context) {
        recordVisit(requestContext, context);
    }

    /* Generic User Services */
    @POST
    @Path("/getCountryAssoc")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String getCountryAssoc(@QueryParam("sports_type_id") String sports_type_id) {
        GenericHelper genericHelper = new GenericHelper();
        String result = (String) genericHelper.getCountryAssoc(sports_type_id, "GSON");
        return result;
    }

    /* Formula1 User Services */
    @POST
    @Path("/getFormula1Schedule")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String getFormula1Schedule() {
        Formula1Helper f1Helper = new Formula1Helper();
        String result = f1Helper.getFormula1Schedule();
        return result;
    }

    @POST
    @Path("/getFormula1ToEdit")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String getFormula1ToEdit() {
        Formula1Helper f1Helper = new Formula1Helper();
        String result = f1Helper.getFormula1ToEdit();
        return result;
    }

    @POST
    @Path("/updateF1Practice")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @PermitAll
    public String setF1Practice(@Context UriInfo uriInfo) {
        Formula1Helper f1Helper = new Formula1Helper();
        String result = f1Helper.updateF1Practice(uriInfo.getQueryParameters());
        return result;
    }

    @POST
    @Path("/setF1Schedule")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String setF1Schedule(@Context UriInfo uriInfo) {
        Formula1Helper f1Helper = new Formula1Helper();
        String result = f1Helper.setF1Schedule(uriInfo.getQueryParameters());
        return result;
    }

    @POST
    @Path("/removeF1Schedule")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String removeF1Schedule(@Context UriInfo uriInfo) {
        Formula1Helper f1Helper = new Formula1Helper();
        String result = f1Helper.removeF1Schedule(uriInfo.getQueryParameters());
        return result;
    }

    @POST
    @Path("/getMovies")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String getMovies(@Context UriInfo uriInfo) {
        MoviesServices movieServices = new MoviesServices();
        String result = movieServices.getMovies(uriInfo.getQueryParameters());
        return result;
    }

    @POST
    @Path("/getMoviesToEdit")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String getMoviesToEdit() {
        MoviesServices movieServices = new MoviesServices();
        String result = movieServices.getMoviesToEdit();
        return result;
    }

    @POST
    @Path("/setMovie")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String setMovie(@Context UriInfo uriInfo) {
        MoviesServices movieServices = new MoviesServices();
        String result = movieServices.setMovie(uriInfo.getQueryParameters());
        return result;
    }

    @POST
    @Path("/removeMovie")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String removeMovie(@Context UriInfo uriInfo) {
        MoviesServices movieServices = new MoviesServices();
        String result = movieServices.removeMovie(uriInfo.getQueryParameters());
        return result;
    }

    @POST
    @Path("/updateMovie")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String updateMovie(@Context UriInfo uriInfo) {
        MoviesServices movieServices = new MoviesServices();
        String result = movieServices.updateMovie(uriInfo.getQueryParameters());
        return result;
    }

    @POST
    @Path("/setCricket")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String setCricket(@Context UriInfo uriInfo) {
        CricketHelper cricketHelper = new CricketHelper();
        String result = cricketHelper.setCricket(uriInfo.getQueryParameters());
        return result;
    }

    @POST
    @Path("/getIntlCricket")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String getIntlCricket() {
        CricketHelper cricketHelper = new CricketHelper();
        String result = cricketHelper.getIntlCricket();
        return result;
    }

    @POST
    @Path("/getIntlCricketToDisplay")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String getIntlCricketToDisplay() {
        CricketHelper cricketHelper = new CricketHelper();
        String result = cricketHelper.getIntlCricketToDisplay();
        return result;
    }

    @POST
    @Path("/getCricketCountries")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String getCricketCountries() {
        GenericHelper genericHelper = new GenericHelper();
        String result = genericHelper.getCountriesBySport("CRICKET");
        return result;
    }

    @POST
    @Path("/getCricketMatchTypes")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String getCricketMatchTypes() {
        DatabaseUtils dbUtils = new DatabaseUtils();
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("sports_type_id", "CRICKET");
        Map<String, Object> resultMap = dbUtils.getEntityDataWithConditions("sports_child_type", queryParams);
        return new Gson().toJson(resultMap);
    }

    @POST
    @Path("/removeCricket")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String removeCricket(@Context UriInfo uriInfo) {
        CricketHelper cricketHelper = new CricketHelper();
        String result = cricketHelper.removeCricket(uriInfo.getQueryParameters());
        return result;
    }

    @POST
    @Path("/updateCricket")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String updateCricket(@Context UriInfo uriInfo) {
        CricketHelper cricketHelper = new CricketHelper();
        String result = cricketHelper.updateCricket(uriInfo.getQueryParameters());
        return result;
    }

    @POST
    @Path("/getCricketLeagues")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String getCricketLeagues() {
        CricketHelper cricketHelper = new CricketHelper();
        String result = cricketHelper.getCricketLeagues();
        return result;
    }

    @POST
    @Path("/getAllRawCricketLeagues")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String getAllRawCricketLeagues() {
        CricketHelper cricketHelper = new CricketHelper();
        String result = cricketHelper.getAllRawCricketLeagues();
        return result;
    }

    @POST
    @Path("/addRemoveSportsLeague")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String addRemoveSportsLeague(@Context UriInfo uriInfo) {
        GenericHelper genericHelper = new GenericHelper();
        String sports_id = uriInfo.getQueryParameters().getFirst("sports_league_id");
        String result = null;

        if (DefaultObjects.isNotEmpty(sports_id)) {
            result = genericHelper.removeSportsLeague(uriInfo.getQueryParameters());
        } else {
            result = genericHelper.addSportsLeague(uriInfo.getQueryParameters());
        }

        return result;
    }

    @POST
    @Path("/getFantasyCricketPlayers")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String getFantasyCricketPlayers() {
        FantasyCricketHelper fcHelper = new FantasyCricketHelper();
        String result = fcHelper.getAllPlayers();
        return result;
    }

    @POST
    @Path("/getFantasyCricketResult")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String getFantasyCricketResult(@Context UriInfo uriInfo) {
        FantasyCricketHelper fcHelper = new FantasyCricketHelper();
        String result = fcHelper.playCricket(uriInfo.getQueryParameters());
        return result;
    }

    @POST
    @Path("/setFantasyCricketRecord")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String setFantasyCricketRecord(@Context UriInfo uriInfo) {
        FantasyCricketHelper fcHelper = new FantasyCricketHelper();
        String result = fcHelper.setFantasyCricketRecord(uriInfo.getQueryParameters());
        return result;
    }

    @POST
    @Path("/removeFantasyCricketRecord")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String removeFantasyCricketRecord(@Context UriInfo uriInfo) {
        FantasyCricketHelper fcHelper = new FantasyCricketHelper();
        String result = fcHelper.removeFantasyCricketRecord(uriInfo.getQueryParameters());
        return result;
    }

    @POST
    @Path("/updateFantasyCricket")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String updateFantasyCricket(@Context UriInfo uriInfo) {
        FantasyCricketHelper fcHelper = new FantasyCricketHelper();
        String result = fcHelper.updateFantasyCricket(uriInfo.getQueryParameters());
        return result;
    }

    @POST
    @Path("/setPlayAgainst")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String setPlayAgainst(@Context UriInfo uriInfo) {
        FantasyCricketHelper fcHelper = new FantasyCricketHelper();
        String result = fcHelper.setPlayAgainst(uriInfo.getQueryParameters());
        return result;
    }

    @POST
    @Path("/setMessage")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String setMessage(@Context UriInfo uriInfo) {
        ContentHelper contentHelper = new ContentHelper();
        String result = contentHelper.setMessage(uriInfo.getQueryParameters());
        return result;
    }

    @POST
    @Path("/getContentByCondition")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String getContentByCondition(@Context UriInfo uriInfo) {
        ContentHelper contentHelper = new ContentHelper();
        String result = contentHelper.getContentByCondition(uriInfo.getQueryParameters());
        return result;
    }

    // Record every visit. for Analytics purpose.
    private void recordVisit(@Context HttpServletRequest requestContext, @Context SecurityContext context) {
        Visit visit = new Visit();
        visit.setVisit(requestContext);
    }
}
