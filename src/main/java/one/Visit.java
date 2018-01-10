package one;

import java.io.File;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

//reference -- https://www.mkyong.com/java/java-find-location-using-ip-address/
//api -- https://github.com/maxmind/geoip-api-java
public class Visit {

    public void setVisit(HttpServletRequest request) {
        String nextrr_home = System.getProperty("user.dir");
        String geoDataPath = nextrr_home + "/ext-data/geoData/GeoLite2-City.mmdb";

        String ipAddressRequestCameFrom = request.getHeader("x-forwarded-for");
        Map<String, String> result = new HashMap<>();

        if (ipAddressRequestCameFrom == null) {
            ipAddressRequestCameFrom = request.getRemoteAddr();
        }
        String ipAddressesRequestCameFrom[] = ipAddressRequestCameFrom.split(",");
        ipAddressRequestCameFrom = ipAddressesRequestCameFrom[0];

        Map<String, Object> location = null;
        Calendar cal = Calendar.getInstance();
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String requestDate = sdf.format(cal.getTime());
        sdf = new SimpleDateFormat("hh:mm a");
        String requestTime = sdf.format(cal.getTime());

        String requestUri = request.getRequestURI();
        location = getLocation(ipAddressRequestCameFrom, geoDataPath);
        if (location.isEmpty()) {
            System.out.println("ERROR: Visit Location is Null");
            return;
        }
        if (location != null) {
            DatabaseUtils dbUtils = new DatabaseUtils();
            Map<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("requestDate", requestDate);
            queryParams.put("userIp", ipAddressRequestCameFrom);
            queryParams.put("requestUri", requestUri);

            Map<String, Object> resultMap = dbUtils.getEntityDataWithConditions("visit", queryParams);
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) resultMap.get("result");

            if (resultList.isEmpty()) {
                Map<String, Object> queryMap = new HashMap<String, Object>();
                queryMap.put("requestUri", requestUri);
                queryMap.put("userIp", ipAddressRequestCameFrom);
                queryMap.put("userCity", location.get("cityName"));
                queryMap.put("userCountry", location.get("countryName"));
                queryMap.put("requestDate", requestDate);
                queryMap.put("requestTime", requestTime);

                dbUtils.runCreateQuery("visit", queryMap);
            } else {
                System.out.println("ALERT: Visit Already Exists");
            }
        }
    }

    public Map<String, Object> getLocation(String ip, String dbLocation) {
        Map<String, Object> geoMap = new HashMap<String, Object>();

        try {
            File database = new File(dbLocation);
            DatabaseReader dbReader = new DatabaseReader.Builder(database).build();

            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = dbReader.city(ipAddress);

            geoMap.put("countryName", response.getCountry().getName());
            geoMap.put("stateName", response.getLeastSpecificSubdivision().getName());
            geoMap.put("cityName", response.getCity().getName());
            geoMap.put("postalCode", response.getPostal().getCode());

        } catch (IOException | GeoIp2Exception e) {
            System.out.println("ERROR: Error while getting location with IP====" + e);
        }
        return geoMap;
    }
}
