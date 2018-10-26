package com.michalraq.proximitylightapp.estimote;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class ProximityContentManager {

    private Context context;
    private ProximityContentAdapter proximityContentAdapter;
    private EstimoteCloudCredentials cloudCredentials;
    private ProximityObserver.Handler proximityObserverHandler;

    public ProximityContentManager(Context context, ProximityContentAdapter proximityContentAdapter, EstimoteCloudCredentials cloudCredentials) {
        this.context = context;
        this.proximityContentAdapter = proximityContentAdapter;
        this.cloudCredentials = cloudCredentials;
    }

    public void start() {
//TODO JAK ZROBIC AKCJE WEJSCIA WYJSCIA
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
                .inCustomRange(3.5)
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
            @Override
            public Unit invoke(ProximityZoneContext zoneContext) {
                String place = zoneContext.getAttachments().get("place");
                if (place == null) {
                    place = "unknown";
                }
                Toast.makeText(context, "Włączam światło w " + place, Toast.LENGTH_LONG).show();
                return null;
            }
        }) .onExit(new Function1<ProximityZoneContext, Unit>() {
            @Override
            public Unit invoke(ProximityZoneContext zoneContext) {
                String place = zoneContext.getAttachments().get("place");
                if (place == null) {
                    place = "unknown";
                }
                Toast.makeText(context, "Wyłączam światło w " + place, Toast.LENGTH_LONG).show();


                proximityContentAdapter.notifyDataSetChanged();

                return null;
            }
        }).build();



        ProximityZone zone = new ProximityZoneBuilder()
                .forTag("proximity-light-4nu")
                .inCustomRange(3.5)
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

                        proximityContentAdapter.setNearbyContent(nearbyContent);
                        proximityContentAdapter.notifyDataSetChanged();

                        return null;
                    }
                }).build();

        proximityObserverHandler = proximityObserver.startObserving(places);
    }

    public void stop() {
        proximityObserverHandler.stop();
    }
}
