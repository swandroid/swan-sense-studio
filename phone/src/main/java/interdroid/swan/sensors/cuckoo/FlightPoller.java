package interdroid.swan.sensors.cuckoo;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.qpxExpress.QPXExpress;
import com.google.api.services.qpxExpress.QPXExpressRequestInitializer;
import com.google.api.services.qpxExpress.model.FlightInfo;
import com.google.api.services.qpxExpress.model.LegInfo;
import com.google.api.services.qpxExpress.model.PassengerCounts;
import com.google.api.services.qpxExpress.model.PricingInfo;
import com.google.api.services.qpxExpress.model.SegmentInfo;
import com.google.api.services.qpxExpress.model.SliceInfo;
import com.google.api.services.qpxExpress.model.SliceInput;
import com.google.api.services.qpxExpress.model.TripOption;
import com.google.api.services.qpxExpress.model.TripOptionsRequest;
import com.google.api.services.qpxExpress.model.TripsSearchRequest;
import com.google.api.services.qpxExpress.model.TripsSearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.client.json.jackson2.JacksonFactory;

import interdroid.swan.cuckoo_sensors.CuckooPoller;

public class FlightPoller implements CuckooPoller {

    // Google Console server API key
    private static final String API_KEY = "AIzaSyCJoZbF36XS-2I83oe5ahuoEBRuqcU1u7M";

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final String SOURCE = "source";
    private static final String DESTINATION = "destination";
    private static final String DATE = "flight_date";
    private static final String MAX_STOPS = "max_stops";
    private static final String MAX_PRICE = "max_price";
    private static final String MAX_CONNECTION_DURATION = "max_connection_duration";
    private static final String FLIGHT_CABIN_TYPE = "flight_cabin_type";
    private static final String PRICE = "price";

    @Override
    public Map<String, Object> poll(String s, Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        String fromStation = (String) map.get(DESTINATION);
        String toStation = (String) map.get(SOURCE);
        String date = (String) map.get(DATE);
        Integer maxStops = map.get(MAX_STOPS) != null ? Integer.valueOf((String) map.get(MAX_STOPS)) : null;
        Integer maxConnectionDuration = map.get(MAX_CONNECTION_DURATION) != null ? Integer.valueOf((String) map.get(MAX_CONNECTION_DURATION)) : null;
        String preferredCabin = (String) map.get(FLIGHT_CABIN_TYPE);
        String maxPrice = (String) map.get(MAX_PRICE);

        Pattern pattern = Pattern.compile("[A-Z]{3}\\d+(\\.\\d+)?");
        double minPrice = Integer.MAX_VALUE;
        String resultPrice = "There are no flights with this filter";

        try {
            NetHttpTransport httpTransport = new NetHttpTransport();

            PassengerCounts passengers= new PassengerCounts();
            passengers.setAdultCount(1);

            List<SliceInput> slices = new ArrayList<>();

            SliceInput slice = new SliceInput();
            slice.setOrigin(fromStation);
            slice.setDestination(toStation);
            slice.setDate(date);

            if (maxStops != null)
                slice.setMaxStops(maxStops);

            if (maxConnectionDuration != null)
                slice.setMaxConnectionDuration(maxConnectionDuration);

            if (preferredCabin != null)
                slice.setPreferredCabin(preferredCabin);

            slices.add(slice);

            TripOptionsRequest request = new TripOptionsRequest();
            request.setSolutions(10);
            request.setPassengers(passengers);

            if (maxPrice != null)
                request.setMaxPrice(maxPrice);
            request.setSlice(slices);

            TripsSearchRequest parameters = new TripsSearchRequest();
            parameters.setRequest(request);
            QPXExpress qpXExpress= new QPXExpress.Builder(httpTransport, JSON_FACTORY, null)
                    .setApplicationName("SWAN")
                    .setGoogleClientRequestInitializer(new QPXExpressRequestInitializer(API_KEY))
                    .build();

            TripsSearchResponse list = qpXExpress.trips().search(parameters).execute();
            List<TripOption> tripResults = list.getTrips().getTripOption();

            String id;

            for(int i = 0; i < tripResults.size(); i ++){
                //Trip Option ID
                id = tripResults.get(i).getId();
                System.out.println("id "+id);

                //Slice
                List<SliceInfo> sliceInfo= tripResults.get(i).getSlice();
                for(int j=0; j<sliceInfo.size(); j++){
                    int duration= sliceInfo.get(j).getDuration();
                    System.out.println("duration "+duration);
                    List<SegmentInfo> segInfo= sliceInfo.get(j).getSegment();
                    for(int k=0; k<segInfo.size(); k++){
                        String bookingCode= segInfo.get(k).getBookingCode();
                        System.out.println("bookingCode "+bookingCode);
                        FlightInfo flightInfo=segInfo.get(k).getFlight();
                        String flightNum= flightInfo.getNumber();
                        System.out.println("flightNum "+flightNum);
                        String flightCarrier= flightInfo.getCarrier();
                        System.out.println("flightCarrier "+flightCarrier);
                        List<LegInfo> leg=segInfo.get(k).getLeg();
                        for(int l=0; l<leg.size(); l++){
                            String aircraft= leg.get(l).getAircraft();
                            System.out.println("aircraft "+aircraft);
                            String arrivalTime= leg.get(l).getArrivalTime();
                            System.out.println("arrivalTime "+arrivalTime);
                            String departTime=leg.get(l).getDepartureTime();
                            System.out.println("departTime "+departTime);
                            String dest=leg.get(l).getDestination();
                            System.out.println("Destination "+dest);
                            String destTer= leg.get(l).getDestinationTerminal();
                            System.out.println("DestTer "+destTer);
                            String origin=leg.get(l).getOrigin();
                            System.out.println("origun "+origin);
                            String originTer=leg.get(l).getOriginTerminal();
                            System.out.println("OriginTer "+originTer);
                            int durationLeg= leg.get(l).getDuration();
                            System.out.println("durationleg "+durationLeg);
                            int mil= leg.get(l).getMileage();
                            System.out.println("Milleage "+mil);

                        }

                    }
                }

                //Pricing
                List<PricingInfo> priceInfo= tripResults.get(i).getPricing();

                for(int p = 0; p < priceInfo.size(); p ++){
                    String price = priceInfo.get(p).getSaleTotal();
                    System.out.println("Price "+price);

                    Matcher matcher = pattern.matcher(price);
                    if (matcher.find()) {
                        String priceNumber = price.substring(3);
                        if (minPrice > Double.valueOf(priceNumber)) {
                            minPrice = Double.valueOf(priceNumber);
                            resultPrice = price;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        result.put(PRICE, resultPrice);
        return result;
    }

    @Override
    public long getInterval(Map<String, Object> map, boolean remote) {
        return 288 * 6 * 1000; // every 28,8 mins (we are only allowed to do 50 requests per day)
    }
}
