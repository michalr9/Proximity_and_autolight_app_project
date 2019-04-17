package com.michalraq.proximitylightapp.data.estimote;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;
import com.michalraq.proximitylightapp.service.Client;
import com.michalraq.proximitylightapp.view.ServerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Klasa odpowiadająca za sterowanie obserwatorem strefy nadajnika.
 */
public class ProximityContentManager {

    private Context context;
    private EstimoteCloudCredentials cloudCredentials;
    private ProximityObserver.Handler proximityObserverHandler;

    /**
     *
     * @param context kontekst.
     * @param cloudCredentials dane wygenerowane przez chmurę Estimote potrzebne do połączenia aplikacji z usługą.
     */
    public ProximityContentManager(Context context, EstimoteCloudCredentials cloudCredentials) {
        this.context = context;
        this.cloudCredentials = cloudCredentials;
    }

    /**
     * Funkcja uruchamiająca obserwatora. Wraz z definicją obsługiwanych eventów związanych z wejściem i wyjściem ze strefy.
     */
    public void start() {

        ProximityObserver proximityObserver = new ProximityObserverBuilder(context, cloudCredentials)
                .onError(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        Log.e("app", "proximity observer error: " + throwable);
                        return null;
                    }
                })
                .withBalancedPowerMode()
                .build();

        ProximityZone places = new ProximityZoneBuilder()
                .forTag("place")
                .inCustomRange(5.0)
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
            @Override
            public Unit invoke(ProximityZoneContext zoneContext) {
                String place = zoneContext.getAttachments().get("place");
                if (place == null) {
                    place = "unknown";
                }
                //sygnal 1- wlaczanie swiatla
                StringBuilder stringBuilder = new StringBuilder("1");
                stringBuilder.append(place);
                place = stringBuilder.toString();

                Toast.makeText(context, "Włączam światło w " + place, Toast.LENGTH_LONG).show();

                if(ServerManager.isServiceStarted)
                Client.sendMessage(place);

                return null;
            }
        }) .onExit(new Function1<ProximityZoneContext, Unit>() {
            @Override
            public Unit invoke(ProximityZoneContext zoneContext) {
                String place = zoneContext.getAttachments().get("place");
                if (place == null) {
                    place = "unknown";
                }
                StringBuilder stringBuilder = new StringBuilder("0");
                stringBuilder.append(place);
                place = stringBuilder.toString();

                Toast.makeText(context, "Wyłączam światło w " + place, Toast.LENGTH_LONG).show();

                if(ServerManager.isServiceStarted)
                Client.sendMessage(place);


                return null;
            }
        }).build();



        ProximityZone zone = new ProximityZoneBuilder()
                .forTag("proximity-light-4nu")
                .inCustomRange(4.0)
                .onContextChange(new Function1<Set<? extends ProximityZoneContext>, Unit>() {
                    @Override
                    public Unit invoke(Set<? extends ProximityZoneContext> zoneContext) {

                        List<ProximityContent> nearbyContent = new ArrayList<>(zoneContext.size());

                        for (ProximityZoneContext proximityContext : zoneContext) {
                            String title = proximityContext.getAttachments().get("proximity-light-4nu/title");
                            if (title == null) {
                                title = "unknown";
                            }
                            String place = proximityContext.getAttachments().get("place");
                            nearbyContent.add(new ProximityContent(title, place));
                            Log.d("app", "Welcome to " + title +" "+ place );
                 //           Toast.makeText(context, "Włączam światło w " + place, Toast.LENGTH_SHORT).show();

                        }

//                        proximityContentAdapter.setNearbyContent(nearbyContent);
//                        proximityContentAdapter.notifyDataSetChanged();

                        return null;
                    }
                }).build();

        proximityObserverHandler = proximityObserver.startObserving(places);
    }

    /**
     * Funckja zatrzymująca obserwatora.
     */
    public void stop() {
        proximityObserverHandler.stop();
    }
}
